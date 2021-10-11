/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.v200.Reference;
import org.opengis.feature.FeatureType;

/**
 * Implementation of ObjectConverter to convert a {@link FeatureType feature type} into a {@link Reference reference}.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class FeatureTypeToReferenceConverter extends AbstractReferenceOutputConverter<FeatureType> {

    private static FeatureTypeToReferenceConverter INSTANCE;

    private FeatureTypeToReferenceConverter(){
    }

    public static synchronized FeatureTypeToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FeatureTypeToReferenceConverter();
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
    public Reference convert(final FeatureType source, final Map<String,Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        Reference reference = new Reference();

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));

        final String randomFileName = UUID.randomUUID().toString();

        //Write FeatureType
        try {

            final String schemaFileName = randomFileName + "_schema" + ".xsd";

            //create file
            final Path schemaFile = buildPath(params, schemaFileName);
            final OutputStream schemaStream = Files.newOutputStream(schemaFile);

            //write featureType xsd on file
            final JAXBFeatureTypeWriter xmlFTWriter = new JAXBFeatureTypeWriter();
            xmlFTWriter.write(source, schemaStream);

            final String relLoc = getRelativeLocation(schemaFile, params);
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" +relLoc);

        } catch (JAXBException ex) {
            throw new UnconvertibleObjectException("Can't write FeatureType into xsd schema.",ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Can't create xsd schema file.",ex);
        }

        return reference;
    }

}
