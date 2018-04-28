package ru.yurkinsworkshop.tddexample;

import com.github.tomakehurst.wiremock.client.WireMock;
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
@AutoConfigureWireMock(port = 666)
public class TddExampleApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void notifyNotAvailableIfProductQuantityIsZero() throws Exception {
        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 111,\n" +
                        "  \"isAvailable\": false\n" +
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
        stubFor(get(urlEqualTo("isDeliveryAvailable?productId=112"))
                .withHeader("Content-Type", equalTo(MediaType.TEXT_PLAIN_VALUE))
                .willReturn(aResponse().withStatus(HttpStatus.OK_200).withBody("true")));

        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"isAvailable\": true\n" +
                        "}");

        performQuantityUpdateRequest(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"color\" : \"Yellow\",  \n" +
                        "  \"productQuantity\": 10\n" +
                        "}");

        verify(1, postRequestedFor(urlEqualTo("/notify")));
        verify(1, postRequestedFor(urlEqualTo("isDeliveryAvailable?productId=112")));
    }

    @Test
    public void notifyOnceOnSeveralEqualProductMessages() throws Exception {
        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"isAvailable\": true\n" +
                        "}");

        for (int i = 0; i < 5; i++) {
            performQuantityUpdateRequest(
                    // language=JSON
                    "{\n" +
                            "  \"productId\": 112,\n" +
                            "  \"color\" : \"Yellow\",  \n" +
                            "  \"productQuantity\": 10\n" +
                            "}");
        }

        verify(1, postRequestedFor(urlEqualTo("/notify")));
    }

    @Test
    public void notifyFirstAvailableThenNotIfProductQuantityMovedFromPositiveToZero() throws Exception {
        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"isAvailable\": true\n" +
                        "}");

        performQuantityUpdateRequest(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"color\" : \"Yellow\",\n" +
                        "  \"productQuantity\": 10\n" +
                        "}");

        stubNotification(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"isAvailable\": false\n" +
                        "}");

        performQuantityUpdateRequest(
                // language=JSON
                "{\n" +
                        "  \"productId\": 112,\n" +
                        "  \"color\" : \"Yellow\",\n" +
                        "  \"productQuantity\": 0\n" +
                        "}");

        verify(2, postRequestedFor(urlEqualTo("/notify")));
    }

    @Test
    public void noNotificationOnDisabledProduct() throws Exception {
        disableProduct(10);

        for (int i = 0; i < 5; i++) {
            performQuantityUpdateRequest(
                    // language=JSON
                    "{\n" +
                            "  \"productId\": 10,\n" +
                            "  \"color\" : \"Yellow\",\n" +
                            "  \"productQuantity\": " + i + "\n" +
                            "}");
        }

        verify(0, postRequestedFor(urlEqualTo("/notify")));
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
                .withRequestBody(equalTo(content))
                .willReturn(aResponse().withStatus(HttpStatus.OK_200)));
    }
}
