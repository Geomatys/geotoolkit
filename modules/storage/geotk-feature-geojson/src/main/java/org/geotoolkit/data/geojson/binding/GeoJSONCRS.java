/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson.binding;

import org.geotoolkit.data.geojson.utils.GeoJSONUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.geotoolkit.data.geojson.utils.GeoJSONTypes.*;
import static org.geotoolkit.data.geojson.utils.GeoJSONMembres.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONCRS implements Serializable {

    private String type;
    private Map<String, String> properties = new HashMap<>();

    public GeoJSONCRS() {
    }

    public GeoJSONCRS(CoordinateReferenceSystem crs) {
        type = CRS_NAME;
        String crsName = GeoJSONUtils.toURN(crs);
        if (crsName != null) {
            properties.put(NAME, crsName);
        }
    }

    public GeoJSONCRS(URL url, String crsType) {
        type = CRS_LINK;
        if (url != null && crsType != null) {
            properties.put(HREF, url.toString());
            properties.put(TYPE, crsType);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public CoordinateReferenceSystem getCRS() throws FactoryException, MalformedURLException {
        if (type.equals(CRS_NAME)) {
            return org.geotoolkit.referencing.CRS.decode(properties.get(NAME));
        } else if (type.equals(CRS_LINK)) {
            final String href = properties.get(HREF);
            final String crsType = properties.get(TYPE);
            return GeoJSONUtils.parseCRS(href, crsType);
        }
        return null;
    }

    public void setCRS(CoordinateReferenceSystem crs) {
        type = CRS_NAME;
        String crsName = GeoJSONUtils.toURN(crs);
        if (crsName != null) {
            properties = new HashMap<>();
            properties.put(NAME, crsName);
        }
    }
}
