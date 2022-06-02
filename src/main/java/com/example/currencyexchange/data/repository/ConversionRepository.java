package com.example.currencyexchange.data.repository;

import com.example.currencyexchange.data.model.entity.Conversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface ConversionRepository extends PagingAndSortingRepository<Conversion, Long> {

    // if input(s) is null where clause evaluates to true
    @Query("SELECT c FROM Conversion c WHERE (:id is null or c.id = :id) and ((:startDate is null or :endDate is null) or (c.createDate >= :startDate and c.createDate < :endDate))")
    Page<Conversion> findByIdAndDate(@Param("id") Long id, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate, Pageable pageable);


}
