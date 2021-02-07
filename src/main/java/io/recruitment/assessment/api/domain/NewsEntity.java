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
import javax.persistence.Table;
import java.sql.Timestamp;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table (name = "news")
public class NewsEntity {

  @Id
  private Integer id;

  @NonNull
  private String title;

  private String shortDescription;
  private String longDescription;
  private String imageUrl;

  @NonNull
  private Integer createdBy;

  @CreationTimestamp
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_date", nullable = false, updatable=false)
  private Timestamp createdDate;

  private Integer updatedBy;

  @UpdateTimestamp
  @Column(name = "updated_date")
  private Timestamp updatedAt;
}
