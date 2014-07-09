// Copyright © 2011-2014, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core.ipc.api;

import fi.jumi.core.config.SuiteConfiguration;

public interface CommandListener {

    void runTests(SuiteConfiguration suiteConfiguration);

    void shutdown();
}