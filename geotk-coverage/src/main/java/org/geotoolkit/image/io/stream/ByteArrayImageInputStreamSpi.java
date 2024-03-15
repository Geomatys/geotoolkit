/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import org.geotoolkit.io.SeekableByteArrayChannel;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.io.stream.ChannelDataInput;
import org.apache.sis.io.stream.ChannelImageInputStream;


/**
 * A service provider for {@link ImageInputStream}s from byte array.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ByteArrayImageInputStreamSpi extends ImageInputStreamSpi {

    public ByteArrayImageInputStreamSpi() {
        super("Geotoolkit.org", "5.00", byte[].class);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Stream from a byte array."; // TODO: localize
    }

    @Override
    public ImageInputStream createInputStreamInstance(Object input, boolean useCache, File cacheDir) throws IOException {
        final byte[] data = (byte[]) input;
        final ReadableByteChannel channel = new SeekableByteArrayChannel(data);
        //note : we do not use an empty channel because readers may move backward after a flush.
        final ChannelDataInput cdi = new ChannelDataInput("in memory byte array", channel, ByteBuffer.wrap(data),true);
        return new ChannelImageInputStream(cdi);
    }

}
