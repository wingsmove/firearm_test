package com.firearm.simulator.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimulatorSessionIsolationTest {

    @LocalServerPort
    private int port;

    @Test
    void browserSessionsHaveIndependentSimulatorState() throws Exception {
        HttpClient firstBrowser = browser();
        HttpClient secondBrowser = browser();

        assertEquals(200, get(firstBrowser).statusCode());
        assertEquals(200, get(secondBrowser).statusCode());

        HttpResponse<String> cycled = firstBrowser.send(
                HttpRequest.newBuilder(endpoint("/api/simulator/actions/CYCLE"))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, cycled.statusCode());
        assertTrue(cycled.body().contains("\"chamber\":{\"state\":\"LOADED\""));

        HttpResponse<String> untouched = get(secondBrowser);
        assertEquals(200, untouched.statusCode());
        assertTrue(untouched.body().contains("\"chamber\":{\"state\":\"EMPTY\""));
    }

    private HttpClient browser() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
    }

    private HttpResponse<String> get(HttpClient client) throws Exception {
        return client.send(
                HttpRequest.newBuilder(endpoint("/api/simulator")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private URI endpoint(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
