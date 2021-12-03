package iob;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.NewUserBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstancesAPITests {
	// Enable access from everywhere using: InstanceAPITests.KEYS.{___}
	public interface KEYS{
		final String USER_EMAIL = "Shahar@sagi.com";
		final String USERNAME = "InstancesAPITests_InvokingUser";
		final String USER_AVATAR = "InvokingUser";
		final String INSTANCE_TYPE = "dummyInstance";
		final String INSTANCE_NAME = "dummyInstance";

	}

	@Value("${spring.application.name:dummy}")
	private String domainName;

	@Autowired
	private InstanceConverter instanceConverter;
	private int port;
	private NewUserBoundary userActivating;
	private RestTemplate client; //  helper object to invoke HTTP requests
	private String url; // used to represent the URL used to access the server

	// get random port used by server
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@PostConstruct
	public void initTestCase() {
		this.url = "http://localhost:" + this.port + "/iob";
		this.client = new RestTemplate();

		// Adding user to server createUserInvoking
		userActivating = new NewUserBoundary(KEYS.USER_EMAIL, UserRoleBoundary.ADMIN, KEYS.USERNAME, KEYS.USER_AVATAR);
		this.client.postForObject(this.url + "/users",
				userActivating,
				NewUserBoundary.class);
	}


//	@AfterAll
//	public void deleteAllUsers(){
////		this.client.delete(this.url + "/admin/users/" + domainName + "/" + domai);
//	}

	private InstanceBoundary addDummyInstance(String instanceName){
		Location locObj = new Location(5.0,5.0); // creating dummy location
		InstanceBoundary addMe = new InstanceBoundary();
		addMe.setName(instanceName);
		addMe.setType(KEYS.INSTANCE_TYPE);
		addMe.setLocation(locObj);

		// ___ WHAT and WHY Am I? ___
		CreatedByBoundary creatorOfInstance = new CreatedByBoundary(new UserIdBoundary(domainName, KEYS.USER_EMAIL));
		addMe.setCreatedBy(creatorOfInstance);

		// post instance on system
		InstanceBoundary requestsOutput = this.client.postForObject(this.url + "/instances/2022a.tomer/user@gmail.com",
				addMe,
				InstanceBoundary.class);

		return requestsOutput;
	}

	@Test
	void contextLoads() {
	}

//	@AfterEach
//	public void cleanup(){
//		this.client.delete(url + "/admin/instances/2022a.tomer/user@gmail.com");
//	}

	@Test
	public void testInsertNewInstance(){
		// GIVEN 
		// the server is up ( DO Nothing)

		// WHEN we insert new instance
		InstanceBoundary requestsOutput = addDummyInstance(KEYS.INSTANCE_NAME);
		InstanceIdBoundary id = requestsOutput.getInstanceId();
		
		// THEN instance would be added
		InstanceBoundary fetchedFromDB = this.client.getForObject(this.url + "/instances/2022a.tomer/user@gmail.com/" + id.getDomain() + "/" + id.getId(),
				InstanceBoundary.class,
				instanceConverter.toInstancePrimaryKey(id));
		
		assertEquals(fetchedFromDB.getName(), KEYS.INSTANCE_NAME);
		assertEquals(fetchedFromDB.getType(), KEYS.INSTANCE_TYPE);

	}


	@Test
	public void testModifyInstance(){
		InstanceBoundary requestsOutput = addDummyInstance(KEYS.INSTANCE_NAME);
		InstanceIdBoundary id = requestsOutput.getInstanceId();
		
		InstanceBoundary newUpdatedInstance = new InstanceBoundary();
		newUpdatedInstance.setType("MashuAher");
		Location locObj = new Location(5.0,5.0);
		newUpdatedInstance.setLocation(locObj);

		// Modifying to new instance
		this.client.put(this.url + "/instances/2022a.tomer/user@gmail.com/" + id.getDomain() + "/" + id.getId(),
				newUpdatedInstance,
				instanceConverter.toInstancePrimaryKey(id));

		
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
		addDummyInstance(KEYS.INSTANCE_NAME + "_0");
		addDummyInstance(KEYS.INSTANCE_NAME + "_1");
		addDummyInstance(KEYS.INSTANCE_NAME + "_2");


		// Delete All Instance
		this.client.delete(url + "/admin/instances/2022a.tomer/user@gmail.com");

		// Get all instances
		InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "/instances/2022a.tomer/user@gmail.com",
																			InstanceBoundary[].class);
		// check that we dont have any users inside
		assertThat(returnedFromRequest).isEmpty();

	}

	@Test
	public void testGetAllInstances(){
		// GIVEN
		// we have instances in the program
		InstanceBoundary[] demo =  new InstanceBoundary[3];
		demo[0] =  addDummyInstance(KEYS.INSTANCE_NAME + "_0");
		demo[1] = addDummyInstance(KEYS.INSTANCE_NAME+ "_1");
		demo[2] = addDummyInstance(KEYS.INSTANCE_NAME+ "_2");

		// Get all instances
		InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "/instances/2022a.tomer/user@gmail.com",
				InstanceBoundary[].class);

		// check that we dont have any users inside
		// TODO: 03/12/2021 fix contains assertion. Maybe implement equals manually [in InstanceBoundary]
		// assertThat(returnedFromRequest).containsAll(Arrays.asList(demo));
		assertThat((InstanceBoundary[])returnedFromRequest).contains(demo[0]);


		// Delete All Instance
//		this.client.delete(url + "/admin/instances/2022a.tomer/user@gmail.com");
	}


}

// TODO: 03/12/2021 getAllParents
/*
CRUD:
    GET
URL:
    http://localhost:8091/iob/instances/2022a.tomer/user@gmail.com/2022a.Tomer.Dwek/1/parents

Content-Type: (sent object):
    __None__
Accept (returns):
    __None__ ??? HOW?? it should return an object isn't it?
BODY:

 */

// TODO: 03/12/2021 getAllChildren
/*
CRUD:
    GET
URL:
    http://localhost:8091/iob/instances/2022a.tomer/user@gmail.com/2022a.Tomer.Dwek/3/children

Content-Type: (sent object):
    __None__
Accept (returns):
    __None__ ??? HOW?? it should return an object isn't it?
BODY:

 */

// TODO: 03/12/2021 getInstance
/*
CRUD:
    GET
URL:
    http://localhost:8091/iob/instances/2022a.tomer/user@gmail.com/2022a.Tomer.Dwek/1

Content-Type: (sent object):
    __None__
Accept (returns):
    __None__ ??? HOW?? it should return an object isn't it?
BODY:

 */

// TODO: 03/12/2021 bindChildToInstance
/*
CRUD:
    PUT
URL:
    http://localhost:8091/iob/instances/2022a/mail/2022a.Tomer.Dwek/3/children

Content-Type: (sent object):
    Application/json
Accept (returns):
    __None__ ??? HOW?? it should return an object isn't it?
BODY:
	{
    "domain": "2022a.Tomer.Dwek",
    "id": "1"
	}
 */

