/*
 * Copyright 2020 Xavier Baques
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xba.file.server;

import com.xba.file.common.Field;
import com.xba.file.common.FileNameIncrementalType;
import com.xba.file.common.FileType;
import com.xba.file.server.query.CreateFilesQueryObject;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileBuilderExecutor implements Runnable {
  // TODO: This code is not well designed. Think about it to correct it.
  //  NOTE that I can access this object through different threads no matter if the object itself
  //  is running a while loop but I have to take into account concurrency issues
  private static final int DEFAULT_NUM_THREADS = 1;
  private static final long MAX_KEEP_RESULT_TIME_IN_MILLIS = 10*60*1000; //10 min * 60 sec/min * 1000 millis/sec

  private CompletionService<FileBuilderWorker.FileBuilderWorkerResult> completionService;
  private ScheduledThreadPoolExecutor executor;
  private Map<String, FileBuilderWorkerWrapper> workers;
  private int numThreads;
  private final Map<String, FileBuilderWorker.FileBuilderWorkerResult> results;
  private final Queue<Job> createFilesJobsQueue;
  private final AtomicBoolean stop;
  private final AtomicBoolean running;

  private static FileBuilderExecutor fileBuilderExecutorInstance = null;

  private FileBuilderExecutor() {
    this.numThreads = DEFAULT_NUM_THREADS;
    this.results = new ConcurrentHashMap<>();
    this.createFilesJobsQueue = new ConcurrentLinkedQueue<>();
    this.stop = new AtomicBoolean(false);
    this.running = new AtomicBoolean(false);
  }

  public static FileBuilderExecutor getInstance() {
    if (fileBuilderExecutorInstance == null) {
      fileBuilderExecutorInstance = new FileBuilderExecutor();
      fileBuilderExecutorInstance.init();
    }

    return fileBuilderExecutorInstance;
  }

  public int getNumThreads() {
    return numThreads;
  }

  public void setNumThreads(int numThreads) {
    this.numThreads = numThreads;
  }

  public Map<String, FileBuilderWorker.FileBuilderWorkerResult> getResults() {
    return results;
  }

  public Map<String, FileBuilderWorkerWrapper> getWorkers() {
    return workers;
  }

  public String addCreateFilesQuery(CreateFilesQueryObject createFilesQueryObject) {
    UUID jobId = UUID.randomUUID();
    while (workers.containsKey(jobId.toString())) {
      jobId = UUID.randomUUID();
    }
    createFilesJobsQueue.add(new Job(jobId, createFilesQueryObject));

    return jobId.toString();
  }

  public void init() {
    if (!results.isEmpty()) {
      results.clear();
    }

    if (!createFilesJobsQueue.isEmpty()) {
      createFilesJobsQueue.clear();
    }

    stop.set(false);
    running.set(false);

    this.workers = new ConcurrentHashMap<>();
    this.executor = new ScheduledThreadPoolExecutor(Math.max(1, numThreads));
    this.completionService = new ExecutorCompletionService<>(executor);
  }

  public void stop() {
    stop.set(true);
  }

  public void destroy() {
    if (this.executor != null) {
      this.executor.shutdown();
      this.executor = null;
    }

    fileBuilderExecutorInstance = null;
  }

  @Override
  public void run() {
    running.set(true);
    while (!stop.get()) {
      // Delete expired results
      results.forEach((jobId, fileBuilderWorkerResult) -> {
        long currentTime = System.currentTimeMillis();
        if (fileBuilderWorkerResult.getCreatedTime() - currentTime > MAX_KEEP_RESULT_TIME_IN_MILLIS) {
          results.remove(jobId);
        }
      });

      // Check finished jobs and set results
      workers.forEach((jobId, workerWrapper) -> {
        Future<FileBuilderWorker.FileBuilderWorkerResult> workerFuture = workerWrapper.getWorkerFuture();
        if (workerFuture.isCancelled()) {
          workers.remove(jobId);
        }
        else if (workerFuture.isDone()) {
          try {
            FileBuilderWorker.FileBuilderWorkerResult fileBuilderWorkerResult = workerFuture.get();
            results.put(jobId, fileBuilderWorkerResult);
            workers.remove(jobId);
          } catch (InterruptedException e) {
            // TODO add corresponding error handling and/or logging
            e.printStackTrace();
          } catch (ExecutionException e) {
            // TODO add corresponding error handling and/or logging
            e.printStackTrace();
          }
        }
      });

      // Check for new jobs
      while (!createFilesJobsQueue.isEmpty()) {
        Job nextJob = createFilesJobsQueue.poll();
        if (nextJob != null) {
          CreateFilesQueryObject createFilesQueryObject = nextJob.getCreateFilesQueryObject();
          createFiles(
              createFilesQueryObject.getBaseDirectory(),
              createFilesQueryObject.getNamePrefix(),
              createFilesQueryObject.getNameSuffix(),
              createFilesQueryObject.getFileNameIncrementalType(),
              createFilesQueryObject.getFields(),
              createFilesQueryObject.getFileType(),
              createFilesQueryObject.getNumRows(),
              createFilesQueryObject.getNumFiles(),
              nextJob.getJobId()
          );
        }
      }
    }
    running.set(false);
  }

  public void createFiles(
      String baseDirectory,
      String namePrefix,
      String nameSuffix,
      FileNameIncrementalType fileNameIncrementalType,
      List<Field> fields,
      FileType fileType,
      int numRows,
      int numFiles,
      UUID jobId
  ) {
    FileBuilderWorker worker = new FileBuilderWorker(baseDirectory,
        namePrefix,
        nameSuffix,
        fileNameIncrementalType,
        fields,
        fileType,
        numRows,
        numFiles,
        jobId
    );

    Future<FileBuilderWorker.FileBuilderWorkerResult> workerFuture = completionService.submit(worker);
    workers.put(jobId.toString(), new FileBuilderWorkerWrapper(worker, workerFuture));
  }
}
