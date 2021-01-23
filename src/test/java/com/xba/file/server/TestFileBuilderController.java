package com.xba.file.server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.xba.file.common.Field;
import com.xba.file.common.FieldType;
import com.xba.file.common.FileNameIncrementalType;
import com.xba.file.common.FileType;
import com.xba.file.server.rest.query.CreateFilesQuery;
import com.xba.file.server.rest.response.CreateFilesResponse;
import com.xba.file.server.rest.response.JobStatusResponse;
import org.glassfish.grizzly.http.server.HttpServer;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestFileBuilderController {

  private HttpServer server;
  private WebTarget target;

  @Before
  public void setUp() throws Exception {
    // start the server
    server = Main.startServer();
    // create the client
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
    clientConfig.register(new LoggingFilter());
    Client c = ClientBuilder.newClient(clientConfig);

    // uncomment the following line if you want to enable
    // support for JSON in the client (you also have to uncomment
    // dependency on jersey-media-json module in pom.xml and Main.startServer())
    // --
    // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

    target = c.target(Main.BASE_URI);
  }

  @After
  public void tearDown() throws Exception {
    server.stop();
  }

  /**
   * Test to see that the message "Got it!" is sent in the response.
   */
  @Test
  public void testGetStatus() {
    JobStatusResponse responseJobStatusResponse = target.path("/builder/v1/status/job/0")
                                                        .request()
                                                        .get(JobStatusResponse.class);
    assertEquals("0", responseJobStatusResponse.getJobId());
    assertEquals("Job not found", responseJobStatusResponse.getErrorMessage());
  }

  @Test
  public void testCreateFiles() {
    CreateFilesQuery createFilesQuery = getDefaultCreateFilesQueryObject();
    Response response = target.path("/builder/v1/files/create")
                              .request()
                              .post(Entity.entity(createFilesQuery, MediaType.APPLICATION_JSON_TYPE));
    Assert.assertEquals("Incorrect Response Message", "Accepted", response.getStatusInfo().getReasonPhrase());
    Assert.assertEquals("Incorrect Response Status", 202, response.getStatus());
    CreateFilesResponse createFilesResponse = response.readEntity(CreateFilesResponse.class);
    assertNull(createFilesResponse.getErrorMessage());
    assertTrue(
        "create files response job id is -1 which is only used to indicate an error happened",
        !"-1".equals(createFilesResponse.getJobId())
    );
  }

  private CreateFilesQuery getDefaultCreateFilesQueryObject() {
    CreateFilesQuery createFilesQuery = new CreateFilesQuery();

    createFilesQuery.setBaseDirectory("/tmp");
    createFilesQuery.setNamePrefix("namePrefix");
    createFilesQuery.setNameSuffix("nameSuffix");
    createFilesQuery.setFileNameIncrementalType(FileNameIncrementalType.COUNTER);
    createFilesQuery.setFields(getDefaultFields());
    createFilesQuery.setFileType(FileType.CSV);
    createFilesQuery.setNumRows(5);
    createFilesQuery.setNumFiles(10);

    return createFilesQuery;
  }

  private List<Field> getDefaultFields() {
    List<Field> fields = new ArrayList<>();
    fields.add(new Field("fieldName", FieldType.STRING, "fieldValue"));
    return fields;
  }

  private class LoggingFilter implements ClientRequestFilter {
    private final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
      LOG.log(Level.INFO,
          "Request: " + requestContext != null ? requestContext.getEntity() != null
                                                 ? requestContext.getEntity()
                                                                 .toString()
                                                 : "Request Context Entity is null" : "Request context is null"
      );
    }
  }

}