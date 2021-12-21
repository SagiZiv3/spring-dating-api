package iob.logic.db.dao;

import iob.data.InstanceEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface InstancesDao extends PagingAndSortingRepository<InstanceEntity, InstancePrimaryKey> {
    List<InstanceEntity> findAllByNameAndActiveEquals(@Param("name") String name, @Param("active") boolean active,
                                                      Pageable pageable);

    List<InstanceEntity> findAllByTypeAndActiveEquals(@Param("type") String type, @Param("active") boolean active,
                                                      Pageable pageable);

    List<InstanceEntity> findAllByCreatedTimestampBetweenAndActiveEquals(@Param("start") Date start, @Param("end") Date end,
                                                                         @Param("active") boolean active, Pageable pageable);

//    List<InstanceEntity> findAllByLocationLocationLatBetweenAndLocationLocationLngBetween(@Param("min_lat") double min_location_locationLat,
//                                                                                          @Param("max_lat") double max_location_locationLat,
//                                                                                          @Param("min_lng") double min_location_locationLng,
//                                                                                          @Param("max_lng") double max_location_locationLng2);

    @Query("select i from InstanceEntity i where sqrt((i.location.locationLat - :center_lat) * (i.location.locationLat - :center_lat) + (i.location.locationLng - :center_lng) * (i.location.locationLng - :center_lng)) <= :radius and i.active = :active")
    List<InstanceEntity> getAllEntitiesInRadiusAndActive(@Param("center_lat") double centerLat,
                                                         @Param("center_lng") double centerLng,
                                                         @Param("radius") double radius,
                                                         @Param("active") boolean active,
                                                         Pageable pageable);
}