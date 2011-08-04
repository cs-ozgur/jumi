// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core.actors;

public interface LongLivedActors {

    <T> T createPrimaryActor(Class<T> type, T target, String name);
}