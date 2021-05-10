package de.gesellix.gradle.docker.tasks;

import de.gesellix.docker.engine.EngineResponse;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class DockerVersionTask extends GenericDockerTask {

  private EngineResponse version;

  @Internal
  public EngineResponse getVersion() {
    return version;
  }

  @Inject
  public DockerVersionTask(ObjectFactory objectFactory) {
    super(objectFactory);
    setDescription("Show the Docker version information");
  }

  @TaskAction
  public void version() {
    getLogger().info("docker version");
    version = getDockerClient().version();
  }
}
