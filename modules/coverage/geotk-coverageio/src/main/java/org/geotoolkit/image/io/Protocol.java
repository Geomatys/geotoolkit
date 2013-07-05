/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.image.io;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.sis.util.logging.Logging;


/**
 * The protocol used for connecting to an image source through the network. Some protocols are
 * specific to some plugins, for example the {@linkplain #DODS} protocol which is used only by
 * {@link org.geotoolkit.image.io.plugin.NetcdfImageReader}. Consequently, an inspection of the
 * protocol can determine the image plugin to use.
 * <p>
 * This class provide also a convenient place where to summarize the various protocols
 * understood by the Geotk library.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
public enum Protocol {
    /**
     * The <cite>File Protocol</cite>. This is understood by every plugins
     * defined in the Geotk library.
     */
    FILE,

    /**
     * The <cite>File Transfer Protocol</cite>. This is understood by every plugins
     * defined in the Geotk library, but the {@link FileImageReader} will need to
     * copy its content to a temporary file.
     */
    FTP,

    /**
     * The <cite>Hyper Text Transfer Protocol</cite>. This is understood by every plugins
     * defined in the Geotk library, but the {@link FileImageReader} will need to copy its
     * content to a temporary file.
     */
    HTTP,

    /**
     * The <cite>Distributed Oceanographic Data System</cite> protocol. This is specific
     * to the {@link org.geotoolkit.image.io.plugin.NetcdfImageReader} plugin.
     */
    DODS,

    /**
     * Any protocol not in the above list.
     */
    UNKNOWN;

    /**
     * Returns the protocol of the given input, or {@link #UNKNOWN}. The input shall be an
     * instance of {@link File}, {@link URL}, {@link URI} or {@link CharSequence}. Any other
     * type will cause {@link #UNKNOWN} to be returned.
     *
     * @param  input The image input.
     * @return The protocol used by the given input, or {@link #UNKNOWN}.
     */
    public static Protocol getProtocol(final Object input) {
        if (input instanceof File) {
            return FILE;
        }
        String protocol = null;
        if (input instanceof URL) {
            final URL url = (URL) input;
            protocol = url.getProtocol();
        } else if (input instanceof URI) {
            final URI url = (URI) input;
            protocol = url.getScheme();
        } else if (input instanceof CharSequence) {
            final String url = input.toString();
            final int s = url.indexOf(':');
            if (s > 0) {
                protocol = url.substring(0, s);
            }
        }
        if (protocol != null) {
            protocol = protocol.toUpperCase().trim();
            try {
                return valueOf(protocol);
            } catch (IllegalArgumentException e) {
                Logging.recoverableException(WarningProducer.LOGGER, Protocol.class, "getProtocol", e);
            }
        }
        return UNKNOWN;
    }
}
