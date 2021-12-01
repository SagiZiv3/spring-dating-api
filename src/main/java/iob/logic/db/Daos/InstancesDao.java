package iob.logic.db.Daos;

import iob.data.InstanceEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface InstancesDao extends CrudRepository<InstanceEntity, InstancePrimaryKey> {
}