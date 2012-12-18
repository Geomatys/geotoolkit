/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.ogc.xml;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FilterXmlFactory {

    public static XMLFilter buildFeatureIDFilter(final String currentVersion, final String featureId) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.FilterType(new org.geotoolkit.ogc.xml.v200.ResourceIdType(featureId));
            
        } else if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.FilterType(new org.geotoolkit.ogc.xml.v110.FeatureIdType(featureId));
            
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v100.FilterType(new org.geotoolkit.ogc.xml.v100.FeatureIdType(featureId));
            
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
