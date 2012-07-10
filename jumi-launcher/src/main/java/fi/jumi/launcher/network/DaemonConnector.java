// Copyright © 2011-2012, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.launcher.network;

import fi.jumi.actors.eventizers.Event;
import fi.jumi.actors.queue.MessageSender;
import fi.jumi.core.SuiteListener;

import java.io.File;
import java.util.List;

public interface DaemonConnector {

    int listenForDaemonConnection(MessageSender<Event<SuiteListener>> eventTarget, List<File> classPath, String testsToIncludePattern);
}
