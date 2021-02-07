package io.recruitment.assessment.api.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table (name = "ORDERZ_PRODUCT")
public class OrderzProduct {

    @Id
    private Integer id;

    @NonNull
    private Integer quantity;

    @NonNull
    private Integer createdBy;

    @ManyToOne
    private Orderz orderz;

    @ManyToOne
    private Product product;

    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_date", nullable = false, updatable=false)
    private Timestamp createdAt;

    private Integer updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Timestamp updatedAt;
}
