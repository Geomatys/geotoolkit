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

import org.apache.sis.internal.storage.io.ChannelImageInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import javax.imageio.stream.ImageInputStream;

import static java.nio.file.StandardOpenOption.*;


/**
 * An {@linkplain ImageInputStream Image Input Stream} using a  {@link Path} as the data source and
 * create a {@link FileChannel} from it.
 * This implementation is based on {@link ChannelImageInputStream} of Apache SiS.
 *
 * @author Quentin Boileau (Geomatys)
 *
 * @see org.apache.sis.internal.storage.ChannelImageInputStream
 */
public class PathImageInputStream extends ChannelImageInputStream {

    /**
     * Default ByteBuffer
     */
    public static final ByteBuffer DEFAULT_BYTE_BUFFER = ByteBuffer.allocate(4096);

    /**
     * The file given to the constructor.
     */
    public final Path path;

    /**
     * Creates a new image input stream for the given file.
     *
     * @param  path The Path for which to create an image input stream.
     * @throws IOException If an error occurred while opening the channel.
     */
    public PathImageInputStream(final Path path) throws IOException {
        super(path.getFileName().toString(),
                FileChannel.open(path, READ),
                DEFAULT_BYTE_BUFFER,
                false);
        this.path = path;
    }
}
