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

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.FileCacheImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
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
 *
 * @author Quentin Boileau (Geomatys)
 */
public class PathImageOutputStreamSpi extends ImageOutputStreamSpi {

    public PathImageOutputStreamSpi() {
        super("Geotoolkit.org", "4.00", Path.class);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Stream from a NIO Path."; // TODO: localize
    }

    @Override
    public ImageOutputStream createOutputStreamInstance(Object output, boolean useCache, File cacheDir) throws IOException {

        final Path outputPath = (Path) output;

        try {
            if (useCache) {
                //create from path because cacheImageOutputStream implementation use OutputStream.
                return createFromPath(outputPath, cacheDir);
            } else {
                //try to File
                final File outputFile = outputPath.toFile();
                //direct file access
                return new FileImageOutputStream(outputFile);
            }
        } catch (UnsupportedOperationException ex) {
            // toFile() not supported, use stream
            return createFromPath(outputPath, cacheDir);
        }
    }

    /**
     * Create only cached ImageOutputStream from stream opened with input {@link Path}.
     *
     * @param outputPath
     * @param cacheDir if {@code null}, use in memory implementation, otherwise
     * @return cached ImageOutputStream with underling OutputStream
     * @throws IOException
     */
    private ImageOutputStream createFromPath(Path outputPath, File cacheDir) throws IOException {
        OutputStream outputStream = Files.newOutputStream(outputPath, CREATE, WRITE);

        return new ClosableFileCacheImageOutputStream(outputStream, cacheDir);

        //use memory cache if business need it
        //return new ClosableMemoryCacheImageOutputStream(outputStream);
    }
}
