package io.recruitment.assessment.api.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table (name = "users")
public class User {

  @Id
  private Integer id;

  @NonNull
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_type")
  private RoleEntity.RoleType roleType;

  private String apiKey;
}
