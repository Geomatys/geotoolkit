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

import java.io.Writer;
import java.io.OutputStream;
import org.geotoolkit.data.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A interface to serialize feature in XML.
 *
 * @module pending
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
     * Write an XML representation of the specified feature into the Writer.
     *
     * @param feature The feature to marshall.
     */
    void write(SimpleFeature feature, Writer writer);


    /**
     * Write an XML representation of the specified feature into the Stream.
     *
     * @param feature The feature to marshall.
     */
    void write(SimpleFeature feature, OutputStream stream);
    
    /**
     * Return an XML representation of the specified feature collection.
     *
     * @param feature The feature collection to marshall.
     * @return An XML string representing the feature collection.
     */
    String write(FeatureCollection featureCollection);

    /**
     * Write an XML representation of the specified feature collection into the Writer.
     *
     * @param feature The feature to marshall.
     */
    void write(FeatureCollection collection, Writer writer);


    /**
     * Write an XML representation of the specified feature collection into the Stream.
     *
     * @param feature The feature to marshall.
     */
    void write(FeatureCollection collection, OutputStream stream);

    /**
     * Free the resources.
     */
    void dispose();
    
}
