package com.sprint.monew.domain.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;
  @Column(length = 255, nullable = false, unique = true)
  private String email;
  @Column(length = 50, nullable = false, unique = true)
  private String nickname;
  @Column(length = 255, nullable = false)
  private String password;
  @CreatedDate
  @Column(columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
  private Instant createdAt;
  @Column
  private boolean deleted;

  public User(UUID id, String email, String nickname, String password, Instant createdAt,
      boolean deleted) {
    this.email = email;
    this.nickname = nickname;
    this.password = password;
    this.deleted = deleted;
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }
}
