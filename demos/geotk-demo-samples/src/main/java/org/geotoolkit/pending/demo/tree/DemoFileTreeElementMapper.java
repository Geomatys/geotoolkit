
package org.geotoolkit.pending.demo.tree;

import java.io.File;
import java.io.IOException;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.FileTreeElementMapper;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create a FileTreeElementMapper adapted for Tree demo.
 * 
 * @author Remi Marechal (Geomatys).
 */
final class DemoFileTreeElementMapper extends FileTreeElementMapper<Envelope>{
    
    final private CoordinateReferenceSystem crs;
    final private int dimension;
    
    
    DemoFileTreeElementMapper(File output, final CoordinateReferenceSystem crs) throws IOException {
        super(output, (crs.getCoordinateSystem().getDimension()*2*Double.SIZE) /8);
        this.crs       = crs;
        this.dimension = crs.getCoordinateSystem().getDimension();
    }
    
    DemoFileTreeElementMapper(final CoordinateReferenceSystem crs, File input) throws IOException {
        super(input);
        this.crs       = crs;
        this.dimension = crs.getCoordinateSystem().getDimension();
    }

    @Override
    protected void writeObject(Envelope Object) throws IOException {
        /*
         * To store element on hard drive we use a Databuffer where all 
         * databuffer working has been already manage. 
         * User should just write element what he need to define a data, from databuffer from superclass as below.
         * 
         * In this case, we travel all dimensions from envelope coordinate system and we write envelope coordinates. 
         */
        for (int d = 0; d < dimension; d++) {
            super.byteBuffer.putDouble(Object.getMinimum(d));
            super.byteBuffer.putDouble(Object.getMaximum(d));
        }
        /*
         * In our case Envelope is just define by its coordinates and its crs but
         * user may want store other information as he need.
         */
    }

    @Override
    protected Envelope readObject() throws IOException {
        /*
         * Same operation as writing action in reversed order.
         */
        GeneralEnvelope resultEnvelop = new GeneralEnvelope(crs);
        for (int d = 0; d < dimension; d++) {
            final double mind = super.byteBuffer.getDouble();
            final double maxd = super.byteBuffer.getDouble();
            resultEnvelop.setRange(d, mind, maxd);
        }
        return resultEnvelop;
    }

    @Override
    protected boolean areEquals(Envelope objectA, Envelope objectB) {
        /*
         * In some case equals method is not in accordance with equality pertinence asked by user.
         * User may redefine equal made.
         */
        return objectA.equals(objectB);
    }

    @Override
    public Envelope getEnvelope(Envelope object) throws IOException {
        return object;
    }
}
