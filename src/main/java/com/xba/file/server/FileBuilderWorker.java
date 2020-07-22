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

import com.xba.file.builder.FileBuilder;
import com.xba.file.builder.FileBuilderFactory;
import com.xba.file.common.BaseFile;
import com.xba.file.common.Errors;
import com.xba.file.common.Field;
import com.xba.file.common.FileBuilderException;
import com.xba.file.common.FileNameIncrementalType;
import com.xba.file.common.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class FileBuilderWorker implements Callable {

  private static final Logger LOG = LoggerFactory.getLogger(FileBuilderWorker.class);

  private final String baseDirectory;
  private final String namePrefix;
  private final String nameSuffix;
  private final FileNameIncrementalType fileNameIncrementalType;
  private final List<Field> fields;
  private final FileType fileType;
  private final int numRows;
  private final int numFiles;

  private UUID jobId;
  private AtomicInteger createdFiles;
  private AtomicInteger errorFiles;
  private volatile boolean running;
  private volatile boolean jobFinished;

  public FileBuilderWorker(
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
    this.baseDirectory = baseDirectory;
    this.namePrefix = namePrefix;
    this.nameSuffix = nameSuffix;
    this.fileNameIncrementalType = fileNameIncrementalType;
    this.fields = fields;
    this.fileType = fileType;
    this.numRows = numRows;
    this.numFiles = numFiles;
    this.jobId = jobId;
    this.createdFiles = new AtomicInteger(0);
    this.errorFiles = new AtomicInteger(0);
    this.jobFinished = false;
  }

  public String getJobId() {
    return jobId.toString();
  }

  public AtomicInteger getCreatedFiles() {
    return createdFiles;
  }

  public AtomicInteger getErrorFiles() {
    return errorFiles;
  }

  public boolean isRunning() {
    return running;
  }

  public boolean isJobFinished() {
    return jobFinished;
  }

  @Override
  public Object call() throws Exception {
    this.running = true;
    try {
      if (numFiles > 0) {
        FileBuilder fileBuilder = FileBuilderFactory.create(
            baseDirectory,
            namePrefix,
            nameSuffix,
            fileNameIncrementalType,
            fields,
            fileType,
            numRows
        );
        for (int i = 0; i < numFiles; ++i) {
          BaseFile fileToCreate = fileBuilder.build();
          fileToCreate.create();
          createdFiles.incrementAndGet();
        }
      }
      jobFinished = true;
      running = false;
    } catch (FileBuilderException e) {
      errorFiles.incrementAndGet();
      LOG.error(Errors.FILE_BUILDER_03.getErrorMessage(), e.getMessage(), createdFiles, errorFiles);
      e.printStackTrace();
    }

    return new FileBuilderWorkerResult(createdFiles.intValue(), errorFiles.intValue(), System.currentTimeMillis());
  }

  public class FileBuilderWorkerResult {
    private final int createdFiles;
    private final int errorFiles;
    private final long createdTime;

    public FileBuilderWorkerResult(int createdFiles, int errorFiles, long createdTime) {
      this.createdFiles = createdFiles;
      this.errorFiles = errorFiles;
      this.createdTime = createdTime;
    }

    public int getCreatedFiles() {
      return createdFiles;
    }

    public int getErrorFiles() {
      return errorFiles;
    }

    public long getCreatedTime() {
      return createdTime;
    }
  }
}
