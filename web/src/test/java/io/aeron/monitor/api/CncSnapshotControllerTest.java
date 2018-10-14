package io.aeron.monitor.api;

import static io.aeron.CncFileDescriptor.CNC_VERSION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.monitor.api.RestApiControllerV1;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@Disabled
public class CncSnapshotControllerTest {

    @Autowired
    private RestApiControllerV1 controller;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    private static MediaDriver.Context context;
    private static MediaDriver mediaDriver;

    @Test
    public void smoke() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void shouldListenOnPort() {
        String url = "http://localhost:" + port;
        String httpResponse = testRestTemplate.getForObject(url, String.class);
        assertThat(httpResponse).isNotBlank();
    }

    @Test
    public void shouldReturnCnCVersion() {
        String url = "http://localhost:" + port + "/api/v1/cnc/version";
        ResponseEntity<Integer> ret = testRestTemplate.getForEntity(url, Integer.class);
        assertThat(ret.getBody()).isEqualTo(CNC_VERSION);
    }

    @BeforeAll
    public static void startMediaDriver() {
        context = new MediaDriver.Context();
        context.threadingMode(ThreadingMode.SHARED);
        mediaDriver = MediaDriver.launchEmbedded(context);

    }

    @AfterAll
    public static void stopMediaDriver() {
        mediaDriver.close();
        context.deleteAeronDirectory();
    }
}
