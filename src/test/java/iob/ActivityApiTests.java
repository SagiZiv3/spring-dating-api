package iob;

import iob.boundaries.NewUserBoundary;
import iob.boundaries.converters.InstanceConverter;
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


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActivityApiTests {
    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    public interface KEYS {
        final String USER_EMAIL = "Shahar@sagi.com";
        final String USERNAME = "InstancesAPITests_InvokingUser";
        final String USER_AVATAR = "InvokingUser";
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
    }

    @BeforeAll
    public void createUserInvoking() {
        // Adding user to server
        userActivating = new NewUserBoundary(KEYS.USER_EMAIL, UserRoleBoundary.ADMIN, KEYS.USERNAME, KEYS.USER_AVATAR);
        this.client.postForObject(this.url + "/users",
                userActivating,
                NewUserBoundary.class);
    }

    @Test
    void contextLoads() {
    }

}

// TODO: 04/12/2021 insertActivityToDatabase


//// TODO: 03/12/2021 InvokeActivity
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

