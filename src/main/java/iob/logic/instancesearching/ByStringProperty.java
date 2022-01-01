package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

public class ByStringProperty extends By {
    private final String propertyName;
    private final String value;

    ByStringProperty(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    protected String getHumanReadableValue() {
        return String.format("\"%s\" equals \"%s\"", propertyName, value);
    }

    @Override
    protected final Specification<InstanceEntity> getSpecification() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(propertyName), value
        );
    }
}