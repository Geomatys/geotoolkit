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
package org.geotoolkit.processing.jts.centroid;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.ProcessException;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import static org.geotoolkit.processing.jts.centroid.CentroidDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Compute input geometry centroid.
 * The returned point keep input geometry CRS.
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class CentroidProcess extends AbstractProcess {

    public CentroidProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {
        try {
            final Geometry geom = value(GEOM, inputParameters);

            final CoordinateReferenceSystem geomCRS = JTS.findCoordinateReferenceSystem(geom);

            final Point result = geom.getCentroid();
            JTS.setCRS(result, geomCRS);

            getOrCreate(RESULT_GEOM, outputParameters).setValue(result);

        } catch (NoSuchAuthorityCodeException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (FactoryException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
