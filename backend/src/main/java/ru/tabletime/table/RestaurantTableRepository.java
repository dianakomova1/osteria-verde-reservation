package ru.tabletime.table;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByCapacityGreaterThanEqualOrderByCapacityAscTableNumberAsc(Integer guestsCount);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t from RestaurantTable t
            where t.capacity >= :guestsCount
            order by t.capacity, t.tableNumber
            """)
    List<RestaurantTable> findSuitableForUpdate(@Param("guestsCount") Integer guestsCount);
}
