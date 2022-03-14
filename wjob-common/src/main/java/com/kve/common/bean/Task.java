package com.kve.common.bean;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: hujing39
 * @date: 2022-03-14
 */
@Slf4j
@DisallowConcurrentExecution
public class Task implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap();
        String jarPath = map.getString("jarPath"); //jar执行路径
        String parameter = map.getString("parameter");
        String vmParam = map.getString("vmParam");

        log.info("Running Job name : {}", map.get("name"));
        log.info("Running Job description : {}", map.get("jobDescription"));
        log.info("Running Job group : {}", map.get("group"));
        log.info("Running Job cron : {}", map.get("cronExpression"));
        log.info("Running Job jar path : {}", jarPath);
        log.info("Running Job jar parameter : {}", parameter);
        log.info("Running Job jar vmParam : {}", vmParam);

        long startTime = System.currentTimeMillis();
        if (!StringUtils.isEmpty(jarPath)) {
            File jar = new File(jarPath);
            if (jar.exists()) {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(jar.getParentFile());
                List<String> commands = new ArrayList<>(); //命令
                commands.add("java");
                if (!StringUtils.isEmpty(vmParam)) {
                    commands.add(vmParam);
                }
                commands.add("-jar");
                commands.add(jarPath);
                if (!StringUtils.isEmpty(parameter)) {
                    commands.add(parameter);
                }
                log.info("Running Job details as Followed >>>>>>>>>>>");
                log.info("Running Job commands : {}", commands.toString());

                try {
                    Process process = processBuilder.start(); //执行命令
                    logProcess(process.getInputStream(), process.getErrorStream()); //具体任务执行日志打印
                } catch (IOException e) {
                    throw new JobExecutionException(e);
                }
            }
            else {
                throw new JobExecutionException("Job jar not found >> " + jarPath);
            }
        }
        long endTime = System.currentTimeMillis();
        log.info(">>>>>>>>>>> Running Job has been completed, cost time : " +
                (endTime - startTime) + "/ms" + "\n");

    }

    //打印Job执行内容的日志
    private void logProcess(InputStream inputStream, InputStream errorStream) throws IOException {
        String inputLine;
        String errorLine;
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        while ((inputLine = inputReader.readLine()) != null) log.info(inputLine);
        while ((errorLine = errorReader.readLine()) != null) log.error(errorLine);
    }
}
