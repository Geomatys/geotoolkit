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

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileStatus;
import org.geotoolkit.client.Request;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.DefaultImageTile;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class RequestImageTile extends DefaultImageTile {

    public RequestImageTile(TileMatrix matrix, ImageReaderSpi spi, Request input, int imageIndex, long... position) {
        super(matrix, spi, input, imageIndex, position);
    }

    @Override
    public ImageReader getImageReader() throws IOException {

        if(spi == null){
            final Request request = (Request)input;
            //download the tile once : the readers would open the URL again if given as input
            final byte[] bytes;
            try (InputStream in = request.getResponseStream()) {
                bytes = in.readAllBytes();
            }
            final ImageInputStream imin;
            try {
                imin = new StorageConnector(bytes).getStorageAs(ImageInputStream.class);
            } catch (DataStoreException ex) {
                throw new IOException(ex.getMessage(), ex);
            }
            //try to find a reader using the URL suffix, otherwise from the content
            final String suffix = IOUtilities.extension(request.getURL().getPath());
            if (suffix != null && !suffix.isEmpty()) {
                try {
                    return XImageIO.getReaderBySuffix(suffix, imin, Boolean.TRUE, Boolean.TRUE);
                } catch (IOException ex) {
                    //no reader for this suffix, search by content
                }
            }
            return XImageIO.getReader(imin, Boolean.TRUE, Boolean.TRUE);
        }

        final Object inputTmp = ((Request)input).getResponseStream();
        Object in = XImageIO.toSupportedInput(spi, inputTmp);

        final ImageReader reader = spi.createReaderInstance();
        reader.setInput(in, true, true);
        return reader;
    }

    @Override
    public TileStatus getStatus() {
        return TileStatus.UNKNOWN;
    }
}
