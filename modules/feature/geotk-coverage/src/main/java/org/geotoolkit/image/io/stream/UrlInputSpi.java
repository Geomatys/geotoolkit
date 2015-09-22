/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io.stream;

import java.util.Locale;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.SocketException;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileCacheImageInputStream;

import org.apache.sis.util.logging.Logging;


/**
 * A service provider for creating {@link ImageInputStream}s from {@link URL}s.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public class UrlInputSpi extends ImageInputStreamSpi {
    /**
     * Maximum number of retries when a connection failed.
     */
    private static final int RETRY = 3;

    /**
     * The proxy.
     */
    private final Proxy proxy;

    /**
     * Creates a new instance with no proxy.
     */
    public UrlInputSpi() {
        this(Proxy.NO_PROXY);
    }

    /**
     * Creates a new instance with the specified proxy.
     *
     * @param proxy The proxy to use.
     *
     * @since 2.5
     */
    public UrlInputSpi(final Proxy proxy) {
        super("Geotoolkit.org", "3.00", URL.class);
        this.proxy = proxy;
    }

    /**
     * Returns a brief, human-readable description of this service
     * provider and its associated implementation.
     */
    @Override
    public String getDescription(final Locale locale) {
        return "Stream from a URL."; // TODO: localize
    }

    /**
     * Returns {@code true} since the input stream requires the use of a cache file.
     */
    @Override
    public boolean needsCacheFile() {
        return true;
    }

    /**
     * Constructs an input stream for an URL.
     *
     * @throws IOException If the stream can not be created.
     */
    @Override
    public ImageInputStream createInputStreamInstance(final Object input,
            final boolean useCache, final File cacheDir) throws IOException
    {
        final URL url = (URL) input;
        final URLConnection connection = Proxy.NO_PROXY.equals(proxy) ? null : url.openConnection(proxy);
        int retry = RETRY;
        InputStream stream;
        while (true) {
            /*
             * If there is no proxy, then get the input stream with URL.openStream() instead
             * than using the connection because URL.openConnection() is not supported for URL
             * to an entry inside a JAR file, while URL.openStream() is.
             */
            try {
                stream = (connection != null) ? connection.getInputStream() : url.openStream();
                break;
            } catch (SocketException exception) {
                if (--retry < 0) {
                    throw exception;
                }
                Logging.recoverableException(null, UrlInputSpi.class, "createInputStreamInstance", exception);
            }
            /*
             * Failed to get the connection. After we logged a warning, wait a little bit, run
             * the finalization and try again. Experience suggests that running the finalizers
             * sometime help, but also sometime freeze the system. FinalizationStopper may help
             * to unfreeze the system after a timeout.
             */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
                // Someone doesn't want to let us sleep. Go back to work...
            }
            System.gc();
            Thread.interrupted(); // Clears the interrupted flag.
            final FinalizationStopper stopper = new FinalizationStopper(4000);
            System.runFinalization();
            stopper.cancel();
            // Thread.interrupted() must be first in order to clear the flag.
            if (Thread.interrupted() || stopper.interrupted) {
                Logging.getLogger("org.geotoolkit.image.io.stream").warning("System.runFinalization() was blocked.");
            }
        }
        return new FileCacheImageInputStream(stream, cacheDir);
    }
}
