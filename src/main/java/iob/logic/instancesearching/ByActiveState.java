package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.stream.Collectors;

public class ByActiveState extends By {
    private final Collection<Boolean> allowedActiveStates;

    ByActiveState(Collection<Boolean> allowedActiveStates) {
        this.allowedActiveStates = allowedActiveStates;
    }

    @Override
    public String toString() {
        String allowedStatesString = allowedActiveStates.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return String.format("\"active\" in (%s)", allowedStatesString);
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, query, criteriaBuilder) -> root.get("active").in(allowedActiveStates);
    }
}