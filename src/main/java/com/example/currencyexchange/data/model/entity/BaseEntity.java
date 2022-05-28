package com.example.currencyexchange.data.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;

//    @Version
//    private int version;
}
