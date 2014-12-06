package com.example.wochacha.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServiceExecutor {

	private static ServiceExecutor instance;

	private ExecutorService services = Executors.newFixedThreadPool(3);
	HashMap<DataServiceImpl, Future<?>> pendingTask = new HashMap<>();

	public synchronized static ServiceExecutor getInstance() {
		if (instance == null) {
			instance = new ServiceExecutor();
		}
		return instance;
	}

	public void shutdown() {
		Iterator<Future<?>> iterator = pendingTask.values().iterator();
		while (iterator.hasNext()) {
			Future<?> task = (Future<?>)iterator.next();
			task.cancel(true);
		}
		pendingTask.clear();
		
		services.shutdown();
	}

	public void execute(DataServiceImpl dataServiceImpl) {
		Future<?> task = services.submit(dataServiceImpl);		
		pendingTask.put(dataServiceImpl, task);
	}

	public void done(DataServiceImpl impl) {
		pendingTask.remove(impl);
	}

	public void cancel(DataServiceImpl impl) {
		Future<?> task = pendingTask.get(impl);

		if (task == null || task.isCancelled() || task.isDone()) {
			pendingTask.remove(impl);
			return;
		}
		task.cancel(true);
		pendingTask.remove(impl);
	}

}
