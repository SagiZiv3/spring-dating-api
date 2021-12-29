package iob;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstancesAPITests extends AbstractTestClass {

    private final InstanceConverter instanceConverter;
    private UserBoundary managerUser;

    @Autowired
    public InstancesAPITests(InstanceConverter instanceConverter) {
        this.instanceConverter = instanceConverter;
    }

    @BeforeEach
    public void createManagerUser() {
        managerUser = super.createUser(UserRole.MANAGER);
    }

    @Test
    public void testGetAllInstances() {
        // GIVEN
        // we have instances in the program
        ArrayList<InstanceBoundary> insertedInstances = new ArrayList<>();
        insertedInstances.add(super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_0"));
        insertedInstances.add(super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_1"));
        insertedInstances.add(super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_2"));

        // Get all instances
        String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail());
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "?page=1&size=2",
                InstanceBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();
        // We should get only one element because the database is empty before this test.
        assertThat(returnedFromRequest.length).isEqualTo(1);
        // And that one element must be one of the values we inserted.
        assertThat(insertedInstances).contains(returnedFromRequest[0]);


    }

    @Test
    public void testInsertNewInstance() {
        // GIVEN
        // the server is up ( DO Nothing)

        // WHEN we insert new instance
        InstanceBoundary insertedInstance = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME);
        InstanceIdBoundary id = insertedInstance.getInstanceId();

        // THEN instance would be added
        InstanceBoundary fetchedFromDB = this.client.getForObject(super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), id.getDomain(), id.getId()),
                InstanceBoundary.class,
                instanceConverter.toInstancePrimaryKey(id));

        assertThat(fetchedFromDB).isNotNull();
        assertEquals(fetchedFromDB.getName(), KEYS.INSTANCE_NAME);
        assertEquals(fetchedFromDB.getType(), KEYS.INSTANCE_TYPE);

    }


    @Test
    public void testModifyInstance() {
        // insert old
        InstanceBoundary insertedInstance = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME);
        InstanceIdBoundary id = insertedInstance.getInstanceId();

        // create new
        InstanceBoundary newUpdatedInstance = new InstanceBoundary();
        newUpdatedInstance.setType("MashuAher");
        Location locObj = new Location(5.0, 5.0);
        newUpdatedInstance.setLocation(locObj);

        // Modifying to new instance
        this.client.put(super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), id.getDomain(), id.getId()),
                newUpdatedInstance);


        // Fetching modified instance
        InstanceBoundary fetchedFromDB = this.client.getForObject(super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), id.getDomain(), id.getId()),
                InstanceBoundary.class);

        assertEquals(fetchedFromDB.getLocation(), locObj);
        assertEquals(fetchedFromDB.getType(), "MashuAher");
    }

    @Test
    public void testDeleteAllInstance() {
        // GIVEN
        // we have instances in the program
        super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_0");
        super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_1");
        super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_2");


        // Delete All Instance
        this.client.delete(super.buildAdminUrl(KEYS.ROOT_NAME, domainName, UserRole.ADMIN.getEmail()));

        // Get all instances
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail()),
                InstanceBoundary[].class);
        // check that we dont have any users inside
        assertThat(returnedFromRequest).isEmpty();

    }


    @Test
    public void testBindChildToInstance() {
        // GIVEN we have 2 instances
        InstanceBoundary parent = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_0");
        InstanceBoundary child = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_1");

        //bind 2 objects together
        this.client.put(super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(),
                        domainName, parent.getInstanceId().getId(), "children"),
                child.getInstanceId(),
                InstanceIdBoundary.class);

        // get child's parent list,
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(super.buildUrl(KEYS.ROOT_NAME,
                        domainName, UserRole.MANAGER.getEmail(), domainName, child.getInstanceId().getId(), "parents"),
                InstanceBoundary[].class);

        // check that parent exists
        assertThat(returnedFromRequest).isNotNull();
        assertThat(returnedFromRequest).contains(parent);

        /* ___________________________________________________________________________
        testBindChildToInstance :
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


    @Test
    public void testGetAllParents() {
        // GIVEN we have an instance with parents
        List<InstanceBoundary> parents = new ArrayList<>();
        parents.add(super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_0"));
        parents.add(super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_1"));
        InstanceBoundary child = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_2");

        // Bind the parents to the child
        parents.forEach(parent -> {
            String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, parent.getInstanceId().getId(), "children");
            this.client.put(url, child.getInstanceId(), InstanceIdBoundary.class);
        });

        // get obj0's children list,
        String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, child.getInstanceId().getId(), "parents");
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "?page=1&size=1",
                InstanceBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();
        assertThat(returnedFromRequest.length).isEqualTo(1);
        assertThat(parents).contains(returnedFromRequest[0]);


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

    @Test
    public void testGetAllChildren() {
        // GIVEN we have an instance with parents
        ArrayList<InstanceBoundary> children = new ArrayList<>();
        InstanceBoundary parent = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_0");
        children.add(super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_1"));
        children.add(super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_2"));

        // Bind the children to the parent
        children.forEach(child -> {
            String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, parent.getInstanceId().getId(), "children");
            this.client.put(url, child.getInstanceId(), InstanceIdBoundary.class);
        });

        // get obj0's children list,
        String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, parent.getInstanceId().getId(), "children");
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(url + "?page=1&size=1",
                InstanceBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();
        assertThat(returnedFromRequest.length).isEqualTo(1);
        assertThat(children).contains(returnedFromRequest[0]);


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

    // Enable access from everywhere using: InstanceAPITests.KEYS.{___}
    private interface KEYS {
        String ROOT_NAME = "instances";
        String USER_AVATAR = "InvokingUser";
        String INSTANCE_TYPE = "dummyInstanceType";
        String INSTANCE_NAME = "dummyInstanceName";
    }
}