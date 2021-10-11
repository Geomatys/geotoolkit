/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.feature.xml.jaxb.mapping;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.xsd.xml.v2001.Annotated;
import org.opengis.feature.Feature;
import org.opengis.feature.IdentifiedType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface XSDMapping extends AutoCloseable {

    IdentifiedType getType();

    /**
     * Read value from xml reader and store it in given feature.
     *
     * @param reader XML reader
     * @param propName property name in the feature
     * @param feature feature to store the property in
     * @throws XMLStreamException
     */
    void readValue(XMLStreamReader reader, GenericName propName, Feature feature) throws XMLStreamException;

    void writeValue(XMLStreamWriter writer, Object value) throws XMLStreamException;

    /**
     * Release any encoding or decoding resources.
     */
    @Override
    void close();

    public interface Spi {

        float getPriority();

        XSDMapping create(GenericName name, JAXBFeatureTypeReader stack, Annotated xsdObject);

    }

}
