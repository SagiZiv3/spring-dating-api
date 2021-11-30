package iob.logic.db;

import iob.data.ActivityEntity;
import iob.data.ActivityPrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface ActivitiesDao extends CrudRepository<ActivityEntity, ActivityPrimaryKey> {
}