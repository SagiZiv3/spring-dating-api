package iob;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.NewUserBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.Location;
import iob.boundaries.helpers.UserRoleBoundary;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstancesAPITests {
    @Value("${spring.application.name:dummy}")
    private String domainName;
    @Autowired
    private InstanceConverter instanceConverter;
    private int port;
    private NewUserBoundary userActivating;
    private RestTemplate client; //  helper object to invoke HTTP requests
    private String url; // used to represent the URL used to access the server

    @Test
    public void testGetAllInstances() {
        // GIVEN
        // we have instances in the program
        ArrayList<InstanceBoundary> demo1 = new ArrayList<>();
        demo1.add(addDummyInstance(KEYS.INSTANCE_NAME + "_0"));
        demo1.add(addDummyInstance(KEYS.INSTANCE_NAME + "_1"));
        demo1.add(addDummyInstance(KEYS.INSTANCE_NAME + "_2"));

        // Get all instances
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "/instances/2022a.tomer/user@gmail.com",
                InstanceBoundary[].class);

        assertThat(returnedFromRequest).containsAll(demo1);


    }

    private InstanceBoundary addDummyInstance(String instanceName) {
        Location locObj = new Location(5.0, 5.0); // creating dummy location
        InstanceBoundary addMe = new InstanceBoundary();
        addMe.setName(instanceName);
        addMe.setType(KEYS.INSTANCE_TYPE);
        addMe.setLocation(locObj);

        // post instance on system
        InstanceBoundary requestsOutput = this.client.postForObject(this.url + "/instances/" + domainName + "/" + KEYS.USER_EMAIL,
                addMe,
                InstanceBoundary.class);

        return requestsOutput;
    }

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

    @BeforeAll
    public void createUserInvoking() {
        // Adding user to server createUserInvoking
        userActivating = new NewUserBoundary(KEYS.USER_EMAIL, UserRoleBoundary.ADMIN, KEYS.USERNAME, KEYS.USER_AVATAR);
        this.client.postForObject(this.url + "/users",
                userActivating,
                NewUserBoundary.class);
    }

    @AfterAll
    public void deleteAllUsers() {
        this.client.delete(this.url + "/admin/instances/" + domainName + "/" + KEYS.USER_EMAIL);
        this.client.delete(this.url + "/admin/users/" + domainName + "/" + KEYS.USER_EMAIL);

    }

    @Test
    void contextLoads() {
    }

//	@AfterEach
//	public void cleanup(){
//		this.client.delete(url + "/admin/instances/2022a.tomer/user@gmail.com");
//	}

    @Test
    public void testInsertNewInstance() {
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
    public void testModifyInstance() {
        // insert old
        InstanceBoundary requestsOutput = addDummyInstance(KEYS.INSTANCE_NAME);
        InstanceIdBoundary id = requestsOutput.getInstanceId();

        // create new
        InstanceBoundary newUpdatedInstance = new InstanceBoundary();
        newUpdatedInstance.setType("MashuAher");
        Location locObj = new Location(5.0, 5.0);
        newUpdatedInstance.setLocation(locObj);

        // Modifying to new instance
        this.client.put(this.url + "/instances/2022a.tomer/user@gmail.com/" + id.getDomain() + "/" + id.getId(),
                newUpdatedInstance);


        // Fetching modified instance
        InstanceBoundary fetchedFromDB = this.client.getForObject(this.url + "/instances/2022a.tomer/user@gmail.com/" + id.getDomain() + "/" + id.getId(),
                InstanceBoundary.class);

        assertEquals(fetchedFromDB.getLocation(), locObj);
        assertEquals(fetchedFromDB.getType(), "MashuAher");
    }

    @Test
    public void testDeleteAllInstance() {
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

    // Enable access from everywhere using: InstanceAPITests.KEYS.{___}
    public interface KEYS {
        String USER_EMAIL = "user@gmail.com";
        String USERNAME = "InstancesAPITests_InvokingUser";
        String USER_AVATAR = "InvokingUser";
        String INSTANCE_TYPE = "dummyInstanceType";
        String INSTANCE_NAME = "dummyInstanceName";
    }



    // TODO: 03/12/2021 bindChildToInstance
    @Test
    public void testBindChildToInstance(){
        // GIVEN we have 2 instances

        // WHEN: we set one of them to be the other's child ??? TODO: Make sure its true


        // THEN: We will bind them as array (or arraylist??)

//        InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "/instances/2022a.tomer/user@gmail.com"
//                        + domainName + "/" + id.getId(),
//                InstanceBoundary[].class);

        /* ___________________________________________________________________________
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


    }


    // TODO: 03/12/2021 getAllParents
    @Test
    public void testGetAllParents(){
        // GIVEN we have instance with parents

        // WHEN: we ask to see all it's parents


        // THEN: We will get a them as array (or arraylist??)

//        InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "/instances/2022a.tomer/user@gmail.com"
//                        + domainName + "/" + id.getId(),
//                InstanceBoundary[].class);


                /*____________________________________________________________
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

    }

    // TODO: 03/12/2021 getInstance
    @Test
    public void testGetAllChildren(){
        // GIVEN we have instance with parents

        // WHEN: we ask to see all it's parents


        // THEN: We will get a them as array (or arraylist??)

//        InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "/instances/2022a.tomer/user@gmail.com"
//                        + domainName + "/" + id.getId(),
//                InstanceBoundary[].class);


          /* ____________________________________________________________
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

    }

}




