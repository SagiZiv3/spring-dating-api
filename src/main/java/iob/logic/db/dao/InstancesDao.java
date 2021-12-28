package iob.logic.db.dao;

import iob.data.InstanceEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;

public interface InstancesDao extends PagingAndSortingRepository<InstanceEntity, InstancePrimaryKey>, JpaSpecificationExecutor<InstanceEntity> {

    Page<InstanceEntity> findAllByActiveIn(@Param("allowed_active_states") Collection<Boolean> allowedActiveStates, Pageable pageable);

    Page<InstanceEntity> findAllByNameAndActiveIn(@Param("name") String name, @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                  Pageable pageable);

    Page<InstanceEntity> findAllByTypeAndActiveIn(@Param("type") String type, @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                  Pageable pageable);

    Page<InstanceEntity> findAllByCreatedTimestampBetweenAndActiveIn(@Param("start") Date start, @Param("end") Date end,
                                                                     @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                                     Pageable pageable);

    // Source: https://stackoverflow.com/a/42586604/9977758
    Page<InstanceEntity> findAllByParentInstancesIdAndParentInstancesDomainAndActiveIn(@Param("id") long parentId,
                                                                                       @Param("domain") String parentDomain,
                                                                                       @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                                                       Pageable pageable);

    Page<InstanceEntity> findAllByChildInstancesIdAndChildInstancesDomainAndActiveIn(@Param("id") long childId,
                                                                                     @Param("domain") String childDomain,
                                                                                     @Param("allowed_active_states") Collection<Boolean> active,
                                                                                     Pageable pageable);

    // Use distance formula √((Cx - Px)^2 + (Cy - Py)^2) to find if a point is in the given radius
    @Query("select i from InstanceEntity i where sqrt((i.location.locationLat - :center_lat) * (i.location.locationLat - :center_lat) + (i.location.locationLng - :center_lng) * (i.location.locationLng - :center_lng)) <= :radius and i.active in :allowed_active_states")
    Page<InstanceEntity> getAllEntitiesInRadiusAndActive(@Param("center_lat") double centerLat,
                                                         @Param("center_lng") double centerLng,
                                                         @Param("radius") double radius,
                                                         @Param("allowed_active_states") Collection<Boolean> allowedActiveStates,
                                                         Pageable pageable);

//    Page<InstanceEntity> findAllByTypeEqualsAndCreatedByDomainEqualsAndCreatedByEmailEquals(
//            @NonNull String type, String createdBy_domain, String createdBy_email, Pageable pageable);
}