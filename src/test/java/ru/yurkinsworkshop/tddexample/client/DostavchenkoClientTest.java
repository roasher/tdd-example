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
import ru.yurkinsworkshop.tddexample.configuration.dostavchenko.DostavchenkoConfig;
import ru.yurkinsworkshop.tddexample.configuration.dostavchenko.DostavchenkoConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class DostavchenkoClientTest {

    private DostavchenkoClient dostavchenkoClient;

    @Mock
    private DostavchenkoConfig dostavchenkoConfig;
    private RestTemplate dostavchenkoRestTemplate;

    private MockRestServiceServer server;


    @Before
    public void init() {
        dostavchenkoRestTemplate = new DostavchenkoConfiguration().dostavchenkoRestTemplate();
        dostavchenkoClient = new DostavchenkoClient(dostavchenkoRestTemplate, dostavchenkoConfig);

        when(dostavchenkoConfig.getEndpoint()).thenReturn("/isDeliveryAvailable?productId={productId}");
        server = MockRestServiceServer.createServer(dostavchenkoRestTemplate);
    }

    @Test
    public void returnTrueIfDostavchenkoReturnedTrue() {
        server.expect(requestTo("/isDeliveryAvailable?productId=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));

        assertThat(dostavchenkoClient.isAvailableForTransportation(200L), is(true));
        server.verify();
    }

    @Test
    public void returnFalseIfDostavchenkoReturnedFalse() {
        server.expect(requestTo("/isDeliveryAvailable?productId=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThat(dostavchenkoClient.isAvailableForTransportation(200L), is(false));
        server.verify();
    }

    @Test
    public void returnFalseIfDostavchenkoReturnedCrap() {
        server.expect(requestTo("/isDeliveryAvailable?productId=200"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Sorry, we can't deliver this item", MediaType.TEXT_PLAIN));

        assertThat(dostavchenkoClient.isAvailableForTransportation(200L), is(false));
        server.verify();
    }

}