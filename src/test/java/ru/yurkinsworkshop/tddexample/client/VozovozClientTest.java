package ru.yurkinsworkshop.tddexample.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.yurkinsworkshop.tddexample.configuration.vozovoz.VozovozConfig;
import ru.yurkinsworkshop.tddexample.configuration.vozovoz.VozovozConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class VozovozClientTest {

    private VozovozClient vozovozClient;

    @Mock
    private VozovozConfig vozovozConfig;
    private RestTemplate vozovozRestTemplate;

    private MockRestServiceServer server;


    @Before
    public void init() {
        vozovozRestTemplate = new VozovozConfiguration().vozovozRestTemplate();
        vozovozClient = new VozovozClient(vozovozRestTemplate, vozovozConfig);

        when(vozovozConfig.getEndpoint()).thenReturn("/isDeliveryAvailable?productId={productId}");
        server = MockRestServiceServer.createServer(vozovozRestTemplate);
    }

    @Test
    public void returnTrueIfVozovozReturnedTrue() {
        server.expect(requestTo("/isDeliveryAvailable?productId=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));

        assertThat(vozovozClient.isAvailableForTransportation(200L), is(true));
        server.verify();
    }

    @Test
    public void returnFalseIfVozovozReturnedFalse() {
        server.expect(requestTo("/isDeliveryAvailable?productId=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThat(vozovozClient.isAvailableForTransportation(200L), is(false));
        server.verify();
    }

    @Test
    public void returnFalseIfVozovozReturnedCrap() {
        server.expect(requestTo("/isDeliveryAvailable?productId=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Sorry, we can't deliver this item", MediaType.TEXT_PLAIN));

        assertThat(vozovozClient.isAvailableForTransportation(200L), is(false));
        server.verify();
    }

}