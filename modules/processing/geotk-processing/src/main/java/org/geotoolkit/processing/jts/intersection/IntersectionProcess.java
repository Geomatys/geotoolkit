/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011 - 2012, Geomatys
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

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.jts.intersection.IntersectionDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Compute the intersection geometry of the two inputs geometries.
 * The process ensure that two geometries are into the same CoordinateReferenceSystem.
 * The returned point keep the common geometry CRS.
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class IntersectionProcess extends AbstractProcess {

    public IntersectionProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {

         try {

            final Geometry geom1 = value(GEOM1, inputParameters);
            Geometry geom2 = value(GEOM2, inputParameters);

            // ensure geometries are in the same CRS
            final CoordinateReferenceSystem resultCRS = JTS.getCommonCRS(geom1, geom2);
            if (JTS.isConversionNeeded(geom1, geom2)) {
                geom2 = JTS.convertToCRS(geom2, resultCRS);
            }

            final Geometry result = (Geometry) geom1.intersection(geom2);
            if (resultCRS != null) {
                JTS.setCRS(result, resultCRS);
            }

            getOrCreate(RESULT_GEOM, outputParameters).setValue(result);

        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
