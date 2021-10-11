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
import org.geotoolkit.feature.xml.GMLConvention;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.xsd.xml.v2001.Annotated;
import org.geotoolkit.xsd.xml.v2001.ComplexType;
import org.opengis.feature.Feature;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DecoratedMapping implements XSDMapping {

    private final ComplexType xsdType;
    private final PropertyType propertyType;

    public DecoratedMapping(ComplexType xsdType, PropertyType propertyType) {
        this.xsdType = xsdType;
        this.propertyType = propertyType;
    }

    @Override
    public IdentifiedType getType() {
        return propertyType;
    }

    @Override
    public void readValue(XMLStreamReader reader, GenericName propName, Feature feature) throws XMLStreamException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeValue(XMLStreamWriter writer, Object value) throws XMLStreamException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
    }

    public static final class Spi implements XSDMapping.Spi {

        @Override
        public XSDMapping create(GenericName name, JAXBFeatureTypeReader stack, Annotated xsdObject) {

            //check if we are dealing with a decorated property
            if (!(xsdObject instanceof ComplexType)) return null;
            final ComplexType ct = (ComplexType) xsdObject;
            final String tip = ct.getName();
            if (tip == null || !GMLConvention.isDecoratedProperty(tip)) return null;

            //TODO

            return null;

        }

        @Override
        public float getPriority() {
            return 0;
        }
    }

}
