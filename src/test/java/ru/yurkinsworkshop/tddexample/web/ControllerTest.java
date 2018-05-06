package ru.yurkinsworkshop.tddexample.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yurkinsworkshop.tddexample.dto.Update;
import ru.yurkinsworkshop.tddexample.service.UpdateProcessorService;
import ru.yurkinsworkshop.tddexample.service.exception.DataCommunicationException;
import ru.yurkinsworkshop.tddexample.service.exception.VozovozException;
import ru.yurkinsworkshop.tddexample.service.manualexclusion.ManualExclusionService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class ControllerTest {

    @InjectMocks
    private Controller controller;
    @MockBean
    private UpdateProcessorService updateProcessorService;
    @MockBean
    private ManualExclusionService manualExclusionService;

    @Autowired
    private MockMvc mvc;

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
        doThrow(new VozovozException()).when(updateProcessorService).processUpdate(any(Update.class));

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
    public void returnServerErrorOnDataCommunicationError() throws Exception {
        doThrow(new DataCommunicationException()).when(updateProcessorService).processUpdate(any(Update.class));

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
                        "      \"message\": \"Can't communicate with Data system\"\n" +
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
        doThrow(new RuntimeException()).when(updateProcessorService).processUpdate(any(Update.class));

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