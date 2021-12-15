package iob;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.UserRoleBoundary;
import iob.controllers.UserController;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAPITests {
    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    public interface KEYS {
        final String USER_EMAIL = "UserAPITests_Shahar@userApiTest.com";
        final String USERNAME = "UserAPITests_InvokingUser";
        final String USER_AVATAR = "UserAPITests_USER_AVATAR";
        final String USER_EMAIL_Player = "UserAPITests_Player@userApiTest.com";
        final String USERNAME_Player = "UserAPITests_InvokingUser_Player";
        final String USER_AVATAR_Player = "UserAPITests_USER_AVATAR_Player";

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

    @Test
    public void testCreateUSer(){
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(this.url + "/users",
                insertMe,
                NewUserBoundary.class);


        NewUserBoundary[] returnedFromRequest = this.client.getForObject(this.url + "/admin/users/"+ "/" + domainName + "/" + KEYS.USER_EMAIL,
                                    NewUserBoundary[].class);

        // FIXME // How to Compare UserBoundary to NewUserBoundary? 15/12/2021 Maybe convert one of them to the other, or check specific fields? Consult with everyone..
        // TODO: 15/12/2021 Test Failed: The returned objects does not contain the User's email (null instead). FIXME
        assertThat(returnedFromRequest).contains(insertMe);

/*
        System.out.println("________________________________________________________ HEYYY");

        for (NewUserBoundary usr: results) {
            System.out.println(usr);
        }
        assertEquals(fetchedFromDB.getLocation(), locObj);

        System.out.println(userActivating);

        System.out.println("____________________________________________test____________ THERE");
*/
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
    public void testGetUser(){
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(this.url + "/users",
                insertMe,
                NewUserBoundary.class);

        // get user from server
        NewUserBoundary returnedFromRequest = this.client.getForObject(this.url + "/users/"+ "/login/" + domainName + "/" + KEYS.USER_EMAIL_Player,
                NewUserBoundary.class);

        // FIXME // How to Compare UserBoundary to NewUserBoundary? 15/12/2021 Maybe convert one of them to the other, or check specific fields? Consult with everyone..
        // TODO: 15/12/2021 Test Failed: The returned objects does not contain the User's email (null instead). FIXME
        assertThat(returnedFromRequest).isEqualTo(insertMe);

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
    public void testGetAllUsers()
    {
        ArrayList<NewUserBoundary> demo1 = new ArrayList<>();
        demo1.add(new NewUserBoundary(KEYS.USER_EMAIL_Player+ "_0", UserRoleBoundary.PLAYER, KEYS.USERNAME_Player+ "_0", KEYS.USER_AVATAR_Player + "_0"));
        demo1.add(new NewUserBoundary(KEYS.USER_EMAIL_Player+ "_1", UserRoleBoundary.PLAYER, KEYS.USERNAME_Player+ "_1", KEYS.USER_AVATAR_Player + "_1"));
        demo1.add(new NewUserBoundary(KEYS.USER_EMAIL_Player+ "_2", UserRoleBoundary.PLAYER, KEYS.USERNAME_Player+ "_2", KEYS.USER_AVATAR_Player + "_2"));
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(this.url + "/users",
                insertMe,
                NewUserBoundary.class);


        NewUserBoundary[] returnedFromRequest = this.client.getForObject(this.url + "/admin/users/" + domainName + "/" + KEYS.USER_EMAIL,
                NewUserBoundary[].class);

        // TODO: 15/12/2021 Test Failed: The returned objects does not contain the User's email (null instead). FIXME
        assertThat(returnedFromRequest).containsAll(demo1);

    }


    @Test
    public void testModifyUser(){
        // TODO: 03/12/2021 updateUser
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(this.url + "/users",
                insertMe,
                NewUserBoundary.class);

        // Send update to server
        NewUserBoundary updatedVersion = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player + "_Updated_");
        this.client.put(this.url + "/users/"  + domainName +  "/" + KEYS.USER_EMAIL_Player,
                updatedVersion);


        // get user from server
        UserBoundary returnedFromServer = this.client.getForObject(this.url + "/users/"+ "login/" + domainName + "/" + KEYS.USER_EMAIL_Player,
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

}






