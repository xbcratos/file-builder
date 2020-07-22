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

import com.xba.file.server.query.CreateFilesQueryObject;

import java.util.UUID;

public class Job {

  private UUID jobId;
  private CreateFilesQueryObject createFilesQueryObject;

  public Job(UUID jobId, CreateFilesQueryObject createFilesQueryObject) {
    this.jobId = jobId;
    this.createFilesQueryObject = createFilesQueryObject;
  }

  public UUID getJobId() {
    return jobId;
  }

  public CreateFilesQueryObject getCreateFilesQueryObject() {
    return createFilesQueryObject;
  }
}
