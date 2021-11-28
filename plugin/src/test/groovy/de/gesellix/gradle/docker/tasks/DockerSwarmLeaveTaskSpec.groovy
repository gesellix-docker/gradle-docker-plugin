package de.gesellix.gradle.docker.tasks

import de.gesellix.docker.client.DockerClient
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerSwarmLeaveTaskSpec extends Specification {

  def project
  def task
  def dockerClient = Mock(DockerClient)

  def setup() {
    project = ProjectBuilder.builder().build()
    task = project.task('leaveSwarm', type: DockerSwarmLeaveTask)
    task.dockerClient = dockerClient
  }

  def "delegates to dockerClient and saves result"() {
    given:
    task.force = true

    when:
    task.leaveSwarm()

    then:
    1 * dockerClient.leaveSwarm(true)
  }
}
