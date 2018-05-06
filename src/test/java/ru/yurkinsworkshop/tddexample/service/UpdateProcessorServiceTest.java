package ru.yurkinsworkshop.tddexample.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import ru.yurkinsworkshop.tddexample.client.VozovozClient;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;
import ru.yurkinsworkshop.tddexample.dto.Update;
import ru.yurkinsworkshop.tddexample.service.exception.VozovozException;
import ru.yurkinsworkshop.tddexample.service.manualexclusion.ManualExclusionService;
import ru.yurkinsworkshop.tddexample.service.notifier.AvailabilityNotifier;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateProcessorServiceTest {

    @InjectMocks
    private UpdateProcessorService updateProcessorService;

    @Mock
    private ManualExclusionService manualExclusionService;
    @Mock
    private VozovozClient vozovozClient;
    @Mock
    private AvailabilityNotifier availabilityNotifier;

    @Test
    public void notifyAvailableIfYellowProductIsEnabledAndReadyForTransportation() {
        final Update testProduct = new Update(1L, 10L, "Yellow");

        when(vozovozClient.isAvailableForTransportation(testProduct.getProductId())).thenReturn(true);
        when(manualExclusionService.isProductEnabled(testProduct.getProductId())).thenReturn(true);

        updateProcessorService.processUpdate(testProduct);

        verify(availabilityNotifier, only()).notify(eq(new ProductAvailability(testProduct.getProductId(), true)));
    }

    @Test
    public void notifyNotAvailableIfProductIsAbsent() {
        final Update testProduct = new Update(1L, 0L, "Yellow");

        updateProcessorService.processUpdate(testProduct);

        verify(availabilityNotifier, only()).notify(eq(new ProductAvailability(testProduct.getProductId(), false)));
        verifyNoMoreInteractions(manualExclusionService);
        verifyNoMoreInteractions(vozovozClient);
    }

    @Test
    public void notifyNotAvailableIfProductIsBlue() {
        final Update testProduct = new Update(1L, 10L, "Blue");

        updateProcessorService.processUpdate(testProduct);

        verify(availabilityNotifier, only()).notify(eq(new ProductAvailability(testProduct.getProductId(), false)));
        verifyNoMoreInteractions(manualExclusionService);
        verifyNoMoreInteractions(vozovozClient);
    }

    @Test
    public void notifyNotAvailableIfProductIsDisabled() {
        final Update testProduct = new Update(1L, 10L, "Yellow");

        when(manualExclusionService.isProductEnabled(testProduct.getProductId())).thenReturn(false);

        updateProcessorService.processUpdate(testProduct);

        verify(availabilityNotifier, only()).notify(eq(new ProductAvailability(testProduct.getProductId(), false)));
        verifyNoMoreInteractions(vozovozClient);
    }

    @Test
    public void notifyNotAvailableIfProductIsNotReadyForTransportation() {
        final Update testProduct = new Update(1L, 10L, "Yellow");

        when(vozovozClient.isAvailableForTransportation(testProduct.getProductId())).thenReturn(false);
        when(manualExclusionService.isProductEnabled(testProduct.getProductId())).thenReturn(true);

        updateProcessorService.processUpdate(testProduct);

        verify(availabilityNotifier, only()).notify(eq(new ProductAvailability(testProduct.getProductId(), false)));
    }

    @Test(expected = VozovozException.class)
    public void throwCustomExceptionIfVozovozCommunicationFailed() {
        final Update testProduct = new Update(1L, 10L, "Yellow");

        when(vozovozClient.isAvailableForTransportation(testProduct.getProductId()))
                .thenThrow(new RestClientException("Something's wrong"));
        when(manualExclusionService.isProductEnabled(testProduct.getProductId())).thenReturn(true);

        updateProcessorService.processUpdate(testProduct);
    }

}