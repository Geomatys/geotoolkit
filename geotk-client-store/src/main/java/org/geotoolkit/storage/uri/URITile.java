/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.storage.uri;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.multires.DeferredTile;
import org.geotoolkit.storage.multires.Tile;

/**
 * TileMatrix Tile resolved by an URI.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class URITile {

    private URITile(){}

    /**
     * Create URI tile.
     *
     * @param tilematrix tile parent matrix
     * @param path tile URI
     * @param security not null
     * @param position tile coordinate in the matrix
     * @return Tile may be an ImageTile or DeferredTile based on matrix tile format.
     * @throws DataStoreException
     */
    public static Tile create(URITileMatrix tilematrix, URI path, ClientSecurity security, Point position) throws DataStoreException {
        ArgumentChecks.ensureNonNull("security", security);
        final URITileFormat.Compression compression = tilematrix.getFormat().getCompression();

        if (tilematrix.getFormat().isImage()) {
            return new Image(tilematrix.getFormat().getImageSpi(), path, security, 0, position, compression);
        } else {
            return new DataSet(tilematrix.getFormat().getStoreProvider(), path, security, position, compression);
        }
    }

    /**
     * Image format tile.
     */
    private static class Image extends AbstractResource implements ImageTile, ResourceOnFileSystem {

        private final URITileFormat.Compression compression;
        private final ImageReaderSpi spi;
        private final URI input;
        private final ClientSecurity security;
        private final int imageIndex;
        private final Point position;

        public Image(ImageReaderSpi spi, URI input, ClientSecurity security, int imageIndex, Point position, URITileFormat.Compression compression) {
            this.spi = spi;
            this.input = input;
            this.security = security;
            this.imageIndex = imageIndex;
            this.position = position;
            this.compression = compression;
        }

        @Override
        public ImageReader getImageReader() throws IOException {
            Object input = this.input;
            ImageReaderSpi spi = this.spi;
            ImageReader reader = null;

            switch (compression.name()) {
                case "GZ" :
                    input = new BufferedInputStream(new GZIPInputStream(new BufferedInputStream(IOUtilities.open(this.input))));
                    break;
                case "LZ4" :
                    input = new BufferedInputStream(new FramedLZ4CompressorInputStream(new BufferedInputStream(IOUtilities.open(this.input))));
                    break;
                case "NONE" :
                    break;
                default :
                    throw new IOException("Unsupported compression " + compression.name());
            }

            if (input instanceof URI) {
                //try to have a Path as input, many image readers dislike URI.
                try {
                    input = Paths.get((URI) input);
                } catch (FileSystemNotFoundException | SecurityException | IllegalArgumentException ex) {
                    //we have try
                }
            }

            if (spi == null && input != null) {
                reader = XImageIO.getReader(input, Boolean.FALSE, Boolean.FALSE);
                spi = reader.getOriginatingProvider();
            }

            if (spi == null) {
                //could not find a proper reader for input
                throw new IOException("Could not find image reader spi for input : "+input);
            }

            if (reader == null) {
                Object in = null;
                try {
                    in = XImageIO.toSupportedInput(spi, input);
                    reader = spi.createReaderInstance();
                    reader.setInput(in, true, true);
                } catch (IOException | RuntimeException e) {
                    try {
                        IOUtilities.close(in);
                    } catch (IOException ex) {
                        e.addSuppressed(ex);
                    }
                    if (reader != null) {
                        try {
                            XImageIO.dispose(reader);
                        } catch (Exception ex) {
                            e.addSuppressed(ex);
                        }
                    }
                    throw e;
                }
            }
            return reader;
        }

        @Override
        public ImageReaderSpi getImageReaderSpi() {
            return spi;
        }

        @Override
        public URI getInput() {
            return input;
        }

        @Override
        public int getImageIndex() {
            return imageIndex;
        }

        @Override
        public Point getPosition() {
            return position;
        }

        @Override
        public Path[] getComponentFiles() throws DataStoreException {
            try {
                return new Path[]{Paths.get(input)};
            } catch (FileSystemNotFoundException | SecurityException | IllegalArgumentException ex) {
                return new Path[0];
            }
        }
    }

    /**
     * Dataset format tile.
     */
    private static class DataSet extends AbstractResource implements Tile, DeferredTile, ResourceOnFileSystem {

        private final URITileFormat.Compression compression;
        private final DataStoreProvider provider;
        private final URI input;
        private final ClientSecurity security;
        private final Point position;

        public DataSet(DataStoreProvider provider, URI input, ClientSecurity security, Point position, URITileFormat.Compression compression) {
            this.provider = provider;
            this.input = input;
            this.security = security;
            this.position = position;
            this.compression = compression;
        }

        @Override
        public Point getPosition() {
            return position;
        }

        @Override
        public Path[] getComponentFiles() throws DataStoreException {
            try {
                return new Path[]{Paths.get(input)};
            } catch (FileSystemNotFoundException | SecurityException | IllegalArgumentException ex) {
                return new Path[0];
            }
        }

        @Override
        public Resource open() throws DataStoreException {
            final StorageConnector sc = new StorageConnector(input);
            return provider.open(sc);
        }
    }

}
