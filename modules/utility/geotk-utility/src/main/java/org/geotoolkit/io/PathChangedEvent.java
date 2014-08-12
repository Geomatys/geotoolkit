package org.geotoolkit.io;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.EventObject;

/**
 * An event raised by {@link org.geotoolkit.io.DirectoryWatcher} when a path has changed in a followed folder.
 *
 * @author Alexis Manin (Geomatys)
 */

public class PathChangedEvent extends EventObject {

    /**
     * The path which denotes the changed file.
     */
    public final Path target;

    /**
     * The kind of modification which happened. Most likely one of the {@link java.nio.file.StandardWatchEventKinds}.
     */
    public final WatchEvent.Kind kind;

    /**
     * True if the target path was a directory at the moment of the event, false if it was a file.
     */
    public final boolean isDirectory;

    /**
     * Number of times the same event occured.
     */
    public final int count;

    public PathChangedEvent(Object source, Path target, WatchEvent.Kind kind, boolean isDirectory, int count) {
        super(source);
        this.target = target;
        this.kind = kind;
        this.isDirectory = isDirectory;
        this.count = count;
    }
}
