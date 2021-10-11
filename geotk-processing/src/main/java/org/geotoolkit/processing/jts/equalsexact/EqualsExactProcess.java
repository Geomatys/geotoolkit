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
package org.geotoolkit.processing.jts.equalsexact;

import org.locationtech.jts.geom.Geometry;

import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.geotoolkit.processing.jts.equalsexact.EqualsExactDescriptor.*;

/**
 * Compute if two input geometries are equalExact.
 * The process ensure that two geometries are into the same CoordinateReferenceSystem. *
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class EqualsExactProcess extends AbstractProcess {

    public EqualsExactProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

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

            final Boolean result;

            if (inputParameters.getValue(TOLERANCE) != null) {
                final Double tolerance = inputParameters.getValue(TOLERANCE);
                result = (Boolean) geom1.equalsExact(geom2,tolerance);

            } else {
                result = (Boolean) geom1.equalsExact(geom2);
            }

            outputParameters.getOrCreate(RESULT).setValue(result);

        } catch (TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (FactoryException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
