package iob;

import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.converters.UserConverter;
import iob.boundaries.helpers.UserRoleBoundary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAPITests extends AbstractTestClass {
    UserConverter userConverter;

    @Autowired
    public UserAPITests(UserConverter userConverter_whereFrom) {
        super();
        this.userConverter = userConverter_whereFrom;
    }

    @Test
    public void testCreateUser() {
        // Adding user to server
        UserBoundary insertedUser = super.createUser(UserRole.PLAYER);

        String url = super.buildUrl(KEYS.ROOT_NAME, "login", domainName, insertedUser.getUserId().getEmail());
        UserBoundary returnedFromRequest = this.client.getForObject(url, UserBoundary.class);

        assertThat(returnedFromRequest).isNotNull();
        assertThat(returnedFromRequest).isEqualTo(insertedUser);

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

        // get the admin user from server (it is created before every test)
        String url = super.buildUrl(KEYS.ROOT_NAME, "login", domainName, UserRole.ADMIN.getEmail());
        UserBoundary returnedFromRequest = this.client.getForObject(url, UserBoundary.class);


        assertThat(returnedFromRequest).isNotNull();

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
    public void testModifyUser() {
        // Adding user to server
        UserBoundary insertedUser = super.createUser(UserRole.PLAYER);

        // Send update to server
        String url = super.buildUrl(KEYS.ROOT_NAME, domainName, insertedUser.getUserId().getEmail());
        UserBoundary updatedVersion = new UserBoundary();
        String updatedAvatar = "updated_avatar";
        updatedVersion.setAvatar(updatedAvatar);
        this.client.put(url, updatedVersion);


        // get user from server
        url = super.buildUrl(KEYS.ROOT_NAME, "login", domainName, insertedUser.getUserId().getEmail());
        UserBoundary returnedFromServer = this.client.getForObject(url, UserBoundary.class);
        assertThat(returnedFromServer).isNotNull();
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

    @ParameterizedTest
    @ArgumentsSource(PaginationGetAllArgumentsProvider.class)
    public void testGetAllUsers(int numUsers, int page, int size, int expectedLength) {
        List<UserBoundary> insertedUsers = IntStream.range(0, numUsers)
                .mapToObj(i -> new NewUserBoundary(i + "_" + UserRole.PLAYER.getEmail(),
                        UserRoleBoundary.PLAYER, KEYS.USERNAME_Player + "_" + i, KEYS.USER_AVATAR_Player + "_" + i))
                .map(newUserBoundary -> this.client.postForObject(super.buildUrl(KEYS.ROOT_NAME),
                        newUserBoundary, UserBoundary.class))
                .collect(Collectors.toList());

        // Getting all users from server
        String url = super.buildAdminUrl(KEYS.ROOT_NAME, domainName, UserRole.ADMIN.getEmail());
        UserBoundary[] returnedFromRequest = this.client.getForObject(String.format("%s?page=%d&size=%d", url, page, size),
                UserBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();

        // Filter out the admin user because it is not part of the test.
        List<UserBoundary> relevantUsers = Arrays.stream(returnedFromRequest).
                filter(userBoundary -> !userBoundary.getUserId().getEmail().equals(UserRole.ADMIN.getEmail()))
                .collect(Collectors.toList());

        System.err.println(relevantUsers.size());
        assertThat(relevantUsers.size()).isEqualTo(expectedLength);
        assertThat(insertedUsers).containsAll(relevantUsers);

    }

    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    private interface KEYS {
        String ROOT_NAME = "users";
        String USERNAME_Player = "UserAPITests_InvokingUser_Player";
        String USER_AVATAR_Player = "UserAPITests_USER_AVATAR_Player";

    }

}