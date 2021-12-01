package iob.logic.db.Daos;

import iob.data.ActivityEntity;
import iob.data.primarykeys.ActivityPrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface ActivitiesDao extends CrudRepository<ActivityEntity, ActivityPrimaryKey> {
}