package iob.logic.instancesearching;

import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.TimeFrame;
import iob.boundaries.helpers.UserIdBoundary;
import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Lazy;

import java.util.Collection;

public abstract class By {
    private Lazy<StringBuilder> stringBuilder;
    private Lazy<Specification<InstanceEntity>> query;

    By() {
        this.query = Lazy.of(this::getSpecification);
        stringBuilder = Lazy.of(() -> new StringBuilder(getHumanReadableValue()));
    }

    protected abstract String getHumanReadableValue();

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

    //<editor-fold desc="Static initializers">
    public static By name(String name) {
        return new ByStringProperty("name", name);
    }

    //<editor-fold desc="Concatenation methods">
    public By and(By by) {
        query = query.map(q -> q.and(by.getSpecification()));
        stringBuilder = stringBuilder.map(sb -> sb.append(", and ").append(by));
        return this;
    }
    //</editor-fold>

    public By or(By by) {
        query = query.map(q -> q.or(by.getSpecification()));
        stringBuilder = stringBuilder.map(sb -> sb.append(", or ").append(by));
        return this;
    }

    public Specification<InstanceEntity> getQuery() {
        return query.get();
    }

    protected abstract Specification<InstanceEntity> getSpecification();

    @Override
    public String toString() {
        return stringBuilder.get().toString();
    }
}