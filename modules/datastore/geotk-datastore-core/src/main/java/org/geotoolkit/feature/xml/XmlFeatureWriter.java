/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.xml;

import org.geotoolkit.data.collection.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A interface to serialize feature in XML.
 *
 * @module pending
 * 
 * @author Guilhem Legal (Geomatys)
 */
public interface XmlFeatureWriter {

    /**
     * Return an XML representation of the specified feature.
     *
     * @param feature The feature to marshall.
     * @return An XML string representing the feature.
     */
    String write(SimpleFeature feature);

    /**
     * Return an XML representation of the specified feature collection.
     *
     * @param feature The feature collection to marshall.
     * @return An XML string representing the feature collection.
     */
    String write(FeatureCollection featureCollection);
    
}
