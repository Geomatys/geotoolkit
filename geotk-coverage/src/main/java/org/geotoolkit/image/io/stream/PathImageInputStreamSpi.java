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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;


/**
 * A service provider for {@link ImageInputStream}s from {@link Path}s.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class PathImageInputStreamSpi extends ImageInputStreamSpi {

    public PathImageInputStreamSpi() {
        super("Geotoolkit.org", "4.00", Path.class);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Stream from a NIO Path."; // TODO: localize
    }

    @Override
    public ImageInputStream createInputStreamInstance(Object input, boolean useCache, File cacheDir) throws IOException {
        final Path path = (Path) input;
        if (Files.isDirectory(path)) {
            Logger.getLogger("org.geotoolkit.image.io.stream")
                    .log(Level.FINER, "Unable to open ImageInputStream on directory : {0}", path.toAbsolutePath().toString());
            return null;
        }

        if (useCache) {
            return new ClosingCachedImageStream(Files.newInputStream(path, StandardOpenOption.READ));
        }
        return new PathImageInputStream(path);
    }

    private static class ClosingCachedImageStream extends MemoryCacheImageInputStream {

        private final InputStream in;

        public ClosingCachedImageStream(InputStream in) {
            super(in);
            this.in = in;
        }

        @Override
        public void close() throws IOException {
            super.close();
            in.close();
        }

    }
}
