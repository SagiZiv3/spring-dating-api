package iob.logic.instancesearching;

import iob.boundaries.helpers.TimeFrame;
import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

public class ByCreationDate extends By {
    private final TimeFrame timeFrame;

    ByCreationDate(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    @Override
    protected String getHumanReadableValue() {
        return String.format("\"createdTimestamp\" between %s and %s", timeFrame.getStartDate(), timeFrame.getEndDate());
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(
                root.get("createdTimestamp"),
                timeFrame.getStartDate(), timeFrame.getEndDate()
        );
    }
}