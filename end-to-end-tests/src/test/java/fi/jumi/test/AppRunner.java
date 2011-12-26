// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.test;

import fi.jumi.launcher.JumiLauncher;
import org.apache.commons.io.FileUtils;
import org.junit.ComparisonFailure;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.io.*;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;

public class AppRunner implements MethodRule {

    private static final String NEWLINE = System.getProperty("line.separator");

    // TODO: use a proper sandbox utility
    private final File sandboxDir = new File(TestEnvironment.getSandboxDir(), UUID.randomUUID().toString());

    private final JumiLauncher launcher = new JumiLauncher();
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    public void runTests(Class<?> clazz) throws IOException, InterruptedException {
        runTests(clazz.getName());
    }

    public void runTests(String testsToInclude) throws IOException, InterruptedException {
        launcher.addToClassPath(TestEnvironment.getSampleClasses());
        launcher.setTestsToInclude(testsToInclude);
        launcher.start();

        TextUI ui = new TextUI(new PrintStream(out), new PrintStream(out), launcher);
        ui.runToCompletion();

        synchronized (System.out) {
            System.out.println("--- TEXT UI OUTPUT ----");
            System.out.println(out.toString());
            System.out.println("--- / TEXT UI OUTPUT ----");
        }
    }

    public void checkTotalTests(int expected) {
        assertThat("total tests", out.toString(), containsString("Total: " + expected + NEWLINE));
    }

    public void checkFailingTests(int expected) {
        assertThat("failing tests", out.toString(), containsString("Fail: " + expected + ","));
    }

    public void checkPassingTests(int expected) {
        assertThat("passing tests", out.toString(), containsString("Pass: " + expected + ","));
    }

    public void checkHasStackTrace(String... elements) {
        String actual = out.toString();
        int pos = 0;
        for (String element : elements) {
            pos = actual.indexOf(element, pos);
            if (pos < 0) {
                throw new ComparisonFailure("stack trace not found", asLines(elements), actual);
            }
        }
    }

    private static String asLines(String[] ss) {
        StringBuilder sb = new StringBuilder();
        for (String s : ss) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(s);
        }
        return sb.toString();
    }

    // JUnit integration

    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            public void evaluate() throws Throwable {
                setUp();
                try {
                    base.evaluate();
                } finally {
                    tearDown();
                }
            }
        };
    }

    private void setUp() {
        assertTrue("Unable to create " + sandboxDir, sandboxDir.mkdirs());

        printProcessOutput(launcher);
        launcher.setJumiHome(sandboxDir);

        if (System.getProperty("jumi.useThreadSafetyAgent", "false").equals("true")) {
            String threadSafetyAgent = TestEnvironment.getProjectJar("thread-safety-agent").getAbsolutePath();
            launcher.setJvmOptions("-javaagent:" + threadSafetyAgent);
        }
    }

    private void tearDown() {
        try {
            // XXX: may fail to delete because the daemon is still running and the JAR is locked
            FileUtils.forceDelete(sandboxDir);
        } catch (IOException e) {
            System.err.println("WARNING: " + e.getMessage());
        }
    }

    private static void printProcessOutput(JumiLauncher launcher) {
        launcher.setOutputListener(new Writer() {
            public void write(char[] cbuf, int off, int len) {
                System.out.print(new String(cbuf, off, len));
            }

            public void flush() {
                System.out.flush();
            }

            public void close() {
                flush();
            }
        });
    }
}