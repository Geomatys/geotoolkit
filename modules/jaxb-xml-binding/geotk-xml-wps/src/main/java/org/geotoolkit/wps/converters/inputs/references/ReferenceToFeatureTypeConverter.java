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
package org.geotoolkit.wps.converters.inputs.references;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.geotoolkit.feature.type.FeatureType;


/**
 * Implementation of ObjectConverter to convert a reference into a FeatureType.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ReferenceToFeatureTypeConverter extends AbstractReferenceInputConverter<FeatureType> {

    private static ReferenceToFeatureTypeConverter INSTANCE;

    private ReferenceToFeatureTypeConverter(){
    }

    public static synchronized ReferenceToFeatureTypeConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ReferenceToFeatureTypeConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureType> getTargetClass() {
        return FeatureType.class;
    }

    /**
     * {@inheritDoc}
     * @return FeatureType.
     */
    @Override
    public FeatureType convert(final ReferenceType source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final String mime = source.getMimeType() != null ? source.getMimeType() : WPSMimeType.TEXT_XML.val();
        final InputStream stream = getInputStreamFromReference(source);

        //XML
        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val()) || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val()) ||
                mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {
             try {
                final XmlFeatureTypeReader xsdReader = new JAXBFeatureTypeReader();
                final List<FeatureType> ft = xsdReader.read(stream);

                if(ft.size() != 1){
                    throw new UnconvertibleObjectException("Invalid reference input : More than one FeatureType in schema.");
                }
                return ft.get(0);
            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Invalid reference input : can't read reference schema.",ex);
            }
        }else {
             throw new UnconvertibleObjectException("Reference data mime is not supported");
        }
    }
}