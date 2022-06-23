/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Locale;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;


/**
 * A service provider for {@link ImageOutputStream}s from {@link Path}s.
 * This SPI doesn't support cache and create ImageOutputStream as wrapper of OutputStream.
 * Wrapped {@link OutputStream} is obtained using {@link Files#newOutputStream(Path, OpenOption...)} with options
 * {@link java.nio.file.StandardOpenOption#CREATE} and {@link java.nio.file.StandardOpenOption#WRITE}
 */
public class PathImageOutputStreamSpi extends ImageOutputStreamSpi {

    public PathImageOutputStreamSpi() {
        super("Geotoolkit.org", "4.00", Path.class);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Stream from a NIO Path.";
    }

    @Override
    public ImageOutputStream createOutputStreamInstance(Object output, boolean useCache, File cacheDir) throws IOException {
        final Path outputPath = (Path) output;
        try {
            return new FileImageOutputStream(outputPath.toFile());
        } catch (UnsupportedOperationException ex) {
            // toFile() not supported, use stream
        }
        final OutputStream stream = Files.newOutputStream(outputPath, CREATE, WRITE);
        try {
            if (useCache) {
                return new ClosableFileCacheImageOutputStream(stream, cacheDir);
            } else {
                return new ClosableMemoryCacheImageOutputStream(stream);
            }
        } catch (Exception e) {
            try {
                stream.close();
            } catch (Exception bis) {
                e.addSuppressed(bis);
            }
            throw e;
        }
    }
}
