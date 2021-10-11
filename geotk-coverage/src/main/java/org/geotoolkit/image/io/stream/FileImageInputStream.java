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
package org.geotoolkit.image.io.stream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import javax.imageio.stream.ImageInputStream;
import com.sun.media.imageio.stream.FileChannelImageInputStream;


/**
 * An {@linkplain ImageInputStream Image Input Stream} using a {@linkplain FileChannel File Channel}
 * as the data source. This implementation differs from the Sun's parent implementation in three
 * ways:
 * <p>
 * <ul>
 *   <li>The constructor expects a {@link File} argument instead than a {@link FileChannel}
 *       argument, so no reference to the channel exists outside this class.</li>
 *   <li>The {@link #close()} method closes also the channel. This is allowed because
 *       this class "own" the channel.</li>
 *   <li>The file given to the constructor is keep for information purpose.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @see javax.imageio.stream.FileImageInputStream
 *
 * @since 3.01
 * @module
 */
public class FileImageInputStream extends FileChannelImageInputStream {
    /**
     * The file given to the constructor.
     */
    public final File file;

    /**
     * The handler to close when we are done, or {@code null} if it is already closed.
     */
    private RandomAccessFile raf;

    /**
     * Creates a new image input stream for the given file.
     *
     * @param  file The file for which to create an image input stream.
     * @throws IOException If an error occurred while opening the channel.
     */
    public FileImageInputStream(final File file) throws IOException {
        this(new RandomAccessFile(file, "r"), file);
    }

    /**
     * Creates a new image input stream for the given handler.
     * This method is not public because we want to be sure that no reference
     * to that handler exists outside this class.
     *
     * @param  raf The handler to the file for which to create an image input stream.
     * @throws IOException If an error occurred while opening the channel.
     */
    private FileImageInputStream(final RandomAccessFile raf, final File file) throws IOException {
        super(raf.getChannel());
        this.raf  = raf;
        this.file = file;
    }

    /**
     * Closes this stream and its underlying channel.
     *
     * @throws IOException If an error occurred while closing the stream.
     */
    @Override
    public void close() throws IOException {
        if (raf != null) {
            raf.close(); // This closes the channel as well.
            raf = null;
        }
        super.close();
    }
}
