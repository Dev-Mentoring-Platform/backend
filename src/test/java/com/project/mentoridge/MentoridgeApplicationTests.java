package com.project.mentoridge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {"spring.config.location=classpath:application-test.yml"})
class MentoridgeApplicationTests {

	@Test
	void contextLoads() {
	}

}
