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
package org.geotoolkit.process.jts.intersection;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.geometry.jts.JTS;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.jts.JTSProcessingUtils;
import static org.geotoolkit.process.jts.intersection.IntersectionSurfaceDescriptor.*;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Calculates the intersection surface of two geometries.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
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

            final Geometry geom1 = value(GEOM1, inputParameters);
            Geometry geom2 = value(GEOM2, inputParameters);

            // ensure geometries are in the same CRS
            final CoordinateReferenceSystem resultCRS = JTSProcessingUtils.getCommonCRS(geom1, geom2);
            if (JTSProcessingUtils.isConversionNeeded(geom1, geom2)) {
                geom2 = JTSProcessingUtils.convertToCRS(geom2, resultCRS);
            }

            final Geometry intersection = (Geometry) geom1.intersection(geom2);
            if (resultCRS != null) {
                JTS.setCRS(intersection, resultCRS);
            }

            final double area = (intersection == null) ? 0d : intersection.getArea();
            getOrCreate(RESULT_SURFACE, outputParameters).setValue(area);

        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
