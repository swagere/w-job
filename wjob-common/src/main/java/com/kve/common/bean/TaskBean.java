package com.kve.common.bean;

import com.kve.common.config.ApplicationContextHelper;
import com.kve.common.mapper.TaskEntityMapper;
import com.kve.common.util.ParamUtil;
import com.kve.common.util.RandomUtils;
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
@DisallowConcurrentExecution
public class TaskBean implements Job {
    private static Logger log = LoggerFactory.getLogger(TaskBean.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap();
        //任务ID
        Integer jobId = (Integer) map.get("jobId");
        //项目名称
        String appName = map.getString("appName");
        //目标类名
        String targetClass = map.getString("jobClass");
        //目标方法
        String targetMethod = map.getString("jobMethod");
        //方法参数
        String methodArgs = map.getString("methodArgs");

        if (StringUtils.isEmpty(targetClass) || StringUtils.isEmpty(targetMethod)) {
            throw new JobExecutionException("缺少执行类信息");
        }

        long startTime = System.currentTimeMillis();
        try {
            //任务日志标识
            MDC.put("logId", RandomUtils.randomAlphanumeric(15));
            log.info("[ JobDetail ] >> job start jobId:{} , targetClass:{} ,targetMethod:{} , methodArgs:{}", jobId, targetClass, targetMethod, methodArgs);
            //任务参数处理
            Object[] jobArs = ParamUtil.getJobArgs(methodArgs);

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
            log.error("[ JobDetail ] >> job execute exception jobId:{} , targetClass:{} ,targetMethod:{} , methodArgs:{}"
                    , jobId, targetClass, targetMethod, methodArgs, e);
            throw new JobExecutionException(e);
        }

        this.updateAfterRun(jobId, appName);

        log.info("[ JobDetail ] >> job end jobId:{} , targetClass:{} ,targetMethod:{} , methodArgs:{} , time:{} ms"
                , jobId, targetClass, targetMethod, methodArgs, (System.currentTimeMillis() - startTime));

    }

    private void updateAfterRun(Integer jobId, String appName) {
        try {
            TaskEntityMapper taskEntityMapper = ApplicationContextHelper.getApplicationContext().getBean(TaskEntityMapper.class);
            //更新最后执行时间
            TaskEntity task = new TaskEntity();
            task.setId(jobId);
            task.setAppName(appName);
            task.setLastRunTimestamp(System.currentTimeMillis());
            taskEntityMapper.updateByAppNameAndId(task);
        } catch (Exception e) {
            log.error("[ JobDetail ] >> job updateAfterRun exception jobId:{} , projectKey:{} ", jobId, appName, e);
        }
    }
}
