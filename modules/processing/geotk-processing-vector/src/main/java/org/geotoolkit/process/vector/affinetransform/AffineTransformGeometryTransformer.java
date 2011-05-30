/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.process.vector.affinetransform;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import org.geotoolkit.geometry.jts.transform.AbstractGeometryTransformer;

import org.opengis.referencing.operation.TransformException;

/**
 * Implementation of GeometryTransformer which apply an AffineTransformation to a Geometry
 * @author Quentin Boileau
 * @module pending
 */
public class AffineTransformGeometryTransformer extends AbstractGeometryTransformer{

    private final java.awt.geom.AffineTransform transform;
    
    /**
     * Constructor
     * @param transform
     */
    public AffineTransformGeometryTransformer(final java.awt.geom.AffineTransform transform) {
        this.transform = transform;
    }

    /**
     * Apply an AffineTransform to CoordonateSequence given in parameters
     * @param sequence
     * @param minpoints
     * @return the transform CoordinateSequence
     * @throws TransformException
     */
    @Override
    public CoordinateSequence transform(final CoordinateSequence sequence, final int minpoints) throws TransformException {

        final int dim = sequence.getDimension();

        final CoordinateSequence tranformCoord = new CoordinateArraySequence(sequence.size());

        final double[] val = new double[dim];
        final double[] resultVal = new double[dim];
        for (int i = 0; i<sequence.size(); i++) {
            for(int j = 0; j<dim; j++){
                val[j] = sequence.getOrdinate(i, j);
            }
            //TODO affine tranbsform handle only 2D
            transform.transform(val, 0, resultVal, 0, 1);

            for(int k = 0; k<dim; k++){
                tranformCoord.setOrdinate(i, k, resultVal[k]);
            }
        }

       return tranformCoord;
    }
}
