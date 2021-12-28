package iob.logic;

public interface InstanceWIthBindingsService extends InstancesService {
    void bindToParent(String userDomain, String userEmail, String parentId, String parentDomain, String childId, String childDomain);
}