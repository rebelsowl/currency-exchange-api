package com.example.currencyexchange.data.repository;

import com.example.currencyexchange.data.model.entity.Conversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ConversionRepository extends PagingAndSortingRepository<Conversion, Long> {

    @Query("SELECT c FROM Conversion c WHERE (:id is null or c.id = :id) and (:date is null or c.createDate = :date)")
    Page<Conversion> findByIdAndDate(@Param("id") Long id, @Param("date") Date date, Pageable pageable);

    List<Conversion> findByCreateDate(Date date);

}
