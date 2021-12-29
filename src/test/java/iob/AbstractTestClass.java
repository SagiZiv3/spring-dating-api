package iob;

import iob.boundaries.NewUserBoundary;
import iob.boundaries.helpers.UserRoleBoundary;
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

    private final String rootName;
    protected String domainName;
    protected RestTemplate client;
    private String url;

    public AbstractTestClass(String rootName) {
        this.rootName = rootName;
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
        NewUserBoundary adminUser = new NewUserBoundary(ADMIN_KEYS.ADMIN_EMAIL, UserRoleBoundary.ADMIN, "_", "_");
        String createUserUrl = String.format("%s/%s", url, "users");
        client.postForObject(createUserUrl, adminUser, NewUserBoundary.class);
    }

    @AfterEach
    public void clearDatabase() {
        client.delete(buildAdminUrl(domainName, ADMIN_KEYS.ADMIN_EMAIL));
    }

    protected String buildAdminUrl(String... parts) {
        return String.format("%s/admin/%s/%s", url, rootName, String.join("/", parts));
    }

    @Test
    public void contextLoads() {
    }

    protected String buildUrl(String... parts) {
        return String.format("%s/%s/%s", url, rootName, String.join("/", parts));
    }

    protected interface ADMIN_KEYS {
        String ADMIN_EMAIL = "sagi@shahar.co.il";
    }
}