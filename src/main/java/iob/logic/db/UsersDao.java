package iob.logic.db;

import iob.data.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UsersDao extends CrudRepository<UserEntity, String> {
}