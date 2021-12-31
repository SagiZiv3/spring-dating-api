package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class ById extends By {
    private final String domain, id;

    public ById(String domain, String id) {
        this.domain = domain;
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("\"id\" is {domain=%s, id=%s}", domain, id);
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, query, criteriaBuilder) -> {
            Predicate domainEquals = criteriaBuilder.equal(root.get("domain"), domain);
            Predicate idEquals = criteriaBuilder.equal(root.get("id"), id);
            return criteriaBuilder.and(domainEquals, idEquals);
        };
    }
}