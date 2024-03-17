package io.vertx.test.core.engine;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

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
    Class<?> declaringClass = descriptor.getDeclaringClass();
    try {
      Object testObject = declaringClass.getDeclaredConstructor().newInstance();
      // TODO run setup
      // TODO add FD leak test around here!
      Object testResult = descriptor.getTestMethod().invoke(testObject);
      // TODO run tear down

    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      return TestExecutionResult.aborted(new RuntimeException("Cannot instantiate test class " + declaringClass.getSimpleName(), e));
    }

    return TestExecutionResult.successful();
  }

}
