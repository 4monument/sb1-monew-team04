package com.sprint.monew.domain.interest;

import com.sprint.monew.common.util.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

  @Column(name = "keywords", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> keywords;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public Interest(String name, List<String> keywords) {
    this.name = name;
    this.keywords = keywords;
    this.createdAt = Instant.now();
  }

  public void updateKeywords(List<String> keywords) {
    this.keywords = keywords;
  }
}
