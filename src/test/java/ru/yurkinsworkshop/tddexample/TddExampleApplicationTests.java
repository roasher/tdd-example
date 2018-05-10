package ru.yurkinsworkshop.tddexample;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8090)
public class TddExampleApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void init() {
        WireMock.reset();
    }

    @Test
    public void notifyNotAvailableIfProductQuantityIsZero() throws Exception {
        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 111,\n" +
                        "  \"available\": false\n" +
                        "}");

        performQuantityUpdateRequest(
                // language=JSON
                "{\n" +
                        "  \"productId\": 111,\n" +
                        "  \"color\" : \"red\",  \n" +
                        "  \"productQuantity\": 0\n" +
                        "}");

        verify(1, postRequestedFor(urlEqualTo("/notify")));
    }

    @Test
    public void notifyAvailableYellowProductIfPositiveQuantityAndVozovozApproved() throws Exception {
        stubVozovoz("112");

        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"available\": true\n" +
                        "}");

        performQuantityUpdateRequest(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"color\" : \"Yellow\",  \n" +
                        "  \"productQuantity\": 10\n" +
                        "}");

        verify(1, postRequestedFor(urlEqualTo("/notify")));
    }

    @Test
    public void notifyOnceOnSeveralEqualProductMessages() throws Exception {
        stubVozovoz("113");

        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 113,\n" +
                        "  \"available\": true\n" +
                        "}");

        for (int i = 0; i < 5; i++) {
            performQuantityUpdateRequest(
                    // language=JSON
                    "{\n" +
                            "  \"productId\": 113,\n" +
                            "  \"color\" : \"Yellow\",  \n" +
                            "  \"productQuantity\": 10\n" +
                            "}");
        }

        verify(1, postRequestedFor(urlEqualTo("/notify")));
    }

    @Test
    public void notifyFirstAvailableThenNotIfProductQuantityMovedFromPositiveToZero() throws Exception {
        stubVozovoz("114");

        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 114,\n" +
                        "  \"available\": true\n" +
                        "}");

        performQuantityUpdateRequest(
                // language=JSON
                "{\n" +
                        "  \"productId\": 114,\n" +
                        "  \"color\" : \"Yellow\",\n" +
                        "  \"productQuantity\": 10\n" +
                        "}");

        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 114,\n" +
                        "  \"available\": false\n" +
                        "}");

        performQuantityUpdateRequest(
                // language=JSON
                "{\n" +
                        "  \"productId\": 114,\n" +
                        "  \"color\" : \"Yellow\",\n" +
                        "  \"productQuantity\": 0\n" +
                        "}");

        verify(2, postRequestedFor(urlEqualTo("/notify")));
    }

    @Test
    public void noNotificationOnDisabledProduct() throws Exception {
        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 115,\n" +
                        "  \"available\": false\n" +
                        "}");

        disableProduct(115);

        for (int i = 0; i < 5; i++) {
            performQuantityUpdateRequest(
                    // language=JSON
                    "{\n" +
                            "  \"productId\": 115,\n" +
                            "  \"color\" : \"Yellow\",\n" +
                            "  \"productQuantity\": " + i + "\n" +
                            "}");
        }

        verify(1, postRequestedFor(urlEqualTo("/notify")));
    }

    private void disableProduct(int productId) throws Exception {
        mockMvc.perform(
                post("/disableProduct?productId=" + productId)
        ).andDo(
                print()
        ).andExpect(
                status().isOk()
        );
    }

    private void performQuantityUpdateRequest(String content) throws Exception {
        mockMvc.perform(
                post("/product-quantity-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andDo(
                print()
        ).andExpect(
                status().isOk()
        );
    }

    private void stubNotification(String content) {
        stubFor(WireMock.post(urlEqualTo("/notify"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .withRequestBody(equalToJson(content))
                .willReturn(aResponse().withStatus(HttpStatus.OK_200)));
    }

    private void stubVozovoz(final String productId) {
        stubFor(get(urlEqualTo("/isDeliveryAvailable?productId=" + productId))
                .willReturn(aResponse().withStatus(HttpStatus.OK_200).withBody("true")));
    }
}
