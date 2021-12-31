package iob.logic.instancesearching;

import iob.data.InstanceEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;

public class ByDistance extends By {
    private final double centerLat, centerLng, radius;

    ByDistance(double centerLat, double centerLng, double radius) {
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radius = radius;
    }

    @Override
    public String toString() {
        return String.format("\"location\"'s distance from (%f, %f) is less than or equal to %f",
                centerLat, centerLng, radius);
    }

    @Override
    protected Specification<InstanceEntity> getSpecification() {
        return (root, query, criteriaBuilder) -> {
            // Get lat, lng
            Expression<Double> lat = root.get("location").get("locationLat");
            Expression<Double> lng = root.get("location").get("locationLng");
            // Calculate lat-centerLat
            Expression<Double> latDiff = criteriaBuilder.diff(lat, centerLat);
            // Calculate lng-centerLng
            Expression<Double> lngDiff = criteriaBuilder.diff(lng, centerLng);
            // Calculate (lat-centerLat) * (lat-centerLat)
            Expression<Double> latDiffSquared = criteriaBuilder.prod(latDiff, latDiff);
            // Calculate (lng-centerLng) * (lng-centerLng)
            Expression<Double> lngDiffSquared = criteriaBuilder.prod(lngDiff, lngDiff);
            // Calculate sum
            Expression<Double> sumSquares = criteriaBuilder.sum(latDiffSquared, lngDiffSquared);
            // Get all the instances with distance less than or equals to the given radius.
            return criteriaBuilder.le(
                    // We can use either sqrt or square the radius.
                    // I chose to square the radius because it is fewer calculations for the database.
                    sumSquares,
                    Math.pow(radius, 2)
            );
        };
    }
}