/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.shapefile.indexed;

import java.io.IOException;
import org.geotoolkit.data.shapefile.shp.IndexFile;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.quadtree.DataReader;

/**
 *
 * @author jsorel
 */
public class IndexDataReader implements DataReader {

    private static final DataDefinition DATA_DEFINITION = new DataDefinition("US-ASCII");
    static {
        DATA_DEFINITION.addField(Integer.class);
        DATA_DEFINITION.addField(Long.class);
    }

    private final IndexFile indexfile;

    public IndexDataReader(IndexFile indexFile){
        this.indexfile = indexFile;
    }

    @Override
    public Data create(Integer recno) throws IOException {
        final Data data = new Data(DATA_DEFINITION);
        data.addValue(new Integer(recno.intValue() + 1));
        data.addValue(new Long(indexfile.getOffsetInBytes(recno.intValue())));
        return data;
    }

    @Override
    public void close() throws IOException {
        indexfile.close();
    }

}
