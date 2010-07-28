/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.shapefile.indexed;

import java.io.IOException;
import org.geotoolkit.data.shapefile.shp.IndexFile;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.quadtree.DataReader;

/**
 *
 * @author jsorel
 */
public class IndexDataReader implements DataReader {

    public static final DataDefinition DATA_DEFINITION = new DataDefinition("US-ASCII", Integer.class, Long.class);

    private final IndexFile indexfile;

    public IndexDataReader(IndexFile indexFile){
        this.indexfile = indexFile;
    }

    @Override
    public Data read(int recno) throws IOException {
        return new ShpData(recno+1, (long)indexfile.getOffsetInBytes(recno));
    }

    @Override
    public void read(int[] ids, Data[] buffer, int size) throws IOException {
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

        private final int v1;
        private final long v2;

        public ShpData(int v1, long v2){
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Data addValue(Object val) throws TreeException {
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
        public Object getValue(int i) {
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
