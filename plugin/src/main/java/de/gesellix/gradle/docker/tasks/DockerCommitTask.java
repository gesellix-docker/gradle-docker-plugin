package de.gesellix.gradle.docker.tasks;

import de.gesellix.docker.engine.EngineResponse;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class DockerCommitTask extends GenericDockerTask {

  private final Property<String> containerId;

  @Input
  public Property<String> getContainerId() {
    return containerId;
  }

  private final Property<String> repo;

  @Input
  public Property<String> getRepo() {
    return repo;
  }

  private final Property<String> tag;

  @Optional
  @Input
  public Property<String> getTag() {
    return tag;
  }

  private final Property<String> author;

  @Optional
  @Input
  public Property<String> getAuthor() {
    return author;
  }

  private final Property<String> comment;

  @Optional
  @Input
  public Property<String> getComment() {
    return comment;
  }

  private final Property<Boolean> pauseContainer;

  @Optional
  @Input
  public Property<Boolean> getPauseContainer() {
    return pauseContainer;
  }

  private final Property<String> changes;

  @Optional
  @Input
  public Property<String> getChanges() {
    return changes;
  }

  @Inject
  public DockerCommitTask(ObjectFactory objectFactory) {
    super(objectFactory);
    setDescription("Commit changes to a container");

    containerId = objectFactory.property(String.class);
    repo = objectFactory.property(String.class);
    tag = objectFactory.property(String.class);
    author = objectFactory.property(String.class);
    comment = objectFactory.property(String.class);
    changes = objectFactory.property(String.class);
    pauseContainer = objectFactory.property(Boolean.class);
    pauseContainer.convention(true);
  }

  @TaskAction
  public EngineResponse commit() {
    getLogger().info("docker commit");
    Map<String, Object> map = new HashMap<>(6);
    map.put("repo", getRepo().get());
    map.put("tag", getTag().getOrNull());
    map.put("comment", getComment().getOrNull());
    map.put("author", getAuthor().getOrNull());
    map.put("changes", getChanges().getOrNull());
    map.put("pause", getPauseContainer().getOrElse(true));
    return getDockerClient().commit(getContainerId().get(), map);
  }

  /**
   * @see #getContainerId()
   * @deprecated This setter will be removed, please use the Property instead.
   */
  @Deprecated
  public void setContainerId(String containerId) {
    this.containerId.set(containerId);
  }

  /**
   * @see #getContainerId()
   * @deprecated This setter will be removed, please use the Property instead.
   */
  @Deprecated
  public void setRepo(String repo) {
    this.repo.set(repo);
  }

  /**
   * @see #getContainerId()
   * @deprecated This setter will be removed, please use the Property instead.
   */
  @Deprecated
  public void setTag(String tag) {
    this.tag.set(tag);
  }

  /**
   * @see #getContainerId()
   * @deprecated This setter will be removed, please use the Property instead.
   */
  @Deprecated
  public void setAuthor(String author) {
    this.author.set(author);
  }

  /**
   * @see #getContainerId()
   * @deprecated This setter will be removed, please use the Property instead.
   */
  @Deprecated
  public void setComment(String comment) {
    this.comment.set(comment);
  }

  /**
   * @see #getContainerId()
   * @deprecated This setter will be removed, please use the Property instead.
   */
  @Deprecated
  public void setPauseContainer(Boolean pauseContainer) {
    this.pauseContainer.set(pauseContainer);
  }

  /**
   * @see #getContainerId()
   * @deprecated This setter will be removed, please use the Property instead.
   */
  @Deprecated
  public void setChanges(String changes) {
    this.changes.set(changes);
  }
}
