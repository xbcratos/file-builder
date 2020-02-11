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
import com.xba.file.server.query.CreateFilesQueryObject;
import org.glassfish.grizzly.http.server.HttpServer;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class TestFileBuilderServer {

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
    String responseMsg = target.path("/builder/v1/status/job/0")
                               .request()
                               .get(String.class);
    assertEquals("job 0 status is: TODO", responseMsg);
  }

  @Test
  public void testCreateFiles() {
    CreateFilesQueryObject createFilesQueryObject = getDefaultCreateFilesQueryObject();
    Response response = target.path("/builder/v1/files/create")
                              .request()
                              .post(Entity.entity(createFilesQueryObject, MediaType.APPLICATION_JSON_TYPE));
    System.out.println("Create Files Response Message: " + response.getStatusInfo().getReasonPhrase());
    System.out.println("Create Files Response Status: " + response.getStatus());
    System.out.println("Create Files Response Entity: " + response.getEntity().toString());
  }

  private CreateFilesQueryObject getDefaultCreateFilesQueryObject() {
    CreateFilesQueryObject createFilesQueryObject = new CreateFilesQueryObject();

    createFilesQueryObject.setBaseDirectory("/temp");
    createFilesQueryObject.setNamePrefix("namePrefix");
    createFilesQueryObject.setNameSuffix("nameSuffix");
    createFilesQueryObject.setFileNameIncrementalType(FileNameIncrementalType.COUNTER);
    createFilesQueryObject.setFields(getDefaultFields());
    createFilesQueryObject.setFileType(FileType.CSV);
    createFilesQueryObject.setNumRows(5);
    createFilesQueryObject.setNumFiles(10);

    return createFilesQueryObject;
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
      LOG.log(Level.INFO, requestContext.getEntity().toString());
    }
  }

}