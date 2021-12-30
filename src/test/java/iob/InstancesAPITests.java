package iob;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.converters.InstanceConverter;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstancesAPITests extends AbstractTestClass {

    private final InstanceConverter instanceConverter;

    @Autowired
    public InstancesAPITests(InstanceConverter instanceConverter) {
        this.instanceConverter = instanceConverter;
    }

    @BeforeEach
    public void createManagerUser() {
        super.createUser(UserRole.MANAGER);
    }

    @ParameterizedTest
    @ArgumentsSource(PaginationGetAllArgumentsProvider.class)
    public void testGetAllInstances(int numInstances, int page, int size, int expectedLength) {
        // GIVEN
        // we have instances in the program
        List<InstanceBoundary> insertedInstances = IntStream.range(0, numInstances)
                .mapToObj(i -> super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_" + i))
                .collect(Collectors.toList());

        // Get all instances
        String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail());
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(String.format("%s?page=%d&size=%d", url, page, size),
                InstanceBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();
        // We should get only one element because the database is empty before this test.
        assertThat(returnedFromRequest.length).isEqualTo(expectedLength);
        // And that one element must be one of the values we inserted.
        assertThat(insertedInstances).containsAll(Arrays.asList(returnedFromRequest));
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

        assertThat(fetchedFromDB).isNotNull();
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
        // check that we don't have any users inside
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


    @ParameterizedTest
    @ArgumentsSource(PaginationGetAllArgumentsProvider.class)
    public void testGetAllParents(int numParents, int page, int size, int expectedLength) {
        // GIVEN we have an instance with parents
        List<InstanceBoundary> parents = IntStream.range(0, numParents)
                .mapToObj(i -> super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_" + i))
                .collect(Collectors.toList());

        InstanceBoundary child = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_child");

        // Bind the parents to the child
        parents.forEach(parent -> {
            String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, parent.getInstanceId().getId(), "children");
            this.client.put(url, child.getInstanceId(), InstanceIdBoundary.class);
        });

        // get child's parents list,
        String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, child.getInstanceId().getId(), "parents");
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(String.format("%s?page=%d&size=%d", url, page, size),
                InstanceBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();
        assertThat(returnedFromRequest.length).isEqualTo(expectedLength);
        assertThat(parents).containsAll(Arrays.asList(returnedFromRequest));


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

    @ParameterizedTest
    @ArgumentsSource(PaginationGetAllArgumentsProvider.class)
    public void testGetAllChildren(int numParents, int page, int size, int expectedLength) {
        // GIVEN we have an instance with parents
        List<InstanceBoundary> children = IntStream.range(0, numParents)
                .mapToObj(i -> super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_" + i))
                .collect(Collectors.toList());
        InstanceBoundary parent = super.createInstance(KEYS.INSTANCE_TYPE, KEYS.INSTANCE_NAME + "_parent");

        // Bind the children to the parent
        children.forEach(child -> {
            String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, parent.getInstanceId().getId(), "children");
            this.client.put(url, child.getInstanceId(), InstanceIdBoundary.class);
        });

        // get obj0's children list,
        String url = super.buildUrl(KEYS.ROOT_NAME, domainName, UserRole.MANAGER.getEmail(), domainName, parent.getInstanceId().getId(), "children");
        InstanceBoundary[] returnedFromRequest = this.client.getForObject(String.format("%s?page=%d&size=%d", url, page, size),
                InstanceBoundary[].class);

        assertThat(returnedFromRequest).isNotNull();
        assertThat(returnedFromRequest.length).isEqualTo(expectedLength);
        assertThat(children).containsAll(Arrays.asList(returnedFromRequest));


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
        String INSTANCE_TYPE = "dummyInstanceType";
        String INSTANCE_NAME = "dummyInstanceName";
    }
}