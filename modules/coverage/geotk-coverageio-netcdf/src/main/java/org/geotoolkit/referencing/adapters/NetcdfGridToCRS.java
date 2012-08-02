/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.adapters;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;
import org.geotoolkit.resources.Errors;


/**
 * Implementation of a <cite>grid to CRS</cite> transform backed by NetCDF axes.
 * This is used only when a {@link NetcdfCRS} contains at least one non-regular axis.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class NetcdfGridToCRS extends AbstractMathTransform {
    /**
     * The number of source dimensions.
     */
    private final int sourceDim;

    /**
     * The NetCDF coordinate axes in "natural" order (reverse of the order in NetCDF file).
     * The length of this array is the number of target dimensions.
     */
    private final NetcdfAxis[] axes;

    /**
     * Creates a new transform for the given axes.
     */
    NetcdfGridToCRS(final int sourceDim, final NetcdfAxis[] axes) {
        this.sourceDim = sourceDim;
        this.axes = axes;
    }

    /**
     * Returns the number of source dimensions.
     */
    @Override
    public int getSourceDimensions() {
        return sourceDim;
    }

    /**
     * Returns the number of target dimensions.
     */
    @Override
    public int getTargetDimensions() {
        return axes.length;
    }

    /**
     * Transforms a single grid coordinate value.
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws TransformException
    {
        if (derivate) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
        }
        for (int i=0; i<axes.length; i++) {
            dstPts[dstOff + i] = axes[i].getOrdinateValue(srcPts, srcOff);
        }
        return null;
    }
}
