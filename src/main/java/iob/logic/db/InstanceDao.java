package iob.logic.db;

import iob.data.InstanceEntity;
import iob.data.InstancePrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface InstanceDao extends CrudRepository<InstanceEntity, InstancePrimaryKey> {
}