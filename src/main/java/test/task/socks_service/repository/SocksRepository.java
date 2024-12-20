package test.task.socks_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import test.task.socks_service.entity.Sock;

@Repository
public interface SocksRepository extends JpaRepository<Sock, Long>, JpaSpecificationExecutor<Sock> {

    @Query(value = "select * from socks where color = :color and cotton_percentage = :cottonPercentage", nativeQuery = true)
    Optional<Sock> findByColorAndCottonPercentage(@Param("color") String color, @Param("cottonPercentage") Double cottonPercentage);

    @Query(value = "select sum(amount) from socks where color = :color and cotton_percentage > :cottonPercentage", nativeQuery = true)
    Integer countSocksByColorAndCottonPercentageGreaterThan(@Param("color") String color, @Param("cottonPercentage") Double cottonPercentage);

    @Query(value = "select sum(amount) from socks where color = :color and cotton_percentage < :cottonPercentage", nativeQuery = true)
    Integer countSocksByColorAndCottonPercentageLessThan(@Param("color") String color, @Param("cottonPercentage") Double cottonPercentage);

    @Query(value = "select sum(amount) from socks where color = :color and cotton_percentage = :cottonPercentage", nativeQuery = true)
    Integer countSocksByColorAndCottonPercentageEqual(@Param("color") String color, @Param("cottonPercentage") Double cottonPercentage);

}
