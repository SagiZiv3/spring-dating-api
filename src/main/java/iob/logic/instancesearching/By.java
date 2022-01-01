package iob.logic.instancesearching;

import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.TimeFrame;
import iob.boundaries.helpers.UserIdBoundary;
import iob.data.InstanceEntity;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public abstract class By {
    @Getter
    private Specification<InstanceEntity> query;
    private StringBuilder stringBuilder;

    By() {
        this.query = null;
    }

    //<editor-fold desc="Static initializer">
    public static By name(String name) {
        return new ByStringProperty("name", name);
    }

    public static By type(String type) {
        return new ByStringProperty("type", type);
    }

    public static By distance(double centerLat, double centerLng, double radius) {
        return new ByDistance(centerLat, centerLng, radius);
    }

    public static By creationDate(TimeFrame timeFrame) {
        return new ByCreationDate(timeFrame);
    }

    public static By childOf(InstanceIdBoundary parentId) {
        return childOf(parentId.getDomain(), parentId.getId());
    }

    public static By childOf(String parentDomain, String parentId) {
        return new ByRelatedInstances(parentDomain, parentId, "parentInstances");
    }

    public static By parentOf(InstanceIdBoundary childId) {
        return parentOf(childId.getDomain(), childId.getId());
    }

    public static By parentOf(String childDomain, String childId) {
        return new ByRelatedInstances(childDomain, childId, "childInstances");
    }

    public static By id(InstanceIdBoundary instanceId) {
        return id(instanceId.getDomain(), instanceId.getId());
    }

    public static By id(String instanceDomain, String instanceId) {
        return new ById(instanceDomain, instanceId);
    }

    public static By userId(UserIdBoundary userId) {
        return userId(userId.getDomain(), userId.getEmail());
    }

    public static By userId(String userDomain, String userEmail) {
        return new ByUserId(userEmail, userDomain);
    }

    public static By activeIn(Collection<Boolean> allowedActiveStates) {
        return new ByActiveState(allowedActiveStates);
    }
    //</editor-fold>

    //<editor-fold desc="Concatenation methods">
    public By and(By by) {
        if (query == null) {
            query = getSpecification();
            stringBuilder = new StringBuilder(toString());
        }
        stringBuilder.append(", and ").append(by.toString());
        query = query.and(by.getSpecification());
        return this;
    }
    //</editor-fold>

    @Override
    public abstract String toString();

    public String getHumanReadableValue() {
        if (stringBuilder == null)
            return toString();
        return stringBuilder.toString();
    }

    protected abstract Specification<InstanceEntity> getSpecification();
}