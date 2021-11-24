package iob;

import javax.annotation.PostConstruct;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.Instance;
import iob.boundaries.helpers.Location;
import iob.boundaries.helpers.ObjectId;
import lombok.Cleanup;



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstancesAPITests {
	
	@Value("${spring.application.name:dummy}")
	private String domainName;
	private int port;
	
	// setup helper object to invoke HTTP requests
	private RestTemplate client;
	
	// setup a String used to represent the URL used to access the server
	private String url;
	
	// get random port used by server
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@PostConstruct
	public void initTestCase() {
		this.url = "http://localhost:" + this.port + "/iob";
		this.client = new RestTemplate();
	}

	@Test
	public void testInsertNewInstance(){
		// GIVEN 
		// the server is up ( DO Nothing)
		InstanceBoundary requestsOutput = addDummyInstance("Sagi Ziv", "MahrozetBliMashmaut");
		ObjectId id = requestsOutput.getInstanceId();
		
		// THEN
		// instance would be added
		InstanceBoundary fetchedFromDB = this.client.getForObject(this.url + "/instances/2022a.tomer/user@gmail.com/" + id.getDomain() + "/" + id.getId(),
								InstanceBoundary.class,
								id.getDomain() + ";" + id.getId());
		
		assertEquals(fetchedFromDB.getName(), "Sagi Ziv");
		assertEquals(fetchedFromDB.getType(), "MahrozetBliMashmaut");

	}

	private InstanceBoundary addDummyInstance(String name, String type){
		InstanceBoundary instanceMoveMe = new InstanceBoundary();
		instanceMoveMe.setName(name);
		instanceMoveMe.setType(type);
		InstanceBoundary requestsOutput = this.client.postForObject(this.url + "/instances/2022a.tomer/user@gmail.com",
																	instanceMoveMe,
																	InstanceBoundary.class);
																
		return requestsOutput;
	}

	@Test
	public void testModifyInstance(){
		InstanceBoundary requestsOutput = addDummyInstance("Sagi Ziv", "MahrozetBliMashmaut");
		ObjectId id = requestsOutput.getInstanceId();
		
		InstanceBoundary newUpdatedInstance = new InstanceBoundary();
		newUpdatedInstance.setType("MashuAher");
		Location locObj = new Location(5.0,5.0);
		newUpdatedInstance.setLocation(locObj);

		// Modifying to new instace
		this.client.put(this.url + "/instances/2022a.tomer/user@gmail.com/" + id.getDomain() + "/" + id.getId(),
						newUpdatedInstance,
						id.getDomain() + ";" + id.getId());

		
		// Fetching modified instance
		InstanceBoundary fetchedFromDB = this.client.getForObject(this.url + "/instances/2022a.tomer/user@gmail.com/" + id.getDomain() + "/" + id.getId(),
																InstanceBoundary.class,
																id.getDomain() + ";" + id.getId());

		assertEquals(fetchedFromDB.getLocation(), locObj);
		assertEquals(fetchedFromDB.getType(), "MashuAher");
	}
		
	@Test
	public void testDeleteAllInstance(){
		// GIVEN 
		// we have instances in the program
		addDummyInstance("Sagi Ziv", "MahrozetBliMashmaut");
		addDummyInstance("Shahar Raz", "MahrozetBliMashmaut");
		addDummyInstance("Ben Koren", "MahrozetBliMashmaut");
		
		// Delete All Instance
		this.client.delete(url + "/admin/instances/2022a.tomer/user@gmail.com");

		// Get all instances
		InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "/instances/2022a.tomer/user@gmail.com",
																			InstanceBoundary[].class);
		
		assertThat(returnedFromRequest).isEmpty();

	}

	@AfterEach
	public void cleanup(){
		this.client.delete(url + "/admin/instances/2022a.tomer/user@gmail.com");
	}

	@Test
	void contextLoads() {
	}

}