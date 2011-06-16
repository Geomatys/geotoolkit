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
import org.geotoolkit.ncwms.AbstractNcGetMap;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.security.ClientSecurity;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * Implementation for the GetMap request version 1.1.1.
 *
 * @author Olivier Terral (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class NcGetMap111 extends AbstractNcGetMap {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public NcGetMap111(final String serverURL, final ClientSecurity security){
        super(serverURL,"1.1.1",security);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Map<String,String> toString(final Envelope env) {
        final Map<String,String> map = new HashMap<String,String>();
        final StringBuilder sb = new StringBuilder();
        final double minx = env.getMinimum(0);
        final double maxx = env.getMaximum(0);
        final double miny = env.getMinimum(1);
        final double maxy = env.getMaximum(1);
        sb.append(minx).append(',').append(miny).append(',').append(maxx).append(',').append(maxy);

        map.put("BBOX", sb.toString());

        try {
            CoordinateReferenceSystem crs2d = CRSUtilities.getCRS2D(env.getCoordinateReferenceSystem());
            map.put("SRS", IdentifiedObjects.lookupIdentifier(crs2d, true));
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        encodeTimeAndElevation(env, map);

        return map;
    }

}
