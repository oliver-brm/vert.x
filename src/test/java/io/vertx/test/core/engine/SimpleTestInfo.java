package io.vertx.test.core.engine;

import org.junit.jupiter.api.TestInfo;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

public class SimpleTestInfo implements TestInfo {

  private final String displayName;
  private final Set<String> tags;
  private final Class<?> testClass;
  private final Method testMethod;

  public SimpleTestInfo(String displayName, Set<String> tags, Class<?> testClass, Method testMethod) {
      this.displayName = displayName;
      this.tags = tags;
      this.testClass = testClass;
      this.testMethod = testMethod;
  }


    @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public Set<String> getTags() {
    return tags;
  }

  @Override
  public Optional<Class<?>> getTestClass() {
    return Optional.ofNullable(testClass);
  }

  @Override
  public Optional<Method> getTestMethod() {
    return Optional.ofNullable(testMethod);
  }
}
