package de.gesellix.gradle.docker.tasks

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class DockerNetworkDisconnectTask extends GenericDockerTask {

  @Input
  Property<String> networkName

  /**
   * @deprecated This setter will be removed, please use the Property<> instead.
   * @see #getNetworkName()
   */
  @Deprecated
  void setNetworkName(String networkName) {
    this.networkName.set(networkName)
  }

  @Input
  Property<String> containerName

  /**
   * @deprecated This setter will be removed, please use the Property<> instead.
   * @see #getContainerName()
   */
  @Deprecated
  void setContainerName(String containerName) {
    this.containerName.set(containerName)
  }

  @Internal
  def response

  @Inject
  DockerNetworkDisconnectTask(ObjectFactory objectFactory) {
    super(objectFactory)
    description = "Disconnects container from a network"

    networkName = objectFactory.property(String)
    containerName = objectFactory.property(String)
  }

  @TaskAction
  def disconnectNetwork() {
    logger.info "docker network disconnect"
    response = getDockerClient().disconnectNetwork(getNetworkName().get(), getContainerName().get())
  }
}
