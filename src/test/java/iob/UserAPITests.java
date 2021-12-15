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
public class UserAPITests {
    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    public interface KEYS {
        final String USER_EMAIL = "Shahar@userApiTest.com";
        final String USERNAME = "InstancesAPITests_InvokingUser";
        final String USER_AVATAR = "InvokingUser_userApiTest";

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

// TODO: 03/12/2021 createUser
/*
CRUD:
    GET
URL:
     http://localhost:8091/iob/users/login/2022a.Tomer.Dwek/us_er33@ab.com

Content-Type: (sent object):
    __None__
Accept (returns):
    __None__
BODY:
    __None__
 */

// TODO: 03/12/2021 getUser
/*
CRUD:
    GET
URL:
    http://localhost:8091/iob/users/login/2022a.Tomer.Dwek/us_er33@ab.com

Content-Type: (sent object):
    __None__
Accept (returns):
    __None__ ??? HOW?? it should return an object isn't it?
BODY:

 */

// TODO: 03/12/2021 getAllUsers
/*
CRUD:
    GET
URL:
    http://localhost:8091/iob/admin/users/2022a.Tomer.Dwek/aa@bb.com
Content-Type: (sent object):
    __None__
Accept (returns):
    __None__
BODY:
    __None__
 */
// TODO: 03/12/2021 updateUser
/*
CRUD:
    PUT
URL:
    http://localhost:8091/iob/users/2022a.Tomer.Dwek/aa@bb.com
Content-Type: (sent object):
    __None__
Accept (returns):
    __None__
BODY:
    {
    "role":"ADMIN",
    "username":"MY USER Nameeee"
    }

 */

