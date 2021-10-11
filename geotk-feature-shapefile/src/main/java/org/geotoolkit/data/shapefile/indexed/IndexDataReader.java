/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.shapefile.indexed;

import java.io.IOException;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.quadtree.DataReader;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class IndexDataReader implements DataReader<IndexDataReader.ShpData> {

    public static final DataDefinition DATA_DEFINITION = new DataDefinition("US-ASCII", Integer.class, Long.class);

    private final ShxReader indexfile;

    public IndexDataReader(final ShxReader indexFile){
        this.indexfile = indexFile;
    }

    @Override
    public ShpData read(final int recno) throws IOException {
        return new ShpData(recno+1, (long)indexfile.getOffsetInBytes(recno));
    }

    @Override
    public void read(final int[] ids, final Data[] buffer, final int size) throws IOException {
        for(int i=0;i<size;i++){
            final int recno = ids[i];
            buffer[i] = new ShpData(recno+1, (long)indexfile.getOffsetInBytes(recno));
        }
    }

    @Override
    public void close() throws IOException {
        indexfile.close();
    }

    public static final class ShpData implements Data{

        public final int v1;
        public final long v2;

        public ShpData(final int v1, final long v2){
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Data addValue(final Object val) throws TreeException {
            throw new UnsupportedOperationException("Not supported in shapefile quad tree data.");
        }

        @Override
        public DataDefinition getDefinition() {
            return DATA_DEFINITION;
        }

        @Override
        public int getValuesCount() {
            return 2;
        }

        @Override
        public Object getValue(final int i) {
            if(i==0){
                return Integer.valueOf(v1);
            }else{
                return Long.valueOf(v2);
            }
        }

        @Override
        public String toString() {
            return v1 +" "+ v2;
        }

    }

}
