package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class ByActiveState extends By {
    private final Collection<Boolean> allowedActiveStates;

    ByActiveState(Collection<Boolean> allowedActiveStates) {
        this.allowedActiveStates = allowedActiveStates;
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, query, criteriaBuilder) -> root.get("active").in(allowedActiveStates);
    }
}