package iob.logic.db.dao;

import iob.data.ActivityEntity;
import iob.data.primarykeys.ActivityPrimaryKey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActivitiesDao extends PagingAndSortingRepository<ActivityEntity, ActivityPrimaryKey> {
}