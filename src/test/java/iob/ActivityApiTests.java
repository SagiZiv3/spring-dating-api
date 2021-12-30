package iob;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.helpers.InstanceIdWrapper;
import iob.boundaries.helpers.InvokedByBoundary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActivityApiTests extends AbstractTestClass {
    private InstanceBoundary instanceBoundary;
    private UserBoundary playerBoundary;

    @BeforeEach
    public void createInstance() {
        super.createUser(UserRole.MANAGER);
        instanceBoundary = super.createInstance("invokingActivityType", "invokingActivity");
        playerBoundary = super.createUser(UserRole.PLAYER);
    }

    @Test
    public void testInsertActivity() {
        // Create activity boundary
        ActivityBoundary activityBoundary = new ActivityBoundary();
        activityBoundary.setInstance(new InstanceIdWrapper(instanceBoundary.getInstanceId()));
        activityBoundary.setInvokedBy(new InvokedByBoundary(playerBoundary.getUserId()));
        activityBoundary.setType("testInvokeActivity");

        // post activity ( Get it back with the ID)
        Object result = this.client.postForObject(super.buildUrl(KEYS.ROOT_NAME),
                activityBoundary,
                Object.class);

        assertThat(result).isNotNull();

        // Export all activities in domain
        ActivityBoundary[] returnedFromRequest = this.client.getForObject(super.buildAdminUrl(KEYS.ROOT_NAME, domainName, UserRole.ADMIN.getEmail()),
                ActivityBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();
        assertThat(returnedFromRequest.length).isEqualTo(1);
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

    @Test
    public void testInsertActivityFailsBecauseUnknownType() {
        // Create activity boundary
        ActivityBoundary activityBoundary = new ActivityBoundary();
        activityBoundary.setInstance(new InstanceIdWrapper(instanceBoundary.getInstanceId()));
        activityBoundary.setInvokedBy(new InvokedByBoundary(playerBoundary.getUserId()));
        activityBoundary.setType("sOmE rAnDoM tYpE");

        // post activity ( Get it back with the ID)
        try {
            this.client.postForEntity(super.buildUrl(KEYS.ROOT_NAME),
                    activityBoundary,
                    Object.class);

            fail("Error was not thrown for unknown type");
        } catch (HttpStatusCodeException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    private interface KEYS {
        String ROOT_NAME = "activities";
    }
}