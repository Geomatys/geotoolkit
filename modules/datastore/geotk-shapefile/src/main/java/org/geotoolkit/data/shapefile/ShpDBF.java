/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010 , Geomatys
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

package org.geotoolkit.data.shapefile;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import org.geotoolkit.data.dbf.DbaseFileReader;
import org.geotoolkit.data.dbf.IndexedDbaseFileReader;

/**
 * Wrap a dbf reader/writer.
 *
 * @author Johann Sorel (geomatys)
 */
public class ShpDBF {

    private ShpDBF(){}

    public static DbaseFileReader reader(final ShpFiles files, final boolean memoryMapped, final Charset set) throws IOException{
        final ShpDBF wrap = new ShpDBF();
        final ReadableByteChannel rbc = files.getReadChannel(ShpFileType.DBF, wrap);
        final DbaseFileReader reader = new DbaseFileReader(rbc, memoryMapped, set);
        return reader;
    }

    public static IndexedDbaseFileReader indexed(final ShpFiles files, final boolean memoryMapped, final Charset set) throws IOException{
        final ShpDBF wrap = new ShpDBF();
        final ReadableByteChannel rbc = files.getReadChannel(ShpFileType.DBF, wrap);
        final IndexedDbaseFileReader reader = new IndexedDbaseFileReader(rbc, memoryMapped, set);
        return reader;
    }

}
