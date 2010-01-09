/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.io;

import java.io.File;
import java.lang.ref.Reference;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.geotoolkit.resources.Loggings;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.internal.ReferenceQueueConsumer;


/**
 * The thread which will delete expired temporary files.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
final class TemporaryFileCleaner extends ReferenceQueueConsumer<File> {
    /**
     * The unique instance.
     */
    static final TemporaryFileCleaner INSTANCE = new TemporaryFileCleaner();
    static {
        INSTANCE.start();
    }

    /**
     * Creates a new thread.
     */
    private TemporaryFileCleaner() {
        super("TemporaryFileCleaner");
        setPriority(NORM_PRIORITY);
    }

    /**
     * Deletes the given file. This method is invoked automatically when
     * a {@link File} object has been garbage-collected.
     *
     * @param reference The reference to the file to delete.
     */
    @Override
    protected void process(final Reference<? extends File> reference) {
        final TemporaryFile ref = (TemporaryFile) reference;
        if (ref.delete()) {
            /*
             * Logs the message at the WARNING level because execution of this code
             * means that the application failed to delete itself the temporary file.
             */
            final LogRecord record = Loggings.format(Level.WARNING, Loggings.Keys.TEMPORARY_FILE_GC_$1, ref);
            record.setSourceClassName(TemporaryFile.class.getName());
            record.setSourceMethodName("delete");
            Logging.log(TemporaryFile.class, record);
        }
    }
}
