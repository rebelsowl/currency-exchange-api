package com.example.currencyexchange.data.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Conversions")
public class Conversion extends BaseEntity {

    @Column(name = "sourceCurrency")
    private String sourceCurrency;

    @Column(name = "sourceAmount")
    private BigDecimal sourceAmount;

    @Column(name = "targetCurrency")
    private String targetCurrency;

    @Column(name = "targetAmount")
    private BigDecimal targetAmount;

}
