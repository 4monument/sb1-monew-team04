package com.sprint.monew;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

  private static final String IMAGE_VERSION = "postgres:16-alpine";
  private static final PostgresContainer INSTANCE =
      new PostgresContainer();

  private PostgresContainer() {
    super(DockerImageName.parse(IMAGE_VERSION));
    this.withDatabaseName("test-db")
        .withUsername("test-user")
        .withPassword("test-password");
  }

  public static PostgresContainer getInstance() {
    return INSTANCE;
  }

  @Override
  public void start() {
    if (!isRunning()) {
      super.start();
    }
  }

  @Override
  public void stop() {
  }
}