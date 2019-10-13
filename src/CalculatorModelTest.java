import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CalculatorModelTest {

	private CalculatorModel cm;
	
	@BeforeEach
	void beforeEach() {
		cm = new CalculatorModel();
	}
	
	@AfterEach
	void afterEach() {
		cm.clear();
	}
	
	@Test
	void test() {
		cm.push("50");
		cm.push("+");
		assertTrue(cm.parse("132--456"));
	}

}
