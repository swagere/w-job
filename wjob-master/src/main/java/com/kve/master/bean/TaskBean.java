package com.kve.master.bean;

import com.kve.master.config.ApplicationContextHelper;
import com.kve.master.mapper.TaskInfoMapper;
import com.kve.master.util.ParamUtil;
import com.kve.master.util.RandomUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 定时任务 核心Bean
 * @author: hujing39
 * @date: 2022-03-14
 */
//@DisallowConcurrentExecution
public class TaskBean implements Job {
    private static Logger log = LoggerFactory.getLogger(TaskBean.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap();
        //任务ID
        Integer triggerId = (Integer) map.get("triggerId");
        //目标类名
        String targetClass = map.getString("targetClass");
        //目标方法
        String targetMethod = map.getString("targetMethod");
        //方法参数
        String targetArguments = map.getString("targetArguments");

        if (StringUtils.isEmpty(targetClass) || StringUtils.isEmpty(targetMethod)) {
            throw new JobExecutionException("缺少执行类信息");
        }

        long startTime = System.currentTimeMillis();
        try {
            //任务日志标识
            MDC.put("logId", RandomUtils.randomAlphanumeric(15));
            log.info("[ JobDetail ] >> trigger start triggerId:{} , targetClass:{} ,targetMethod:{} , methodArgs:{}", triggerId, targetClass, targetMethod, targetArguments);
            //任务参数处理
            Object[] jobArs = ParamUtil.getJobArgs(targetArguments);

            //目标类处理
            Object target = ApplicationContextHelper.getApplicationContext().getBean(targetClass); //从ApplicationContext中获取到spring管理的bean
            if (target == null) {
                throw new JobExecutionException("无法寻找到目标类");
            }
            Class tc = target.getClass();
            Class[] parameterType = ParamUtil.getParameters(jobArs); //获取类定义的参数列表

            //执行任务方法
            Method method = tc.getDeclaredMethod(targetMethod, parameterType);
            if (method == null) {
                throw new JobExecutionException("无法寻找到目标方法");
            }
            method.invoke(target, jobArs);
        } catch (Exception e) {
            log.error("[ JobDetail ] >> trigger execute exception triggerId:{} , targetClass:{} ,targetMethod:{} , methodArgs:{}"
                    , triggerId, targetClass, targetMethod, targetArguments, e);
            throw new JobExecutionException(e);
        }

        this.updateAfterRun(triggerId);

        log.info("[ JobDetail ] >> trigger end triggerId:{} , targetClass:{} ,targetMethod:{} , methodArgs:{} , time:{} ms"
                , triggerId, targetClass, targetMethod, targetArguments, (System.currentTimeMillis() - startTime));

    }

    private void updateAfterRun(Integer jobId) {
        try {
            TaskInfoMapper taskInfoMapper = ApplicationContextHelper.getApplicationContext().getBean(TaskInfoMapper.class);
            //更新最后执行时间
            TaskInfo task = new TaskInfo();
            task.setId(jobId);
            task.setLastRunTimestamp(System.currentTimeMillis());
            taskInfoMapper.updateById(task);
        } catch (Exception e) {
            log.error("[ JobDetail ] >> job updateAfterRun exception jobId:{} , projectKey:{} ", jobId, e);
        }
    }
}
