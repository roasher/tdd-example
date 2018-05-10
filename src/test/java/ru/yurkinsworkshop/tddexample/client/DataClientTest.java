package ru.yurkinsworkshop.tddexample.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.yurkinsworkshop.tddexample.configuration.data.DataConfig;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class DataClientTest {

    private DataClient dataClient;

    @Mock
    private DataConfig dataConfig;
    private RestTemplate dataRestTemplate;

    private MockRestServiceServer server;

    @Before
    public void init() {
        when(dataConfig.getUrl()).thenReturn("/data-url");

        dataRestTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(dataRestTemplate);

        dataClient = new DataClient(dataRestTemplate, dataConfig);
    }

    @Test
    public void sendNotification() {
        server.expect(requestTo("/data-url"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(
                        // language=JSON
                        "{\"productId\":111,\"available\":false}")
                )
                .andRespond(withSuccess());

        dataClient.notify(new ProductAvailability(111L, false));

        server.verify();
    }

}