package de.gesellix.gradle.docker.tasks

import de.gesellix.docker.client.DockerClient
import de.gesellix.docker.engine.EngineResponse
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerRenameTaskSpec extends Specification {

    def project
    def task
    def dockerClient = Mock(DockerClient)

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('dockerRename', type: DockerRenameTask)
        task.dockerClient = dockerClient
    }

    def "delegates rename command to dockerClient and saves result"() {
        given:
        def containerId = 'oldName'
        task.containerId = containerId

        def newName = 'anotherName'
        task.newName = newName

        def expectedResult = new EngineResponse(content: "result")

        when:
        task.rename()

        then:
        1 * dockerClient.rename(containerId, newName) >> expectedResult

        and:
        task.result == expectedResult
    }
}
