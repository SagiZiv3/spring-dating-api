package iob.logic.db.Daos;

import iob.data.UserEntity;
import iob.data.primarykeys.UserPrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface UsersDao extends CrudRepository<UserEntity, UserPrimaryKey> {
}