package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class ByUserId extends By {
    private final String email, domain;

    public ByUserId(String email, String domain) {
        this.email = email;
        this.domain = domain;
    }

    @Override
    public String toString() {
        return String.format("\"createBy\" is {user email: %s, user domain: %s}", email, domain);
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, query, criteriaBuilder) -> {
            Predicate domainEquals = criteriaBuilder.equal(root.get("createdBy").get("domain"), domain);
            Predicate emailEquals = criteriaBuilder.equal(root.get("createdBy").get("email"), email);
            return criteriaBuilder.and(domainEquals, emailEquals);
        };
    }
}