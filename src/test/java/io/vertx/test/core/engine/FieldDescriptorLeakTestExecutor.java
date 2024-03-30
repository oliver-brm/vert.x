package io.vertx.test.core.engine;

import org.junit.jupiter.api.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.platform.commons.support.HierarchyTraversalMode.TOP_DOWN;


public final class FieldDescriptorLeakTestExecutor {

  public void execute(ExecutionRequest request, TestDescriptor descriptor) {
    EngineExecutionListener executionListener = request.getEngineExecutionListener();
    Consumer<TestDescriptor> executeRecursively = desc -> execute(request, desc);

    if (descriptor instanceof EngineDescriptor) {
      executionListener.executionStarted(descriptor);
      descriptor.getChildren().forEach(executeRecursively);
      executionListener.executionFinished(descriptor, TestExecutionResult.successful());
    }
    if (descriptor instanceof MethodTestDescriptor) {
      executeTest(request, (MethodTestDescriptor) descriptor);
    }
  }

  private void executeTest(ExecutionRequest request, MethodTestDescriptor methodTestDescriptor) {
    request.getEngineExecutionListener().executionStarted(methodTestDescriptor);
    TestExecutionResult executionResult = executeTestMethod(methodTestDescriptor);
    request.getEngineExecutionListener().executionFinished(methodTestDescriptor, executionResult);
  }

  private TestExecutionResult executeTestMethod(MethodTestDescriptor descriptor) {
    TestInfo testInfo = new SimpleTestInfo(descriptor.getDisplayName(), Set.of(), descriptor.getDeclaringClass(), descriptor.getTestMethod());
    Class<?> declaringClass = descriptor.getDeclaringClass();
    try {
      Object testObject = declaringClass.getDeclaredConstructor().newInstance();
      Optional<Method> beforeEach = AnnotationSupport.findAnnotatedMethods(declaringClass, BeforeEach.class, TOP_DOWN).stream().findFirst();
      Optional<Method> afterEach = AnnotationSupport.findAnnotatedMethods(declaringClass, AfterEach.class, TOP_DOWN).stream().findFirst();

      // Set up
      if (beforeEach.isPresent()) {
        beforeEach.get().invoke(testObject, testInfo);
      }

      // TODO remove FileDescriptorLeakTestRule
      // TODO add FD leak test around here!
      Object testResult = descriptor.getTestMethod().invoke(testObject);

      // Tear down
      if (afterEach.isPresent()) {
        afterEach.get().invoke(testObject);
      }

    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      return TestExecutionResult.aborted(new RuntimeException("Cannot instantiate test class " + declaringClass.getSimpleName(), e));
    }

    return TestExecutionResult.successful();
  }

}
