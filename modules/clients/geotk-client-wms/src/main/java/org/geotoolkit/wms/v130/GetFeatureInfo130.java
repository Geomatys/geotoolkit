/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.wms.v130;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.wms.AbstractGetFeatureInfo;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;


/**
 * Implementation for the GetFeatureInfo request version 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GetFeatureInfo130 extends AbstractGetFeatureInfo {
    private Integer i = null;
    private Integer j = null;

    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public GetFeatureInfo130(String serverURL){
        super(serverURL,"1.3.0");
    }

    public Integer getI() {
        return i;
    }

    public Integer getJ() {
        return j;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public void setJ(Integer j) {
        this.j = j;
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
            map.put("CRS", CRS.lookupIdentifier(env.getCoordinateReferenceSystem(), true));
        } catch (FactoryException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        encodeTimeAndElevation(env, map);

        return map;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        if (i == null) {
            throw new IllegalArgumentException("I is not defined");
        }
        if (j == null) {
            throw new IllegalArgumentException("J is not defined");
        }
        requestParameters.put("I", String.valueOf(i));
        requestParameters.put("J", String.valueOf(j));
        return super.getURL();
    }
}
