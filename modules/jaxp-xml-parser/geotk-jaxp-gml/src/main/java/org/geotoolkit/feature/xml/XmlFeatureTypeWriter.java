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

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.opengis.feature.type.FeatureType;
import org.w3c.dom.Node;

/**
 *  An interface for feature type XML writing.
 *
 * @module pending
 * @author Guilhem Legal (Geomatys)
 */
public interface XmlFeatureTypeWriter extends Configurable {

    /**
     * Return an XML representation of the specified featureType.
     *
     * @param feature The featureType to marshall.
     * @return An XML string representing the featureType.
     */
    String write(FeatureType feature) throws JAXBException;

    /**
     * Write an XML representation of the specified featureType into the Writer.
     *
     * @param feature The featureType to marshall.
     */
    void write(FeatureType feature, Writer writer) throws JAXBException;


    /**
     * Write an XML representation of the specified featureType into the Stream.
     *
     * @param feature The featureType to marshall.
     */
    void write(FeatureType feature, OutputStream stream) throws JAXBException;

    /**
     * Write an XML representation of the specified featureType into an Element.
     * @param feature
     * @return the xml element.
     * @throws JAXBException
     * @throws ParserConfigurationException 
     */
    Node writeToElement(FeatureType feature) throws JAXBException, ParserConfigurationException;
    
    /**
     * Create an xsd schema from a list of feature type.
     * 
     * @param featureTypes
     * @return
     */
    Schema getSchemaFromFeatureType(List<FeatureType> featureTypes);

    /**
     * Create a xsd schema from a feature type.
     * 
     * @param featureType
     * @return
     */
    Schema getSchemaFromFeatureType(FeatureType featureType);
}
