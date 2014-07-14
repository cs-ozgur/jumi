// Copyright © 2011-2014, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core.ipc;

import fi.jumi.core.ipc.api.CommandListener;
import fi.jumi.core.ipc.channel.*;
import fi.jumi.core.ipc.dirs.CommandDir;
import fi.jumi.core.ipc.encoding.CommandListenerEncoding;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class IpcCommandReader {

    private final IpcReader<CommandListener> reader;
    private final CommandListener target;

    public IpcCommandReader(CommandDir dir, CommandListener target) {
        this.target = target;
        this.reader = IpcChannel.reader(dir.getRequestPath(), CommandListenerEncoding::new);
    }

    public void run() {
        IpcReaders.decodeAll(reader, target);
    }
}
