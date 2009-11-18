/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.io.InputStream;
import java.io.Reader;
import org.opengis.feature.type.FeatureType;

/**
 * An interface for feature / feature collection XML parsing.
 *
 * @module pending
 * @author Guilhem Legal (Geomatys)
 */
public interface XmlFeatureReader {

    /**
     * Read a feature or featureCollection from the specified String XML representation.
     *
     * @param xml An Xml representation of the feature/feature collection.
     *
     * @return A SimpleFeature / featureCollection or {@code null}
     */
    public Object read(String xml);

    /**
     * Read a feature or featureCollection from the specified XML stream.
     *
     * @param xml An Xml representation of the feature/feature collection.
     *
     * @return A SimpleFeature / featureCollection or {@code null}
     */
    public Object read(InputStream in);

    /**
     * Read a feature or featureCollection from the specified XML reader.
     *
     * @param xml An Xml representation of the feature/feature collection.
     *
     * @return A SimpleFeature / featureCollection or {@code null}
     */
    public Object read(Reader reader);

    public void setFeatureType(FeatureType featureType);
}
