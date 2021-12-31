package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public class ByRelatedInstances extends By {
    private final String domain, id;
    private final String relatedInstancesName;

    public ByRelatedInstances(String domain, String id, String relatedInstancesName) {
        this.domain = domain;
        this.id = id;
        this.relatedInstancesName = relatedInstancesName;
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            // Get all the related instances
            Join<Object, Object> childInstances = root.join(relatedInstancesName);
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