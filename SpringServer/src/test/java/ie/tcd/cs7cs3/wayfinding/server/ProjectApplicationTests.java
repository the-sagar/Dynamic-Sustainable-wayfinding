package ie.tcd.cs7cs3.wayfinding.server;

import ie.tcd.cs7cs3.wayfinding.server.repository.AreaToAvoidRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProjectApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	AreaToAvoidRepository areaToAvoidRepository;

	@Test
	void GetDataFromServer(){
		areaToAvoidRepository.getActiveAreaToAvoid();
	}
}
