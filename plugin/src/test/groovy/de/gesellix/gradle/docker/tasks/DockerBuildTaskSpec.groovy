package de.gesellix.gradle.docker.tasks

import de.gesellix.docker.client.DockerClient
import de.gesellix.docker.client.image.BuildConfig
import de.gesellix.docker.client.image.BuildResult
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

class DockerBuildTaskSpec extends Specification {

    def project
    def task
    def dockerClient = Mock(DockerClient)

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('dockerBuild', type: DockerBuildTask)
        task.dockerClient = dockerClient
    }

    @Unroll
    def "depends on tar task to archive buildContextDirectory (as #type)"() {
        given:
        task.buildContextDirectory = baseDir
        task.imageName = "user/imageName"

        when:
        task.configure()

        then:
        project.getTasksByName("tarBuildcontextForDockerBuild", false).size() == 1
        and:
        task.dependsOn.any { it == project.getTasksByName("tarBuildcontextForDockerBuild", false).first() }

        where:
        baseDir                                                                             | type
        parentDir(getClass().getResource('/docker/Dockerfile'))                             | File
        parentDir(getClass().getResource('/docker/Dockerfile')).absolutePath                | String
        wrapInClosure(parentDir(getClass().getResource('/docker/Dockerfile')).absolutePath) | 'lazily resolved String'
    }

    def "tar task must run after dockerBuild dependencies"() {
        URL dockerfile = getClass().getResource('/docker/Dockerfile')
        def baseDir = new File(dockerfile.toURI()).parentFile

        given:
        def buildTaskDependency = project.task('buildTaskDependency', type: TestTask)
        task.dependsOn buildTaskDependency
        task.buildContextDirectory = baseDir
        task.imageName = "busybox"

        when:
        task.configure()

        then:
        project.tasks.findByName("dockerBuild").getDependsOn().contains project.tasks.findByName("buildTaskDependency")
        and:
        def tarBuildcontextForDockerBuild = project.tasks.findByName("tarBuildcontextForDockerBuild")
        tarBuildcontextForDockerBuild.getMustRunAfter().mutableValues.contains project.tasks.findByName("buildTaskDependency")
    }

    def "tar of buildContextDirectory contains buildContextDirectory"() {
        URL dockerfile = getClass().getResource('/docker/Dockerfile')
        def baseDir = new File(dockerfile.toURI()).parentFile

        given:
        task.buildContextDirectory = baseDir
        task.imageName = "user/imageName"

        when:
        task.configure()

        then:
        def tarOfBuildcontextTask = project.getTasksByName("tarBuildcontextForDockerBuild", false).first()
//    tarOfBuildcontextTask.destinationDir == new File("${tarOfBuildcontextTask.getTemporaryDir()}")

//    and:
//    tarOfBuildcontextTask.inputs.files.asPath == project.fileTree(baseDir).asPath

        and:
        tarOfBuildcontextTask.outputs.files.asPath == task.targetFile.absolutePath
    }

    def "delegates to dockerClient with buildContext"() {
        def inputStream = new FileInputStream(File.createTempFile("docker", "test"))

        given:
        task.buildContext = inputStream
        task.imageName = "imageName"

        when:
        task.build()

        then:
        1 * dockerClient.build(inputStream, new BuildConfig(query: [rm: true, t: "imageName"])) >>
        new BuildResult(imageId: "4711")

        and:
        task.outputs.files.isEmpty()
    }

    def "delegates to dockerClient with buildContext and buildParams"() {
        def inputStream = new FileInputStream(File.createTempFile("docker", "test"))

        given:
        task.buildContext = inputStream
        task.buildParams = [rm: true, dockerfile: './custom.Dockerfile']
        task.imageName = "imageName"

        when:
        task.build()

        then:
        1 * dockerClient.build(inputStream, new BuildConfig(query: [rm: true, t: "imageName", dockerfile: './custom.Dockerfile'])) >>
        new BuildResult(imageId: "4711")

        and:
        task.outputs.files.isEmpty()
    }

    def "delegates to dockerClient with buildContext and buildOptions"() {
        def inputStream = new FileInputStream(File.createTempFile("docker", "test"))

        given:
        task.buildContext = inputStream
        task.buildOptions = [EncodedRegistryConfig: [foo: [:]]]
        task.imageName = "imageName"

        when:
        task.build()

        then:
        1 * dockerClient.build(inputStream, new BuildConfig(query: [rm: true, t: "imageName"], options: [EncodedRegistryConfig: [foo: [:]]])) >>
        new BuildResult(imageId: "4711")

        and:
        task.outputs.files.isEmpty()
    }

    def "does not override rm build param if given"() {
        def inputStream = new FileInputStream(File.createTempFile("docker", "test"))

        given:
        task.buildContext = inputStream
        task.buildParams = [rm: false, dockerfile: './custom.Dockerfile']
        task.imageName = "imageName"

        when:
        task.build()

        then:
        1 * dockerClient.build(inputStream, new BuildConfig(query: [rm: false, t: "imageName", dockerfile: './custom.Dockerfile'])) >>
        new BuildResult(imageId: "4711")

        and:
        task.outputs.files.isEmpty()
    }

    def "delegates to dockerClient with buildContext (with logs)"() {
        def inputStream = new FileInputStream(File.createTempFile("docker", "test"))

        given:
        task.buildContext = inputStream
        task.imageName = "imageName"
        task.enableBuildLog = true

        when:
        task.build()

        then:
        1 * dockerClient.buildWithLogs(inputStream, new BuildConfig(query: [rm: true, t: "imageName"])) >>
        new BuildResult(imageId: "4711", log: [])

        and:
        task.outputs.files.isEmpty()
    }

    def "delegates to dockerClient with buildContext and buildParams (with logs)"() {
        def inputStream = new FileInputStream(File.createTempFile("docker", "test"))

        given:
        task.buildContext = inputStream
        task.buildParams = [rm: true, dockerfile: './custom.Dockerfile']
        task.imageName = "imageName"
        task.enableBuildLog = true

        when:
        task.build()

        then:
        1 * dockerClient.buildWithLogs(inputStream, new BuildConfig(query: [rm: true, t: "imageName", dockerfile: './custom.Dockerfile'])) >>
        new BuildResult(imageId: "4711", log: [])

        and:
        task.outputs.files.isEmpty()
    }

    def "normalizedImageName should match [a-z0-9-_.]"() {
        expect:
        task.getNormalizedImageName() ==~ "[a-z0-9-_\\.]+"
    }

    def parentDir(URL resource) {
        new File(resource.toURI()).parentFile
    }

    def wrapInClosure(value) {
        new Closure(null) {

            @Override
            Object call() {
                value
            }
        }
    }
}
