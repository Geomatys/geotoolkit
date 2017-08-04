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
package org.geotoolkit.processing.jts.buffer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Collections;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import static org.geotoolkit.processing.jts.buffer.BufferDescriptor.*;

/**
 * Compute a buffer around a geometry.
 * The buffer geometry keep the input geometry CRS.
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class BufferProcess extends AbstractProcess {

    public BufferProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {
        try {
            final Geometry geom = inputParameters.getValue(GEOM);
            final double distance = inputParameters.getValue(DISTANCE);

            int segments = 0;
            if(inputParameters.getValue(SEGMENTS) != null) {
                segments = inputParameters.getValue(SEGMENTS);
            }

            int endStyle = 2;
            if(inputParameters.getValue(ENDSTYLE) != null) {
                 endStyle = inputParameters.getValue(ENDSTYLE);
            }

            final CoordinateReferenceSystem geomCRS = JTS.findCoordinateReferenceSystem(geom);

            Geometry result = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);

            if (segments > 0) {
                if (endStyle != 0) {
                     result = geom.buffer(distance, segments, endStyle);
                } else {
                     result = geom.buffer(distance, segments);
                }
            } else {
                result = geom.buffer(distance);
            }

            JTS.setCRS(result, geomCRS);
            outputParameters.getOrCreate(RESULT_GEOM).setValue(result);

        } catch (NoSuchAuthorityCodeException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (FactoryException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
