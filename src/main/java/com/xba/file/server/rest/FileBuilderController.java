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

package com.xba.file.server.rest;

import com.xba.file.server.FileBuilderExecutor;
import com.xba.file.server.FileBuilderWorker;
import com.xba.file.server.FileBuilderWorkerWrapper;
import com.xba.file.server.rest.query.CreateFilesQuery;
import com.xba.file.server.rest.response.CreateFilesResponse;
import com.xba.file.server.rest.response.JobStatusResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/builder")
public class FileBuilderController {

  @GET
  @Path("/v1/status/job/{jobId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStatus(
      @PathParam("jobId")
          String jobId
  ) {
    FileBuilderExecutor fileBuilderExecutorInstance = FileBuilderExecutor.getInstance();
    JobStatusResponse jobStatusResponse;
    Map<String, FileBuilderWorker.FileBuilderWorkerResult> results = fileBuilderExecutorInstance.getResults();
    if (results.containsKey(jobId)) {
      FileBuilderWorker.FileBuilderWorkerResult result = results.get(jobId);
      results.remove(jobId);
      jobStatusResponse = new JobStatusResponse(jobId, result.getCreatedFiles(), result.getErrorFiles(), null);
    } else {
      Map<String, FileBuilderWorkerWrapper> workers = fileBuilderExecutorInstance.getWorkers();
      if (workers.containsKey(jobId)) {
        FileBuilderWorker worker = workers.get(jobId).getWorker();
        jobStatusResponse = new JobStatusResponse(
            jobId,
            worker.getCreatedFiles().intValue(),
            worker.getErrorFiles().intValue(),
            null
        );
      } else {
        return Response.status(Response.Status.OK)
                       .entity(new JobStatusResponse(jobId, -1, -1, "Job not found"))
                       .build();
      }
    }
    return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(jobStatusResponse).build();
  }

  @POST
  @Path("/v1/files/create")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createFiles(CreateFilesQuery createFilesQuery) {
    FileBuilderExecutor fileBuilderExecutorInstance = FileBuilderExecutor.getInstance();
    String jobId = fileBuilderExecutorInstance.addCreateFilesQuery(createFilesQuery);
    return Response.status(Response.Status.ACCEPTED).type(MediaType.APPLICATION_JSON).entity(new CreateFilesResponse(
        jobId,
        null
    )).build();
  }

}
