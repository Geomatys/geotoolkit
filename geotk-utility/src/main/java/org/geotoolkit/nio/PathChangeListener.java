package org.geotoolkit.nio;

import java.util.EventListener;

/**
 * A listener notified when a {@link DirectoryWatcher} detects a change into one watched folder.
 *
 * @author Alexis Manin (Geomatys)
 */

public interface PathChangeListener extends EventListener {

    public void pathChanged(PathChangedEvent event);
}
