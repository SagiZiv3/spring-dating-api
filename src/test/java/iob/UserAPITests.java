package iob;

import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAPITests {
    UserConverter userConverter;

    @BeforeAll
    public void createUserInvoking() {
        // Adding user to server
        NewUserBoundary userActivating = new NewUserBoundary(KEYS.USER_EMAIL, UserRoleBoundary.ADMIN, KEYS.USERNAME, KEYS.USER_AVATAR);
        this.client.postForObject(this.url + "/users",
                userActivating,
                NewUserBoundary.class);
    }

    @Autowired
    public UserAPITests(UserConverter userConverter_whereFrom) {
        this.userConverter = userConverter_whereFrom;

    }

    private String domainName;

    private int port;
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

    @Test
    public void testCreateUSer() {
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(this.url + "/users",
                insertMe,
                NewUserBoundary.class);


        NewUserBoundary[] returnedFromRequest = this.client.getForObject(this.url + "/admin/users/" + "/" + domainName + "/" + KEYS.USER_EMAIL,
                NewUserBoundary[].class);

        // FIXME // How to Compare UserBoundary to NewUserBoundary? 15/12/2021 Maybe convert one of them to the other, or check specific fields? Consult with everyone..
        // TODO: 15/12/2021 Test Failed: The returned objects does not contain the User's email (null instead). FIXME
        assertThat(returnedFromRequest).contains(insertMe);

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

    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testGetUser() {
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(this.url + "/users",
                insertMe,
                NewUserBoundary.class);

        // get user from server
        UserBoundary returnedFromRequest = this.client.getForObject(this.url + "/users" + "/login/" + domainName + "/" + KEYS.USER_EMAIL_Player,
                UserBoundary.class);


        assertThat(returnedFromRequest.getUserId().getEmail()).isEqualTo(insertMe.getEmail());
        assertThat(returnedFromRequest.getUserId().getDomain()).isEqualTo(domainName);
//        assertThat(returnedFromRequest).isEqualTo(insertMe);

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
    }

    @Test
    public void testGetAllUsers() {
        ArrayList<NewUserBoundary> insertUs = new ArrayList<>();
        insertUs.add(new NewUserBoundary("0_" + KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player + "_0", KEYS.USER_AVATAR_Player + "_0"));
        insertUs.add(new NewUserBoundary("1_" + KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player + "_1", KEYS.USER_AVATAR_Player + "_1"));
        insertUs.add(new NewUserBoundary("2_" + KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player + "_2", KEYS.USER_AVATAR_Player + "_2"));
        // Adding users to server
        for (NewUserBoundary newUserBoundary : insertUs) {
            this.client.postForObject(this.url + "/users",
                    newUserBoundary,
                    NewUserBoundary.class);

        }
        // Getting all users from server
        UserBoundary[] returnedFromRequest = this.client.getForObject(this.url + "/admin/users/" + domainName + "/" + KEYS.USER_EMAIL,
                UserBoundary[].class);


        // Convert inserted elements insertUs to UserBoundary (will enable the usage of containsAll)
        List<UserBoundary> insertedAsUserBoundary = insertUs.stream().map(userConverter::toBoundary).collect(Collectors.toList());


        assertThat(returnedFromRequest).containsAll(insertedAsUserBoundary);

    }

    @Test
    public void testModifyUser() {
        // TODO: 03/12/2021 updateUser
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(this.url + "/users",
                insertMe,
                NewUserBoundary.class);

        // Send update to server
        NewUserBoundary updatedVersion = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player + "_Updated_");
        this.client.put(this.url + "/users/" + domainName + "/" + KEYS.USER_EMAIL_Player,
                updatedVersion);


        // get user from server
        UserBoundary returnedFromServer = this.client.getForObject(this.url + "/users/" + "login/" + domainName + "/" + KEYS.USER_EMAIL_Player,
                UserBoundary.class);

        // FIXME // How to Compare UserBoundary to NewUserBoundary? 15/12/2021 Maybe convert one of them to the other, or check specific fields? Consult with everyone..
//        UserController uc = new UserController();
//        assertThat(returnedFromServer).isEqualTo(userCovupdatedVersion);

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


    }

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    public interface KEYS {
        String USER_EMAIL = "UserAPITests_Shahar@userApiTest.com";
        String USERNAME = "UserAPITests_InvokingUser";
        String USER_AVATAR = "UserAPITests_USER_AVATAR";
        String USER_EMAIL_Player = "UserAPITests_Player@userApiTest.com";
        String USERNAME_Player = "UserAPITests_InvokingUser_Player";
        String USER_AVATAR_Player = "UserAPITests_USER_AVATAR_Player";

    }

}