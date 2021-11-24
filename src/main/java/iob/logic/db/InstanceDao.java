package iob.logic.db;

import iob.data.InstanceEntity;
import org.springframework.data.repository.CrudRepository;

public interface InstanceDao extends CrudRepository<InstanceEntity, String> {
}