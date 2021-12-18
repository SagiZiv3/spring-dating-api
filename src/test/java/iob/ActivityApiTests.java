package iob;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InstanceIdWrapper;
import iob.boundaries.helpers.InvokedByBoundary;
import iob.boundaries.helpers.Location;
import iob.boundaries.helpers.UserRoleBoundary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActivityApiTests {
    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    public interface KEYS {
        String USER_EMAIL = "Shahar@sagi.com";
        String USERNAME = "InstancesAPITests_InvokingUser";
        String USER_AVATAR = "InvokingUser";
    }

    @Value("${spring.application.name:dummy}")
    private String domainName;

    @Autowired
    private InstanceConverter instanceConverter;
    private int port;
    private UserBoundary userActivating;

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
    }

    @BeforeAll
    public void createUserInvoking() {
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL, UserRoleBoundary.ADMIN, KEYS.USERNAME, KEYS.USER_AVATAR);
        this.userActivating = this.client.postForObject(this.url + "/users",
                insertMe,
                UserBoundary.class);
    }


    @Test
    void contextLoads() {
    }

    @Test
    public void testInsertActivity(){
        // 0. ____ create instance invoking ____
        Location locObj = new Location(5.0, 5.0); // creating dummy location
        InstanceBoundary addMe = new InstanceBoundary();
        addMe.setName("invokingActivity");
        addMe.setType(InstancesAPITests.KEYS.INSTANCE_TYPE);
        addMe.setLocation(locObj);

        // post instance on system
        InstanceBoundary requestsOutput = this.client.postForObject(this.url + "/instances/" + domainName + "/" + KEYS.USER_EMAIL,
                addMe,
                InstanceBoundary.class);


        // 1, create activity boundary with id=null
        ActivityBoundary activityBoundary = new ActivityBoundary();
        activityBoundary.setInstance(new InstanceIdWrapper(requestsOutput.getInstanceId()));
        activityBoundary.setInvokedBy(new InvokedByBoundary(userActivating.getUserId()));
        activityBoundary.setType("SomeCoolType- ActivityApiTest- Line90");
//        activityBoundary.getActivityId().setId(null);

        // post activity ( Get it back with the ID)
        activityBoundary = this.client.postForObject(url  +"/activities",
                activityBoundary,
                ActivityBoundary.class);


        // Export all activities in domain
        ActivityBoundary[] returnedFromRequest = this.client.getForObject(this.url + "/admin/activities/" + domainName + "/" + KEYS.USER_EMAIL,
                ActivityBoundary[].class);


        // assert addMe is within the activities.
        assertThat(returnedFromRequest).contains(activityBoundary);
        /*
        CRUD:
            POST
        URL:
            http://localhost:8091/iob/activities

        Content-Type: (expected object):
            application/json

        Accept (returns object):

        BODY:
                {
            "type":"demoActivityType",
            "instance":{
            "instanceId":{
            "domain":"2022a.demo",
            "id":"352"
            }
            },
            "invokedBy":{
            "userId":{
            "domain":"2022a.demo",
            "email":"user3@demo.com"
            }
            },
            "activityAttributes":{
            "key1":"can be set to any value you wish",
            "key2":{
            "key2Subkey1":"can be nested json"
            }
            }
            }
         */

    }
}