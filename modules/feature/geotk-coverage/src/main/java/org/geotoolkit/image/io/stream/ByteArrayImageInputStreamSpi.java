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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.internal.storage.io.ChannelDataInput;
import org.apache.sis.internal.storage.io.ChannelImageInputStream;


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
        final InputStream astream = new ByteArrayInputStream(data);
        final ReadableByteChannel channel = Channels.newChannel(astream);
        final ChannelDataInput cdi = new ChannelDataInput("in memory byte array", channel, ByteBuffer.wrap(data),true);
        return new ChannelImageInputStream(cdi);
    }

}
