package Model;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class ThreadRunner extends BlockJUnit4ClassRunner {
	public ThreadRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
 
    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (method.getAnnotation(Threads.class) != null && method.getAnnotation(Ignore.class) == null) {
            return describeThreadTest(method);
        }
        return super.describeChild(method);
    }
 
    private Description describeThreadTest(FrameworkMethod method) {
        int times = method.getAnnotation(Threads.class).value();
 
        Description description = Description.createSuiteDescription(testName(method) + " [" + times + " threads]", method.getAnnotations());
 
        for (int i = 1; i <= times; i++) {
            description.addChild(Description.createTestDescription(getTestClass().getJavaClass(), "[" + i + "] " + testName(method)));
        }
        return description;
    }
 
    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
 
        if (method.getAnnotation(Threads.class) != null && method.getAnnotation(Ignore.class) == null) {
            runThreadly(methodBlock(method), description, notifier);
        }
        else if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        }
        else {
            runLeaf(methodBlock(method), description, notifier);
        }
    }
 
    private void runThreadly(final Statement statement, Description description, final RunNotifier notifier) {
        ExecutorService es = Executors.newCachedThreadPool();
         
        for (final Description desc : description.getChildren()) {
            es.execute(new Runnable() {
                 
                @Override
                public void run() {
                    runLeaf(statement, desc, notifier);
                }
            });
        }
         
        es.shutdown();
        try {
            es.awaitTermination(30, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
