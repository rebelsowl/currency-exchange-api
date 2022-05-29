package com.example.currencyexchange.data.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;

    @CreatedDate
    @Temporal(TemporalType.DATE)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createDate;

//    @LastModifiedDate
//    @Column(name = "updated_at")
//    private Date updatedAt;

//     version

}
