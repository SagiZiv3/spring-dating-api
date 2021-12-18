package iob.logic.db.dao;

import iob.data.InstanceEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstancesDao extends PagingAndSortingRepository<InstanceEntity, InstancePrimaryKey> {
}