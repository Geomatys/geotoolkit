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
package org.geotoolkit.processing.jts.intersection;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.processing.jts.intersection.IntersectionSurfaceDescriptor.*;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Calculates the intersection surface of two geometries.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class IntersectionSurfaceProcess extends AbstractProcess {
    /**
     * Creates an intersection surface process using the given parameters.
     *
     * @param input Input parameters to use.
     */
    public IntersectionSurfaceProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     * Calculates the intersection surface of two geometries. If the two geometries do not
     * intersect, return {@code 0} in the output parameters.
     *
     * @throws ProcessException
     */
    @Override
    protected void execute() throws ProcessException {
        try {

            final Geometry geom1 = inputParameters.getValue(GEOM1);
            Geometry geom2 = inputParameters.getValue(GEOM2);

            // ensure geometries are in the same CRS
            final CoordinateReferenceSystem resultCRS = JTS.getCommonCRS(geom1, geom2);
            if (JTS.isConversionNeeded(geom1, geom2)) {
                geom2 = JTS.convertToCRS(geom2, resultCRS);
            }

            final Geometry intersection = (Geometry) geom1.intersection(geom2);
            if (resultCRS != null) {
                JTS.setCRS(intersection, resultCRS);
            }

            final double area = (intersection == null) ? 0d : intersection.getArea();
            outputParameters.getOrCreate(RESULT_SURFACE).setValue(area);

        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
