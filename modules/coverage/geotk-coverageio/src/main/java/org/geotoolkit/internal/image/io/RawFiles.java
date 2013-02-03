/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ServiceRegistry;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.image.io.plugin.RawImageReader;


/**
 * Utility methods related to RAW image files.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public final class RawFiles implements ServiceRegistry.Filter {
    /**
     * The single filter for the "raw" image format.
     */
    private static final RawFiles FILTER = new RawFiles();

    /**
     * For the singleton only.
     */
    private RawFiles() {
    }

    /**
     * Returns an image reader for the RAW format capable to decode the given input.
     * If the Geotk RAW reader is found and claims to be capable to decode the given
     * input, then that reader is given precedence. We do that because the Geotk RAW
     * reader is faster than the Image I/O one at least for data of type float, and
     * the Image I/O reader also seems to have bugs. However the Geotk reader is not
     * ordered before the Image I/O one in the {@link IIORegistry} because it has
     * more restriction on the kind of file that it can read.
     *
     * @param  input The input to decode, or {@code null} for not using that information.
     * @return The RAW image reader (preferably the Geotk one), or {@code null} if none.
     * @throws IOException If an error occurred while testing the input.
     */
    public static ImageReader getImageReader(final Object input) throws IOException {
        ImageReaderSpi provider = null;
        final Iterator<ImageReaderSpi> it = IIORegistry.getDefaultInstance()
                .getServiceProviders(ImageReaderSpi.class, FILTER, true);
        while (it.hasNext()) {
            final ImageReaderSpi candidate = it.next();
            if (input == null || candidate.canDecodeInput(input)) {
                if (candidate instanceof RawImageReader.Spi) {
                    provider = candidate;
                    break;
                }
                if (provider == null) {
                    provider = candidate;
                    // Continue the search.
                }
            }
        }
        return (provider != null) ? provider.createReaderInstance() : null;
    }

    /**
     * For internal usage only.
     */
    @Override
    public boolean filter(final Object provider) {
        return ArraysExt.contains(((ImageReaderWriterSpi) provider).getFormatNames(), "raw");
    }
}
