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
import com.xba.file.server.response.JobStatus;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.Future;

@Path("/builder")
public class FileBuilderController {

  @GET
  @Path("/v1/status/job/{jobId}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getStatus(@PathParam("jobId") String jobId) {
    FileBuilderExecutor fileBuilderExecutorInstance = FileBuilderExecutor.getInstance();
    JobStatus jobStatus;
    Map<String, FileBuilderWorker.FileBuilderWorkerResult> results = fileBuilderExecutorInstance.results;
    if (results.containsKey(jobId)) {
      FileBuilderWorker.FileBuilderWorkerResult result = results.get(jobId);
      results.remove(jobId);
      jobStatus = new JobStatus(jobId, result.getCreatedFiles(), result.getErrorFiles());
    } else {
      jobStatus = new JobStatus(jobId, 0, 0); // job hasn't finished yet // TODO modify code to allow partial status
    }
    return Response.status(Response.Status.OK)
                                .type(MediaType.APPLICATION_JSON)
                                .entity(jobStatus)
                                .build();
  }

  @POST
  @Path("/v1/files/create")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createFiles(CreateFilesQueryObject createFilesQueryObject) {
    FileBuilderExecutor fileBuilderExecutorInstance = FileBuilderExecutor.getInstance();
    fileBuilderExecutorInstance.createFilesRequestsQueue.add(createFilesQueryObject);
    return Response.status(Response.Status.ACCEPTED)
                   .type(MediaType.APPLICATION_JSON)
                   .entity(0) // TODO modify code to return the correct id. This requires id to be assigned here to the job
                   .build();
  }

}
