/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.geometry.jts.transform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import java.util.Arrays;
import static org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer.DEFAULT_CS_FACTORY;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoordinateSequenceWrapTransformer implements CoordinateSequenceTransformer {

    private final CoordinateSequenceFactory csf;
    private final double[] wrapdistance;
    private final double[] translation;
    private boolean wrap = false;

    /**
     * Default wrap coordinate sequence transformer.
     * @see CoordinateSequenceWrapTransformer#CoordinateSequenceWrapTransformer(com.vividsolutions.jts.geom.CoordinateSequenceFactory, double[], double[])
     */
    public CoordinateSequenceWrapTransformer(final double[] wrapdistance, final double[] translation) {
        this(null,wrapdistance, translation);
    }

    /**
     * Wrap coordinate sequence transformer with given factory.
     *
     * @param csf
     * @param wrapdistance
     * @param translation
     */
    public CoordinateSequenceWrapTransformer(final CoordinateSequenceFactory csf, final double[] wrapdistance, final double[] translation) {
        if(csf == null){
            this.csf = DEFAULT_CS_FACTORY;
        }else{
            this.csf = csf;
        }
        this.wrapdistance = wrapdistance;
        this.translation = translation;
    }

    @Override
    public CoordinateSequence transform(CoordinateSequence sequence, int minpoints) throws TransformException {
        final int size = sequence.size();
        final Coordinate[] tcs = new Coordinate[size];

        Coordinate previous = null;
        Coordinate current = null;
        for(int i= 0; i<size; i++){
            current = sequence.getCoordinate(i);

            if(previous != null
                && (wrapdistance[0]==0 || Math.abs(current.x-previous.x)>=wrapdistance[0])
                && (wrapdistance[1]==0 || Math.abs(current.y-previous.y)>=wrapdistance[1])){
                //assume it crosses the antimeridian
                wrap = !wrap;
            }
            previous = current;

            if(wrap){
                tcs[i] = new Coordinate(current.x+translation[0], current.y+translation[1], current.z);
            }else{
                tcs[i] = current;
            }

        }

        return csf.create(tcs);
    }

}
