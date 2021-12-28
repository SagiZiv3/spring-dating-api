package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

public class ByStringProperty extends By {
    private final String value;
    private final String propertyName;

    ByStringProperty(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    protected final Specification<InstanceEntity> getSpecification() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(propertyName), value
        );
    }
}