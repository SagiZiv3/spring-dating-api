package iob.logic.db.Daos;

import iob.data.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UsersDao extends CrudRepository<UserEntity, String> {
}