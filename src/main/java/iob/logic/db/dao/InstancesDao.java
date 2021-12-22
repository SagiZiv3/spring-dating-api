package iob.logic.db.dao;

import iob.data.InstanceEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;

public interface InstancesDao extends PagingAndSortingRepository<InstanceEntity, InstancePrimaryKey> {

    Page<InstanceEntity> findAllByActiveIn(@Param("allowed_active_states") Collection<Boolean> allowedActiveStates, Pageable pageable);

    Page<InstanceEntity> findAllByNameAndActiveIn(@Param("name") String name, @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                  Pageable pageable);

    Page<InstanceEntity> findAllByTypeAndActiveIn(@Param("type") String type, @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                  Pageable pageable);

    Page<InstanceEntity> findAllByCreatedTimestampBetweenAndActiveIn(@Param("start") Date start, @Param("end") Date end,
                                                                     @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                                     Pageable pageable);

    // Use distance formula √((Cx - Px)^2 + (Cy - Py)^2) to find if a point is in the given radius
    @Query("select i from InstanceEntity i where sqrt((i.location.locationLat - :center_lat) * (i.location.locationLat - :center_lat) + (i.location.locationLng - :center_lng) * (i.location.locationLng - :center_lng)) <= :radius and i.active in :allowed_active_states")
    Page<InstanceEntity> getAllEntitiesInRadiusAndActive(@Param("center_lat") double centerLat,
                                                         @Param("center_lng") double centerLng,
                                                         @Param("radius") double radius,
                                                         @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                         Pageable pageable);
}