package com.kve.worker.router.thread;

import com.kve.common.config.ApplicationContextHelper;
import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.ParamUtil;
import lombok.extern.slf4j.Slf4j;
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

    private String action;

    private String triggerKey;

    private static CallBackThread callBackThread;

    static {
        callBackThread = ApplicationContextHelper.getBean(CallBackThread.class);
    }

    public TaskThread(String triggerKey) {
        this.triggerKey = triggerKey;
        this.triggerQueue = new LinkedBlockingQueue<RequestModel>();
    }

    public void pushTriggerQueue(RequestModel requestModel) {
        triggerQueue.add(requestModel);
    }

    @Override
    public void run() {
        while (true) {
            //开始调度
            try {
                RequestModel requestModel = triggerQueue.poll(3L, TimeUnit.SECONDS);
                if (requestModel != null) {
                    //-----日志处理


                    //-----任务执行
                    try {
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
                        method.invoke(target, jobArs);

                        requestModel.setStatus(ResponseModel.SUCCESS);
                        requestModel.setMsg(null);
                    } catch (Exception e) {
                        requestModel.setStatus(ResponseModel.FAIL);
                        StringWriter out = new StringWriter();
                        e.printStackTrace(new PrintWriter(out));
                        requestModel.setMsg(out.toString());
                    }

//                    callBackThread.pushCallBask(requestModel);
                }
            } catch (Exception e) {
                log.error("[TaskThread] run exception:", e);
            }
        }

        //未开始调度，仍处于调度队列

    }
}
