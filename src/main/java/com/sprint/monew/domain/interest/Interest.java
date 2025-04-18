package com.sprint.monew.domain.interest;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "interests")
@NoArgsConstructor
public class Interest {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Convert(converter = StringListConverter.class) // List <-> JSONB 변환
  @Column(name = "keywords", columnDefinition = "jsonb")
  private List<String> keywords;
}
