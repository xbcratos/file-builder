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

package com.xba.file.server.rest.response;

import com.google.gson.Gson;

public class JobStatusResponse {

  String jobId;
  int createdFiles;
  int errorFiles;
  String errorMessage;

  public JobStatusResponse() {

  }

  public JobStatusResponse(String jobId, int createdFiles, int errorFiles, String errorMessage) {
    this.jobId = jobId;
    this.createdFiles = createdFiles;
    this.errorFiles = errorFiles;
    this.errorMessage = errorMessage;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public int getCreatedFiles() {
    return createdFiles;
  }

  public void setCreatedFiles(int createdFiles) {
    this.createdFiles = createdFiles;
  }

  public int getErrorFiles() {
    return errorFiles;
  }

  public void setErrorFiles(int errorFiles) {
    this.errorFiles = errorFiles;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
