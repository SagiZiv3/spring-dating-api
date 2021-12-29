package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public class ByChild extends By {
    private final String domain, id;

    ByChild(String domain, String id) {
        this.domain = domain;
        this.id = id;
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            // Get all the child instances
            Join<Object, Object> childInstances = root.join("childInstances");
            Predicate domainPredicate = criteriaBuilder.equal(
                    childInstances.get("domain"),
                    domain
            );
            Predicate idPredicate = criteriaBuilder.equal(
                    childInstances.get("id"),
                    id
            );
            return criteriaBuilder.and(domainPredicate, idPredicate);
        };
    }
}