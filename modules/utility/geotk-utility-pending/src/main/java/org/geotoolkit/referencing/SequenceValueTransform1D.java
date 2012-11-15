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
package org.geotoolkit.referencing;

import org.geotoolkit.referencing.operation.transform.AbstractMathTransform1D;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.referencing.operation.TransformException;

/**
 * Mathtransform 1D with linear interpolation between values.
 *
 * @author Johann Sorel (Geomatys)
 */
final class SequenceValueTransform1D extends AbstractMathTransform1D {
    private final double[] values;

    private SequenceValueTransform1D(double[] values) {
        ArgumentChecks.ensureNonNull("values", values);
        this.values = values;
    }

    @Override
    public double transform(double d) throws TransformException {
        final int index = (int) d;
        if(index <= 0){
            return values[0];
        }else if(index >= values.length-1){
            return values[values.length-1];
        }else{
            //interpolate
            final double lower = values[index];
            final double upper = values[index];
            return lower + ((upper-lower) * (d-index));
        }

    }

    @Override
    public double derivative(double d) throws TransformException {
        final int index = (int) d;
        if(index <= 0){
            return 1;
        }else if(index >= values.length-1){
            return 1;
        }else{
            final double lower = values[index];
            final double upper = values[index];
            return upper-lower;
        }
    }

    public static SequenceValueTransform1D create(double[] values){
        return new SequenceValueTransform1D(values);
    }

}
