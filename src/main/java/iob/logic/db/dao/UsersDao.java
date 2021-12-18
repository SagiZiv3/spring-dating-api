package iob.logic.db.dao;

import iob.data.UserEntity;
import iob.data.primarykeys.UserPrimaryKey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UsersDao extends PagingAndSortingRepository<UserEntity, UserPrimaryKey> {
}