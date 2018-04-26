package ru.yurkinsworkshop.tddexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TddExampleApplicationTests {

	@Test
	public void notifyNotAvailableIfProductQuantityIsZero() {}

	@Test
	public void notifyAvailableYellowProductIfPositiveQuantityAndVozovozApproved() {}

	@Test
	public void notifyOnceOnSeveralEqualProductMessages() {}

	@Test
	public void notifyFirstAvailableThenNotIfProductQuantityMovedFromPositiveToZero() {}

}
