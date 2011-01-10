/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * A ReadableByteChannel that delegates all calls to the underlying
 * ReadableByteChannel but for {@link #close()} it also calls
 * ShapefileFiles.unlock method to release the lock on the URL.
 * 
 * @author jesse
 * @module pending
 */
public class UnlockReadableByteChannel implements ReadableByteChannel {

    private final ReadableByteChannel wrapped;
    private final ShpFiles shapefileFiles;
    private final URL url;
    private final Object requestor;
    private boolean closed;

    public UnlockReadableByteChannel(final ReadableByteChannel newChannel,
            final ShpFiles shapefileFiles, final URL url, final Object requestor) {
        this.wrapped = newChannel;
        this.shapefileFiles = shapefileFiles;
        this.url = url;
        this.requestor = requestor;
        this.closed = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        try {
            wrapped.close();
        } finally {
            if (!closed) {
                closed = true;
                shapefileFiles.unlockRead(url, requestor);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isOpen() {
        return wrapped.isOpen();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        return wrapped.read(dst);
    }

}
