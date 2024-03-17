package io.vertx.test.core.engine;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;

public final class MethodTestDescriptor extends AbstractTestDescriptor {

  private final Class<?> declaringClass;
  private final Method testMethod;

  public MethodTestDescriptor(Class<?> declaringClass, Method testMethod, TestDescriptor parent) {
    super(
      parent.getUniqueId().append("method", testMethod.getName()),
      testMethod.getName(),
      MethodSource.from(testMethod)
    );
    this.declaringClass = declaringClass;
    this.testMethod = testMethod;
    setParent(parent);
  }

  public Class<?> getDeclaringClass() {
    return declaringClass;
  }

  public Method getTestMethod() {
    return testMethod;
  }

  @Override
  public Type getType() {
    return Type.TEST;
  }
}
