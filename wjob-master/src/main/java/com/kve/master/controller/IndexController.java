package com.kve.master.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.system.JavaRuntimeInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import com.kve.master.bean.enums.TaskStatusEnum;
import com.kve.master.bean.vo.HomeResultVO;
import com.kve.master.config.response.AjaxResponse;
import com.kve.master.mapper.TaskInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ThreadMXBean;
import java.util.Date;

@RestController
@RequestMapping("/job-admin/home")
public class IndexController {

    @Autowired
    TaskInfoMapper taskInfoMapper;

    @Autowired
    private Environment environment;

    /**
     * 首页统计
     *
     * @return 首页统计
     * @author mengq
     */
    @RequestMapping("/getHomeCount")
    public AjaxResponse getHomeCount() {
        return AjaxResponse.success(getHomeCountDetail());
    }

    /**
     * 主页统计与系统信息
     *
     * @return 主页统计与系统信息
     * @author mengq
     */
    public HomeResultVO getHomeCountDetail() {
        //项目key
        HomeResultVO result = new HomeResultVO();
        result.setCurrentTime(DateUtil.formatDateTime(new Date()));
        //总任务数
        result.setJobTotal(taskInfoMapper.countByStatus(null));
        // TODO: 2022/5/4 状态细分
        //已启动
        result.setEnableJobTotal(taskInfoMapper.countByStatus(TaskStatusEnum.CREATE.getValue()));
        //已停止
        result.setStopJobTotal(taskInfoMapper.countByStatus(TaskStatusEnum.FINISH_EXCEPTION.getValue()));
        //启动端口
        result.setPort(environment.getProperty("server.port"));
        //虚拟机启动时间
        result.setStartTime(DateUtil.formatDateTime(new Date(SystemUtil.getRuntimeMXBean().getStartTime())));
        //操作系统信息
        OsInfo osInfo = SystemUtil.getOsInfo();
        //操作系统名称
        result.setSystemName(osInfo.getName());
        //系统版本
        result.setSystemVersion(osInfo.getVersion());
        //系统用户
        result.setSystemUser(SystemUtil.getUserInfo().getName());
        //线程信息
        ThreadMXBean threadMxBean = SystemUtil.getThreadMXBean();
        result.setActiveThreadCount(threadMxBean.getThreadCount());
        result.setDaemonThreadCount(threadMxBean.getDaemonThreadCount());
        result.setPeakThreadCount(threadMxBean.getPeakThreadCount());
        //java运行时信息
        JavaRuntimeInfo javaRuntimeInfo = SystemUtil.getJavaRuntimeInfo();
        //java版本
        result.setJavaVersion(javaRuntimeInfo.getVersion());
        //系统运行时信息
        RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
        //总内存
        result.setMaxMemory(FileUtil.readableFileSize(runtimeInfo.getMaxMemory()));
        //总可用内存
        result.setFreeMemory(FileUtil.readableFileSize(runtimeInfo.getFreeMemory()));
        //总分配空间
        result.setTotalMemory(FileUtil.readableFileSize(runtimeInfo.getTotalMemory()));
        //已用内存
        result.setUsedMemory(FileUtil.readableFileSize(runtimeInfo.getUsableMemory()));
        return result;
    }
}
