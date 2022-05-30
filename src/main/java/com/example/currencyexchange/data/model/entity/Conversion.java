package com.example.currencyexchange.data.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Conversions")
public class Conversion extends BaseEntity {
    private String sourceCurrency;
    private BigDecimal sourceAmount;
    private String targetCurrency;
    private BigDecimal targetAmount;

}
