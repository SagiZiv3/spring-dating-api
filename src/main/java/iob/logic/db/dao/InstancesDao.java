package iob.logic.db.dao;

import iob.data.InstanceEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface InstancesDao extends CrudRepository<InstanceEntity, InstancePrimaryKey> {
}