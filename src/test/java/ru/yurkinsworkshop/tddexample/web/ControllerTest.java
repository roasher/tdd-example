package ru.yurkinsworkshop.tddexample.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yurkinsworkshop.tddexample.dto.Update;
import ru.yurkinsworkshop.tddexample.service.Service;
import ru.yurkinsworkshop.tddexample.service.exception.VozovozException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    @InjectMocks
    private Controller controller;
    @Mock
    private Service service;

    private MockMvc mvc;

    @Before
    public void init() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void returnBadRequestOnDisableWithInvalidProductId() throws Exception {
        mvc.perform(
                post("/disableProduct?productId=-443")
        ).andDo(
            print()
        ).andExpect(
                status().isBadRequest()
        ).andExpect(
                content().json(getInvalidProductIdJsonContent())
        );
    }

    @Test
    public void returnBadRequestOnNotifyWithInvalidProductId() throws Exception {
        performUpdate(
                //language=JSON
                "{\n" +
                        "  \"productId\": -1,\n" +
                        "  \"color\": \"red\",\n" +
                        "  \"productQuantity\": 0\n" +
                        "}"
        ).andDo(
                print()
        ).andExpect(
                status().isBadRequest()
        ).andExpect(
                content().json(getInvalidProductIdJsonContent())
        );
    }

    @Test
    public void returnBadRequestOnNotifyWithNegativeProductQuantity() throws Exception {
        performUpdate(
                //language=JSON
                "{\n" +
                        "  \"productId\": 1,\n" +
                        "  \"color\": \"red\",\n" +
                        "  \"productQuantity\": -10\n" +
                        "}"
        ).andDo(
                print()
        ).andExpect(
                status().isBadRequest()
        ).andExpect(
                content().json("{\n" +
                        "  \"errors\": [\n" +
                        "    {\n" +
                        "      \"message\": \"productQuantity is invalid\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}")
        );

    }

    @Test
    public void returnServerErrorOnVozovozCommunicationError() throws Exception {
        doThrow(new VozovozException()).when(service).processUpdate(any(Update.class));

        performUpdate(
                //language=JSON
                "{\n" +
                        "  \"productId\": 1,\n" +
                        "  \"color\": \"red\",\n" +
                        "  \"productQuantity\": 10\n" +
                        "}"
        ).andDo(
                print()
        ).andExpect(
                status().isInternalServerError()
        ).andExpect(
                content().json("{\n" +
                        "  \"errors\": [\n" +
                        "    {\n" +
                        "      \"message\": \"Vozovoz communication exception\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}")
        );

    }

    @Test
    public void return200OnSuccess() throws Exception {
        performUpdate(
                //language=JSON
                "{\n" +
                        "  \"productId\": 1,\n" +
                        "  \"color\": \"red\",\n" +
                        "  \"productQuantity\": 10\n" +
                        "}"
        ).andDo(
                print()
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    public void returnServerErrorOnUnexpectedException() throws Exception {
        doThrow(new RuntimeException()).when(service).processUpdate(any(Update.class));

        performUpdate(
                //language=JSON
                "{\n" +
                        "  \"productId\": 1,\n" +
                        "  \"color\": \"red\",\n" +
                        "  \"productQuantity\": 10\n" +
                        "}"
        ).andDo(
                print()
        ).andExpect(
                status().isInternalServerError()
        ).andExpect(
                content().json("{\n" +
                        "  \"errors\": [\n" +
                        "    {\n" +
                        "      \"message\": \"Internal Server Error\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}")
        );
    }

    @Test
    public void returnTwoErrorMessagesOnInvalidProductIdAndNegativeQuantity() throws Exception {
        performUpdate(
                //language=JSON
                "{\n" +
                        "  \"productId\": -1,\n" +
                        "  \"color\": \"red\",\n" +
                        "  \"productQuantity\": -10\n" +
                        "}"
        ).andDo(
                print()
        ).andExpect(
                status().isBadRequest()
        ).andExpect(
                content().json("{\n" +
                        "  \"errors\": [\n" +
                        "    { \"message\": \"productQuantity is invalid\" },\n" +
                        "    { \"message\": \"productId is invalid\" }\n" +
                        "  ]\n" +
                        "}")
        );
    }

    private ResultActions performUpdate(String jsonContent) throws Exception {
        return mvc.perform(
                post("/product-quantity-update")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(jsonContent)
        );
    }

    private String getInvalidProductIdJsonContent() {
        return
                //language=JSON
                "{\n" +
                        "  \"errors\": [\n" +
                        "    {\n" +
                        "      \"message\": \"productId is invalid\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
    }
}