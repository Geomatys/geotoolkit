/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.complex;


import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.ComplexDataType;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import org.geotoolkit.wps.xml.WPSXmlFactory;
import org.opengis.feature.FeatureType;



/**
 * Implementation of ObjectConverter to convert a FeatureType into a {@link ComplexDataType}.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class FeatureTypeToComplexConverter extends AbstractComplexOutputConverter<FeatureType> {

    private static FeatureTypeToComplexConverter INSTANCE;

    private FeatureTypeToComplexConverter(){
    }

    public static synchronized FeatureTypeToComplexConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FeatureTypeToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<FeatureType> getSourceClass() {
        return FeatureType.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComplexDataType convert(final FeatureType source, final Map<String, Object> params) throws UnconvertibleObjectException {


        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof FeatureType)) {
            throw new UnconvertibleObjectException("The requested output data is not an instance of FeatureType.");
        }
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        } 
        final ComplexDataType complex = WPSXmlFactory.buildComplexDataType(wpsVersion, (String) params.get(ENCODING),(String) params.get(MIME), null);

        try {

            final JAXBFeatureTypeWriter xmlWriter = new JAXBFeatureTypeWriter();
            complex.getContent().add(xmlWriter.writeToElement(source));

        } catch (JAXBException ex) {
            throw new UnconvertibleObjectException("Can't write FeatureType into ResponseDocument.",ex);
        } catch (ParserConfigurationException ex) {
            throw new UnconvertibleObjectException("Can't write FeatureType into ResponseDocument.",ex);
        }

       return  complex;

    }
}

