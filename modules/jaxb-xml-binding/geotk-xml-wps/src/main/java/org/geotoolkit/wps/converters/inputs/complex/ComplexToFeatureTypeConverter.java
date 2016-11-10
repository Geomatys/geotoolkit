/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters.inputs.complex;


import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.ComplexDataType;
import org.geotoolkit.feature.type.FeatureType;
import org.w3c.dom.Node;


/**
 * Implementation of ObjectConverter to convert a complex input into a FeatureType.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ComplexToFeatureTypeConverter extends AbstractComplexInputConverter<FeatureType> {

    private static ComplexToFeatureTypeConverter INSTANCE;

    private ComplexToFeatureTypeConverter(){
    }

    public static synchronized ComplexToFeatureTypeConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ComplexToFeatureTypeConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureType> getTargetClass() {
        return FeatureType.class;
    }

    /**
     * {@inheritDoc}
     * @return FeatureType
     */
    @Override
    public FeatureType convert(final ComplexDataType source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final List<Object> data = source.getContent();
        if(data.size() > 1){
           throw new UnconvertibleObjectException("Invalid data input : Only one FeatureType expected.");
        }

        try {
            final JAXBFeatureTypeReader xsdReader = new JAXBFeatureTypeReader();
            final List<FeatureType> ft = xsdReader.read((Node)data.get(0));
            return ft.get(0);

        } catch (JAXBException ex) {
            throw new UnconvertibleObjectException("Unable to read feature type from xsd.", ex);
        }
    }
}

