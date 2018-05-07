package ru.yurkinsworkshop.tddexample.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;
import ru.yurkinsworkshop.tddexample.service.exception.DataCommunicationException;
import ru.yurkinsworkshop.tddexample.service.notifier.AvailabilityNotifier;
import ru.yurkinsworkshop.tddexample.service.notifier.lazy.AvailabilityRepository;
import ru.yurkinsworkshop.tddexample.service.notifier.LazyAvailabilityNotifier;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LazyAvailabilityNotifierTest {

    @Autowired
    private LazyAvailabilityNotifier lazyAvailabilityNotifier;

    @MockBean
    @Qualifier("availabilityNotifierImpl")
    private AvailabilityNotifier availabilityNotifier;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Before
    public void clearDb() {
        availabilityRepository.deleteAll();
    }

    @Test
    public void notifyIfFirstTime() {
        sendNotificationAndVerifyDataBase(new ProductAvailability(1L, false));
    }

    @Test
    public void notifyIfAvailabilityChanged() {
        final ProductAvailability oldProductAvailability = new ProductAvailability(1L, false);
        sendNotificationAndVerifyDataBase(oldProductAvailability);

        final ProductAvailability newProductAvailability = new ProductAvailability(1L, true);
        sendNotificationAndVerifyDataBase(newProductAvailability);
    }

    @Test
    public void doNotNotifyIfAvailabilityDoesNotChanged() {
        final ProductAvailability productAvailability = new ProductAvailability(1L, false);
        sendNotificationAndVerifyDataBase(productAvailability);
        sendNotificationAndVerifyDataBase(productAvailability);
        sendNotificationAndVerifyDataBase(productAvailability);
        sendNotificationAndVerifyDataBase(productAvailability);

        verify(availabilityNotifier, only()).notify(eq(productAvailability));
    }

    @Test
    public void doNotSaveIfSentWithException() {
        doThrow(new RuntimeException()).when(availabilityNotifier).notify(anyObject());

        boolean exceptionThrown = false;
        try {
            availabilityNotifier.notify(new ProductAvailability(1L, false));
        } catch (RuntimeException exception) {
            exceptionThrown = true;
        }

        assertTrue("Exception was not thrown", exceptionThrown);
        assertThat(availabilityRepository.findAll(), hasSize(0));
    }

    @Test(expected = DataCommunicationException.class)
    public void wrapDataException() {
        doThrow(new RestClientException("Something wrong")).when(availabilityNotifier).notify(anyObject());

        lazyAvailabilityNotifier.notify(new ProductAvailability(1L, false));
    }

    private void sendNotificationAndVerifyDataBase(ProductAvailability productAvailability) {

        lazyAvailabilityNotifier.notify(productAvailability);

        verify(availabilityNotifier).notify(eq(productAvailability));
        assertThat(availabilityRepository.findAll(), hasSize(1));
        assertThat(availabilityRepository.findAll().get(0),
                hasProperty("productId", is(productAvailability.getProductId())));
        assertThat(availabilityRepository.findAll().get(0),
                hasProperty("availability", is(productAvailability.isAvailable())));
    }
}