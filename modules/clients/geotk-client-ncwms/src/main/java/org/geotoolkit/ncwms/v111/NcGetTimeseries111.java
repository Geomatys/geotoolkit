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
package org.geotoolkit.ncwms.v111;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.geotoolkit.internal.referencing.CRSUtilities;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.ncwms.AbstractNcGetTimeseries;
import org.geotoolkit.referencing.ReferencingUtilities;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Implementation for the GetTimeseries request version 1.1.1.
 *
 * @author Fabien BERNARD (Geomatys)
 * @module
 */
public class NcGetTimeseries111 extends AbstractNcGetTimeseries {

    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public NcGetTimeseries111(final String serverURL, final ClientSecurity security) {
        super(serverURL, "1.1.1", security);
    }

    @Override
    protected Map<String, String> toString(Envelope env) {
        final Map<String,String> map = new HashMap<String,String>();
        final StringBuilder sb = new StringBuilder();
        final double minx = env.getMinimum(0);
        final double maxx = env.getMaximum(0);
        final double miny = env.getMinimum(1);
        final double maxy = env.getMaximum(1);
        sb.append(minx).append(',').append(miny).append(',').append(maxx).append(',').append(maxy);

        map.put("BBOX", sb.toString());

        try {
            String code = ReferencingUtilities.lookupIdentifier(env.getCoordinateReferenceSystem(), true);
            if (code == null) {
                code = ReferencingUtilities.lookupIdentifier(CRSUtilities.getCRS2D(env.getCoordinateReferenceSystem()), true);
            }
            map.put("SRS", code);
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        encodeElevation(env, map);

        return map;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if (columnIndex == null) {
            throw new IllegalArgumentException("X is not defined");
        }
        if (rawIndex == null) {
            throw new IllegalArgumentException("Y is not defined");
        }
        requestParameters.put("X", String.valueOf(columnIndex));
        requestParameters.put("Y", String.valueOf(rawIndex));
    }
}
