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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/builder")
public class FileBuilderServer {

  /* TODO Create server with corresponding API to send to UI Incremental File Prefix, File and Field types and then let
      the UI run file creation passing to the server baseDirectory, FilePrefix, IncrementalFilePrefixType, FileSuffix,
      Filetype, number of rows (aka in the UI as number of entities per file) and list of fields for each row. The UI
      has to create the HTML with the list of types taking into account the metadata it will get from the server.
      Server can be created with jersey
  */

  @GET
  @Path("/v1/status/job/{jobId}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getStatus(@PathParam("jobId") String jobId) {
    return Response.status(Response.Status.OK)
                                .type(MediaType.TEXT_PLAIN_TYPE)
                                .entity(String.format("job %s status is: TODO", jobId))
                                .build();
  }

  @POST
  @Path("/v1/files/create")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createFiles(CreateFilesQueryObject createFilesQueryObject) {
    return Response.status(Response.Status.ACCEPTED)
                   .type(MediaType.APPLICATION_JSON)
                   .entity(createFilesQueryObject)
                   .build();
  }

}
