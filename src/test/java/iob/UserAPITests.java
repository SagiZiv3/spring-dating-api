package iob;

import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
import iob.boundaries.helpers.UserRoleBoundary;
import iob.controllers.URLS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAPITests extends AbstractTestClass {
    UserConverter userConverter;

    @Autowired
    public UserAPITests(UserConverter userConverter_whereFrom) {
        super("users");
        this.userConverter = userConverter_whereFrom;
    }

    @Test
    public void testCreateUSer() {
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(super.buildUrl(URLS.USERS.CREATE),
                insertMe,
                NewUserBoundary.class);

        String url = super.buildAdminUrl(domainName, ADMIN_KEYS.ADMIN_EMAIL);
        UserBoundary[] returnedFromRequest = this.client.getForObject(url, UserBoundary[].class);

        // FIXME // How to Compare UserBoundary to NewUserBoundary? 15/12/2021 Maybe convert one of them to the other, or check specific fields? Consult with everyone..
        // TODO: 15/12/2021 Test Failed: The returned objects does not contain the User's email (null instead). FIXME
        assertThat(returnedFromRequest).contains(userConverter.toBoundary(insertMe));

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
    public void testGetUser() {
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(super.buildUrl(URLS.USERS.CREATE),
                insertMe,
                NewUserBoundary.class);

        // get user from server
        String url = super.buildUrl("login", domainName, KEYS.USER_EMAIL_Player);
        UserBoundary returnedFromRequest = this.client.getForObject(url, UserBoundary.class);


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
            this.client.postForObject(super.buildUrl(URLS.USERS.CREATE),
                    newUserBoundary,
                    NewUserBoundary.class);

        }
        // Getting all users from server
        String url = super.buildAdminUrl(domainName, ADMIN_KEYS.ADMIN_EMAIL);
        UserBoundary[] returnedFromRequest = this.client.getForObject(url, UserBoundary[].class);


        // Convert inserted elements insertUs to UserBoundary (will enable the usage of containsAll)
        List<UserBoundary> insertedAsUserBoundary = insertUs.stream().map(userConverter::toBoundary).collect(Collectors.toList());


        assertThat(returnedFromRequest).containsAll(insertedAsUserBoundary);

    }

    @Test
    public void testModifyUser() {
        // TODO: 03/12/2021 updateUser
        // Adding user to server
        NewUserBoundary insertMe = new NewUserBoundary(KEYS.USER_EMAIL_Player, UserRoleBoundary.PLAYER, KEYS.USERNAME_Player, KEYS.USER_AVATAR_Player);
        this.client.postForObject(super.buildUrl(URLS.USERS.CREATE),
                insertMe,
                NewUserBoundary.class);

        // Send update to server
        String url = super.buildUrl(domainName, KEYS.USER_EMAIL_Player);
        UserBoundary updatedVersion = new UserBoundary();
        String updatedAvatar = "updated_avatar";
        updatedVersion.setAvatar(updatedAvatar);
        this.client.put(url, updatedVersion);


        // get user from server
        url = super.buildUrl("login", domainName, KEYS.USER_EMAIL_Player);
        UserBoundary returnedFromServer = this.client.getForObject(url, UserBoundary.class);
        assertThat(returnedFromServer.getAvatar()).isEqualTo(updatedAvatar);

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

    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    public interface KEYS {
        String USER_EMAIL_Player = "UserAPITests_Player@userApiTest.com";
        String USERNAME_Player = "UserAPITests_InvokingUser_Player";
        String USER_AVATAR_Player = "UserAPITests_USER_AVATAR_Player";

    }

}