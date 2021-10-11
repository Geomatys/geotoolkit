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
package org.geotoolkit.processing.jts.crosses;

import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Compute if the first geometry cross the second one.
 * The process ensure that two geometries are into the same CoordinateReferenceSystem.
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class CrossesProcess extends AbstractProcess {

    public CrossesProcess(final ParameterValueGroup input){
        super(CrossesDescriptor.INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {

       try {
            final Geometry geom1 = inputParameters.getValue(CrossesDescriptor.GEOM1);
            Geometry geom2 = inputParameters.getValue(CrossesDescriptor.GEOM2);

            // ensure geometries are in the same CRS
            final CoordinateReferenceSystem resultCRS = JTS.getCommonCRS(geom1, geom2);
            if (JTS.isConversionNeeded(geom1, geom2)) {
                geom2 = JTS.convertToCRS(geom2, resultCRS);
            }

            final Boolean result = (Boolean) geom1.crosses(geom2);

            outputParameters.getOrCreate(CrossesDescriptor.RESULT).setValue(result);

        } catch (FactoryException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
