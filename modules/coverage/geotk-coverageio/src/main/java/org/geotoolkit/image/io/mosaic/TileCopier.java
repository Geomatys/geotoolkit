/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.IIOException;

import com.sun.media.imageio.stream.FileChannelImageOutputStream;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.rmi.RMI;
import org.geotoolkit.internal.io.ObjectStream;
import org.geotoolkit.internal.rmi.ShareableTask;
import org.geotoolkit.internal.image.io.RawFile;


/**
 * Copies a set of tiles to temporary RAW files.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
final class TileCopier extends ShareableTask<Tile,Map<Tile,RawFile>> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 6264132661340879616L;

    /**
     * An optional operation to apply on the source tiles, or {@code null} if none.
     */
    private final BufferedImageOp filter;

    /**
     * The temporary files created by this task.
     */
    private final Map<Tile,RawFile> temporaryFiles;

    /**
     * Creates a new tile copier.
     *
     * @param tiles  The source tiles.
     * @param filter An optional operation to apply on the source tiles, or {@code null} if none.
     */
    TileCopier(final Collection<Tile> tiles, final BufferedImageOp filter) {
        super(tiles);
        this.filter = filter;
        temporaryFiles = new HashMap<Tile,RawFile>();
    }

    /**
     * Returns the image writer to use for creating the temporary files.
     */
    private static ImageWriter getTemporaryTileWriter() throws IOException {
        final Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("raw");
        if (!it.hasNext()) {
            throw new IIOException(Errors.format(Errors.Keys.NO_IMAGE_WRITER));
        }
        return it.next();
    }

    /**
     * Uncompress and copies the given source tiles.
     *
     * @return The map of temporary files created by this method.
     * @throws IOException If a read or write operation failed.
     */
    @Override
    public Map<Tile,RawFile> call() throws IOException {
        final ObjectStream<Tile> tiles = inputs();
        final Map<ImageTypeSpecifier,ImageTypeSpecifier> types =
                new HashMap<ImageTypeSpecifier,ImageTypeSpecifier>();
        final ImageWriter writer = getTemporaryTileWriter();
        final File directory = RMI.getSharedTemporaryDirectory();
        ImageTypeSpecifier sourceType = null;
        BufferedImage sourceImage = null;
        BufferedImage targetImage = null;
        Tile tile;
        while ((tile = tiles.next()) != null) {
            final int imageIndex = tile.getImageIndex();
            final ImageReader reader = tile.getImageReader();
            ImageReadParam param = null;
            if (sourceImage != null) {
                /*
                 * Recycles the current BufferedImage if it still suitable for the next tile
                 * to read. In the majority of case, such recycling is possible. If we can't
                 * recycle it, a new BufferedImage will be created. In the later case we will
                 * invoke GC explicitly in order to increase the chances to get the previous
                 * image (which may be very big) reclaimed before to attempt to create a new
                 * one.
                 */
                final ImageTypeSpecifier type = reader.getRawImageType(imageIndex);
                if (reader.getWidth (imageIndex) == sourceImage.getWidth()  &&
                    reader.getHeight(imageIndex) == sourceImage.getHeight() &&
                    (sourceType != null && sourceType.equals(type)))
                {
                    param = reader.getDefaultReadParam();
                    param.setDestination(sourceImage);
                } else {
                    sourceImage = null;
                    targetImage = null;
                    System.gc(); // Image may be huge - give GC an additional chance.
                }
                sourceType = type;
            }
            /*
             * Reads the image, applies an optional operation and remember the color/sample
             * model (as an ImageTypeIdentifier) of the result. In the majority of cases, the
             * ImageTypeSpecifier will be the same for every tiles, so it is worth to share
             * the same instance given that some mosaic contains thousands of input tiles.
             */
            sourceImage = reader.read(imageIndex, param);
            Tile.dispose(reader);
            final BufferedImage image;
            if (filter != null) {
                image = targetImage = filter.filter(sourceImage, targetImage);
            } else {
                image = sourceImage;
            }
            final File file = File.createTempFile("IMW", ".raw", directory);
            file.deleteOnExit();
            ImageTypeSpecifier look = ImageTypeSpecifier.createFromRenderedImage(image);
            ImageTypeSpecifier type = types.get(look);
            if (type == null) {
                type = look;
                types.put(type, type);
            }
            final RawFile entry = new RawFile(file, type, image.getWidth(), image.getHeight());
            if (temporaryFiles.put(tile, entry) != null) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.DUPLICATED_VALUES_$1, tile));
            }
            /*
             * Writes the temporary file.
             */
            final RandomAccessFile raf = new RandomAccessFile(file, "rw");
            final FileChannel channel = raf.getChannel();
            final FileChannelImageOutputStream stream = new FileChannelImageOutputStream(channel);
            writer.setOutput(stream);
            writer.write(image);
            writer.setOutput(null);
            stream.close();
            channel.close();
            raf.close();
        }
        writer.dispose();
        tiles.close();
        return temporaryFiles;
    }

    /**
     * Invoked for aggregating the results after the execution is finished.
     */
    @Override
    public Map<Tile,RawFile> aggregate(final Collection<Map<Tile,RawFile>> outputs) {
        return aggregateMap(outputs);
    }

    /**
     * Invoked in case of failures for deleting the resources that the task may have created.
     */
    @Override
    public void rollback() {
        for (final RawFile file : temporaryFiles.values()) {
            file.file.delete();
        }
        temporaryFiles.clear();
    }
}
