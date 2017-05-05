/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.wps.converters.outputs.references;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;

import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import static org.geotoolkit.wps.converters.WPSObjectConverter.IOTYPE;
import static org.geotoolkit.wps.converters.WPSObjectConverter.WPSVERSION;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.WPSXmlFactory;
import org.opengis.sld.StyledLayerDescriptor;

/**
 * Implementation of ObjectConverter to convert a {@link StyledLayerDescriptor sld} into a {@link OutputReferenceType reference}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StyledLayerDescriptorToReferenceConverter extends AbstractReferenceOutputConverter<StyledLayerDescriptor> {

    private static StyledLayerDescriptorToReferenceConverter INSTANCE;

    private StyledLayerDescriptorToReferenceConverter(){
    }

    public static synchronized StyledLayerDescriptorToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StyledLayerDescriptorToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<StyledLayerDescriptor> getSourceClass() {
        return StyledLayerDescriptor.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reference convert(final StyledLayerDescriptor source, final Map<String,Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }
        if (source == null) {
            throw new UnconvertibleObjectException("The source is not defined.");
        }

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }
        Reference reference = WPSXmlFactory.buildInOutReference(wpsVersion, ioType);

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));


        if(WPSMimeType.APP_SLD.val().equalsIgnoreCase(reference.getMimeType())) {
            //create file
            final String randomFileName = UUID.randomUUID().toString();
            final String dataFileName = randomFileName+".sld";
            final File dataFile = new File((String) params.get(TMP_DIR_PATH), dataFileName);
            try {
                StyleXmlIO xmlio = new StyleXmlIO();
                xmlio.writeSLD(dataFile, source, Specification.StyledLayerDescriptor.V_1_1_0);
            } catch (JAXBException e) {
                throw new UnconvertibleObjectException(e.getMessage(), e);
            }

            reference.setHref(params.get(TMP_DIR_URL) + "/" +dataFileName);

        } else {
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + reference.getMimeType());
        }

        return reference;
    }
}
