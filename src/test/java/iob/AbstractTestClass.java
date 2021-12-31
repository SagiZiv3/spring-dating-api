package iob;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.NewUserBoundary;
import iob.boundaries.UserBoundary;
import iob.boundaries.helpers.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

// Source: https://www.baeldung.com/java-beforeall-afterall-non-static
// TL;DR Make sure the class would be created only once and not every test method
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTestClass {
    protected String domainName;
    protected RestTemplate client;
    private String url;

    public AbstractTestClass() {
        client = new RestTemplate();
    }

    // get random port used by server
    @LocalServerPort
    public void updateBaseUrl(int port) {
        this.url = String.format("http://localhost:%d/iob", port);
    }

    @Value("${spring.application.name:dummy}")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @BeforeEach
    public void createAdminUser() {
        createUser(UserRole.ADMIN);
    }

    @AfterEach
    public void clearDatabase() {
        client.delete(buildAdminUrl("instances", domainName, UserRole.ADMIN.getEmail()));
        client.delete(buildAdminUrl("activities", domainName, UserRole.ADMIN.getEmail()));
        client.delete(buildAdminUrl("users", domainName, UserRole.ADMIN.getEmail()));
    }

    @Test
    public void contextLoads() {
    }

    protected UserBoundary createUser(UserRole role) {
        NewUserBoundary user = new NewUserBoundary(role.getEmail(), role.getUserRoleBoundary(), "_", "_");
        return client.postForObject(buildUrl("users"), user, UserBoundary.class);
    }

    protected InstanceBoundary createInstance(String instanceType, String instanceName) {
        InstanceBoundary addMe = new InstanceBoundary();
        addMe.setName(instanceName);
        addMe.setType(instanceType);
        addMe.setLocation(new Location(5.0, 5.0));

        // post instance on system
        return this.client.postForObject(buildUrl("instances", domainName, UserRole.MANAGER.getEmail()),
                addMe,
                InstanceBoundary.class);
    }

    protected String buildUrl(String rootName, String... parts) {
        return String.format("%s/%s/%s", url, rootName, String.join("/", parts));
    }

    protected String buildAdminUrl(String rootName, String... parts) {
        return String.format("%s/admin/%s/%s", url, rootName, String.join("/", parts));
    }
}