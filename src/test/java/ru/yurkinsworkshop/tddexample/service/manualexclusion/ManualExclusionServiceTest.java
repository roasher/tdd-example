package ru.yurkinsworkshop.tddexample.service.manualexclusion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ManualExclusionServiceTest {

    @Autowired
    private ManualExclusionService service;
    @Autowired
    private ManualExclusionRepository manualExclusionRepository;

    @Before
    public void clearDb() {
        manualExclusionRepository.deleteAll();
    }

    @Test
    public void disableItem() {
        Long productId = 100L;
        service.disableProduct(productId);

        assertThat(service.isProductEnabled(productId), is(false));
    }

    @Test
    public void returnEnabledIfProductWasNotDisabled() {
        assertThat(service.isProductEnabled(100L), is(true));
        assertThat(service.isProductEnabled(200L), is(true));
    }

}