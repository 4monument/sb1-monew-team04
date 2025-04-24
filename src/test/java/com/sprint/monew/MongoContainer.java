package com.sprint.monew;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoContainer extends MongoDBContainer {

  private static final String IMAGE_VERSION = "mongo:6.0";
  private static final MongoContainer INSTANCE = new MongoContainer();

  private MongoContainer() {
    super(DockerImageName.parse(IMAGE_VERSION));
  }

  public static MongoContainer getInstance() {
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
