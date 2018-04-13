/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.client.map;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.setup.OptionKey;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.client.Request;
import org.geotoolkit.storage.coverage.DefaultTileReference;
import org.geotoolkit.image.io.XImageIO;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class RequestTileReference extends DefaultTileReference {

    public RequestTileReference(ImageReaderSpi spi, Request input, int imageIndex, Point position) {
        super(spi, input, imageIndex, position);
    }

    @Override
    public ImageReader getImageReader() throws IOException {


        if(spi == null){
            //try to find reader
            final InputStream in = ((Request)input).getResponseStream();
            final StorageConnector cnx = new StorageConnector(in);
            cnx.setOption(OptionKey.BYTE_BUFFER, ByteBuffer.allocate(8196));

            try {
                final ImageInputStream imin = cnx.getStorageAs(ImageInputStream.class);
                final ImageReader reader = XImageIO.getReader(imin, Boolean.TRUE, Boolean.TRUE);
                return reader;
            } catch (DataStoreException ex) {
                try {
                    in.close();
                } catch (IOException e) {
                    ex.addSuppressed(e);
                }
                throw new IOException(ex.getMessage(), ex);
            } catch (IOException ex) {
                try {
                    in.close();
                } catch (IOException e) {
                    ex.addSuppressed(e);
                }
                throw ex;
            }
        }

        final Object inputTmp = ((Request)input).getResponseStream();
        Object in = XImageIO.toSupportedInput(spi, inputTmp);

        final ImageReader reader = spi.createReaderInstance();
        reader.setInput(in, true, true);
        return reader;
    }
}
