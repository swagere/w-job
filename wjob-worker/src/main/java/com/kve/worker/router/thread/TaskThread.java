package com.kve.worker.router.thread;

import com.kve.common.config.ApplicationContextHelper;
import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.ParamUtil;
import com.kve.worker.log.FileAppender;
import com.kve.worker.model.ExecutorStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TaskThread extends Thread {
    //存放运行请求的阻塞队列
    private LinkedBlockingQueue<RequestModel> triggerQueue;
    private ConcurrentHashSet<Integer> triggerLogIdSet;		// avoid repeat trigger for the same TRIGGER_LOG_ID


    private int status;

    private String triggerKey;

    private static CallBackThread callBackThread;

    static {
        callBackThread = ApplicationContextHelper.getBean(CallBackThread.class);
    }

    public TaskThread(String triggerKey) {
        this.triggerKey = triggerKey;
        this.triggerQueue = new LinkedBlockingQueue<RequestModel>();
        this.triggerLogIdSet = new ConcurrentHashSet<Integer>();
    }

    public void pushTriggerQueue(RequestModel requestModel) {
        if (triggerLogIdSet.contains(requestModel.getScheduleLogId())) {
            log.info("[ TaskThread ] add repeat, scheduleId:{}", requestModel.getScheduleLogId());
            return;
        }
        triggerQueue.add(requestModel);
        triggerLogIdSet.add(requestModel.getScheduleLogId());
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void stopTask() {
        status = ExecutorStatusEnum.STOP.getValue();
    }

    public void pauseTask() {
        status = ExecutorStatusEnum.PAUSE.getValue();
    }

    @Override
    public void run() {
        while (this.status == ExecutorStatusEnum.RUN.getValue()) {
            //开始调度
            try {
                RequestModel requestModel = triggerQueue.poll(3L, TimeUnit.SECONDS);
                if (requestModel != null) {
                    //-----日志处理
                    triggerLogIdSet.remove(requestModel.getScheduleLogId());

                    //-----任务执行
                    try {
                        FileAppender.contextHolder.set(String.valueOf(requestModel.getScheduleLogId()));

                        String targetClass = requestModel.getTargetClass();
                        String targetMethod = requestModel.getTargetMethod();
                        String targetArguments = requestModel.getTargetArguments();
                        if (StringUtils.isEmpty(targetClass) || StringUtils.isEmpty(targetMethod)) {
                            throw new Exception("缺少执行类信息");
                        }

                        long startTime = System.currentTimeMillis();
                        //任务参数处理
                        Object[] jobArs = ParamUtil.getJobArgs(targetArguments);

                        //目标类处理
                        Object target = ApplicationContextHelper.getApplicationContext().getBean(targetClass); //从ApplicationContext中获取到spring管理的bean
                        if (target == null) {
                            throw new Exception("无法寻找到目标类");
                        }
                        Class tc = target.getClass();
                        Class[] parameterType = ParamUtil.getParameters(jobArs); //获取类定义的参数列表

                        //执行任务方法
                        Method method = tc.getDeclaredMethod(targetMethod, parameterType);
                        if (method == null) {
                            throw new Exception("无法寻找到目标方法");
                        }
                        log.info("[ TaskThread ] task run");
                        method.invoke(target, jobArs);
                    } catch (Exception e) {
                        requestModel.setStatus(ResponseModel.FAIL);
                        StringWriter out = new StringWriter();
                        e.printStackTrace(new PrintWriter(out));
                        requestModel.setMsg(out.toString());
                        callBackThread.pushCallBask(requestModel);
                    }

                    //一次任务运行结束后回调
                    if (status == ExecutorStatusEnum.RUN.getValue()) {
                        requestModel.setStatus(ResponseModel.SUCCESS);
                        requestModel.setMsg("任务运行成功");
                        callBackThread.pushCallBask(requestModel);
                        log.info("[ TaskThread ] task end");
                    }
                    else if (status == ExecutorStatusEnum.PAUSE.getValue()) {
                        requestModel.setStatus(ResponseModel.FAIL);
                        requestModel.setMsg("任务暂停");
                        callBackThread.pushCallBask(requestModel);
                        log.info("[ TaskThread ] task pause");
                    }
                    else if (status == ExecutorStatusEnum.STOP.getValue()){
                        requestModel.setStatus(ResponseModel.FAIL);
                        requestModel.setMsg("任务被终止，停止调度运行");
                        callBackThread.pushCallBask(requestModel);
                        log.info("[ TaskThread ] task stop");
                    }

                }
            } catch (Exception e) {
                log.error("[ TaskThread ] run exception:", e);
            }
        }

        //在任务调度队列中被终止
        while (triggerQueue != null && triggerQueue.size() > 0) {
            RequestModel requestModel = triggerQueue.poll();
            if (requestModel != null) {
                requestModel.setStatus(ResponseModel.FAIL);
                requestModel.setMsg("任务尚未执行，在调度队列中被终止");
                callBackThread.pushCallBask(requestModel);
            }
        }

        log.info("[ TaskThread ] task stop, hashCode:{}", Thread.currentThread());

    }
}
