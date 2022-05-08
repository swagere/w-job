package com.kve.worker.router;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.worker.router.action.ExecutorAction;
import com.kve.worker.router.action.RunAction;
import com.kve.worker.router.thread.TaskThread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class HandlerRouter {

	/**
	 * job handler repository
     */
//	private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();
//	public static IJobHandler registJobHandler(String name, IJobHandler jobHandler){
//		logger.info("xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
//		return HandlerRouter.jobHandlerRepository.put(name, jobHandler);
//	}
//	public static IJobHandler loadJobHandler(String name){
//		return HandlerRouter.jobHandlerRepository.get(name);
//	}

    /**
     * job thread repository
     */
    private static ConcurrentHashMap<String, TaskThread> TaskThreadRepository = new ConcurrentHashMap<String, TaskThread>();
	public static TaskThread registerTaskThread(String triggerKey){
        TaskThread taskThread = new TaskThread(triggerKey);
        taskThread.start();
		log.info("[ HandlerRouter ] executor register TaskThread success, triggerKey:{}", new Object[]{triggerKey});
		HandlerRouter.TaskThreadRepository.put(triggerKey, taskThread);	// putIfAbsent | oh my god, map's put method return the old value!!!
		return taskThread;
	}
	public static TaskThread loadJobThread(String jobKey){
		return HandlerRouter.TaskThreadRepository.get(jobKey);
	}

	/**
	 * route action repository
	 */
	public enum ActionRepository {
		RUN(new RunAction()),
//		KILL(new KillAction()),
//		LOG(new LogAction()),
//		BEAT(new BeatAction())
		;

		private ExecutorAction action;
		private ActionRepository(ExecutorAction action){
			this.action = action;
		}


		public static ExecutorAction matchAction(String name){
			if (name != null && name.trim().length() > 0) {
				for (ActionRepository item : ActionRepository.values()) {
					if (item.name().equals(name)) {
						return item.action;
					}
				}
			}
			return null;
		}

	}

	public static ResponseModel route(RequestModel requestModel) {
		log.info("[ HandlerRouter ] executor route; RequestModel:{}", new Object[]{requestModel.toString()});

		// timestamp check
		if (System.currentTimeMillis() - requestModel.getTimestamp() > 60000) {
			return new ResponseModel(ResponseModel.FAIL, "Timestamp Timeout.");
		}

		// match action
		ExecutorAction action = ActionRepository.matchAction(requestModel.getAction());
		if (action == null) {
			return new ResponseModel(ResponseModel.FAIL, "Action match fail.");
		}

		return action.execute(requestModel);
	}

}
