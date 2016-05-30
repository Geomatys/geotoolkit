/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Override of {@link MemoryCacheImageOutputStream} with close of destination {@link OutputStream}.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ClosableMemoryCacheImageOutputStream  extends MemoryCacheImageOutputStream {

    private OutputStream sourceStream;

    public ClosableMemoryCacheImageOutputStream(OutputStream stream) throws IOException {
        super(stream);
        this.sourceStream = stream;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (sourceStream != null) {
            sourceStream.close();
        }
        sourceStream = null;
    }
}