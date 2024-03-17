package io.vertx.test.core.engine;

import io.vertx.test.core.DetectFileDescriptorLeaks;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.util.function.Consumer;

public class FiledDescriptorLeakTestEngine implements TestEngine {

  public static final String ID = "vertx-file-descriptor-leak-test-engine";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
    TestDescriptor engineDescriptor = new EngineDescriptor(uniqueId, "File Descriptor Leak Test");
    Consumer<MethodSelector> appendTestMethod = methodSelector -> {
      var javaClass = methodSelector.getJavaClass();
      var javaMethod = methodSelector.getJavaMethod();
      if (AnnotationSupport.isAnnotated(javaMethod, DetectFileDescriptorLeaks.class)) {
        engineDescriptor.addChild(new MethodTestDescriptor(javaClass, javaMethod, engineDescriptor));
      }
    };
    discoveryRequest.getSelectorsByType(MethodSelector.class).forEach(appendTestMethod);
    return engineDescriptor;
  }

  @Override
  public void execute(ExecutionRequest request) {
    TestDescriptor root = request.getRootTestDescriptor();
    new FieldDescriptorLeakTestExecutor().execute(request, root);
  }

}
