package iob;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.UserBoundary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

//    @Test
//    public void testInsertActivity() {
//        // Create activity boundary
//        ActivityBoundary activityBoundary = new ActivityBoundary();
//        activityBoundary.setInstance(new InstanceIdWrapper(instanceBoundary.getInstanceId()));
//        activityBoundary.setInvokedBy(new InvokedByBoundary(playerBoundary.getUserId()));
//        activityBoundary.setType("SomeCoolType- ActivityApiTest");
//
//        // post activity ( Get it back with the ID)
//        activityBoundary = this.client.postForObject(super.buildUrl(KEYS.ROOT_NAME),
//                activityBoundary,
//                ActivityBoundary.class);
//
//        // Export all activities in domain
//        ActivityBoundary[] returnedFromRequest = this.client.getForObject(super.buildAdminUrl(KEYS.ROOT_NAME, domainName, UserRole.ADMIN.getEmail()),
//                ActivityBoundary[].class);
//
//        // assert addMe is within the activities.
//        assertThat(returnedFromRequest).contains(activityBoundary);
//        /*
//        CRUD:
//            POST
//        URL:
//            http://localhost:8091/iob/activities
//
//        Content-Type: (expected object):
//            application/json
//
//        Accept (returns object):
//
//        BODY:
//                {
//            "type":"demoActivityType",
//            "instance":{
//            "instanceId":{
//            "domain":"2022a.demo",
//            "id":"352"
//            }
//            },
//            "invokedBy":{
//            "userId":{
//            "domain":"2022a.demo",
//            "email":"user3@demo.com"
//            }
//            },
//            "activityAttributes":{
//            "key1":"can be set to any value you wish",
//            "key2":{
//            "key2Subkey1":"can be nested json"
//            }
//            }
//            }
//         */
//    }


    // Enable access from everywhere using: UserAPITests.KEYS.{___}
    private interface KEYS {
        String ROOT_NAME = "activities";
    }
}