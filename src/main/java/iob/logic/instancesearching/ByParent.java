package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public class ByParent extends By {
    private final String domain, id;

    ByParent(String domain, String id) {
        this.domain = domain;
        this.id = id;
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Object, Object> parentInstances = root.join("parentInstances");
            Predicate predicate1 = criteriaBuilder.equal(
                    parentInstances.get("domain"),
                    domain
            );
            Predicate predicate2 = criteriaBuilder.equal(
                    parentInstances.get("id"),
                    id
            );
            return criteriaBuilder.and(predicate1, predicate2);
        };
    }
}