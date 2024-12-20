package test.task.socks_service.specification;

import org.springframework.data.jpa.domain.Specification;

import test.task.socks_service.entity.Sock;

public class SockSpecification {
    public static Specification<Sock> filterByCottonPercentageRange(Double minCotton, Double maxCotton) {
        return (root, query, criteriaBuilder) -> {
            if (minCotton != null && maxCotton != null) {
                return criteriaBuilder.between(root.get("cottonPercentage"), minCotton, maxCotton);
            } else if (minCotton != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("cottonPercentage"), minCotton);
            } else if (maxCotton != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("cottonPercentage"), maxCotton);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Sock> filterByColor(String color) {
        return (root, query, criteriaBuilder) -> {
            if (color != null && !color.isEmpty()) {
                return criteriaBuilder.equal(root.get("color"), color);
            }
            return criteriaBuilder.conjunction();
        };
    }
}