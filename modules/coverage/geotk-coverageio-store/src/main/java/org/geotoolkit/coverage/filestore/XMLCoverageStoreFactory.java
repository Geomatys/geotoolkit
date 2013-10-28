/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.filestore;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage store relying on an xml file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XMLCoverageStoreFactory extends AbstractCoverageStoreFactory{

    /** factory identification **/
    public static final String NAME = "coverage-xml-pyramid";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    /**
     * Mandatory - the folder path
     */
    public static final ParameterDescriptor<URL> PATH =
            new DefaultParameterDescriptor<>("path","folder path",URL.class,null,true);

    /**
     * Mandatory - the image reader type.
     * Use AUTO if type should be detected automaticaly.
     */
    public static final ParameterDescriptor<String> TYPE;
    static{
        final String code = "type";
        final CharSequence remarks = "Reader type";
        final Map<String,Object> params = new HashMap<>();
        params.put(DefaultParameterDescriptor.NAME_KEY, code);
        params.put(DefaultParameterDescriptor.REMARKS_KEY, remarks);
        final LinkedList<String> validValues = new LinkedList<>(FileCoverageStoreFactory.getReaderTypeList());
        validValues.add("AUTO");
        Collections.sort(validValues);

        TYPE = new DefaultParameterDescriptor<>(params, String.class,
                validValues.toArray(new String[validValues.size()]),
                "AUTO", null, null, null, true);
    }

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("XMLCoverageStoreParameters",
                IDENTIFIER,PATH,TYPE,NAMESPACE);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/coverage/bundle", "coverageXMLDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/coverage/bundle", "coverageXMLTitle");
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public CoverageStore open(ParameterValueGroup params) throws DataStoreException {
        if(!canProcess(params)){
            throw new DataStoreException("Can not process parameters.");
        }
        try {
            return new XMLCoverageStore(params);
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore create(ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public boolean canProcess(ParameterValueGroup params) {
        if (super.canProcess(params)) {
            URL pathValue = Parameters.value(PATH, params);
            try {
                final File root = new File(pathValue.toURI());
                if (root.isFile()) {
                    final XMLPyramidSet set = XMLPyramidSet.read(root);
                } else if (root.isDirectory()) {
                    // TODO : We use a filter based on file extension. Maybe we could find a better way ? (header reading ?)
                    final File[] xmlFiles = root.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.matches(".+\\.(?i)xml$");
                        }
                    });
                    
                    for (File f : xmlFiles) {
                        try {
                            final XMLPyramidSet set = XMLPyramidSet.read(f);
                            return true;
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }
            } catch (Exception ex) {
                // Nothing to do, we'll just tell user we can't process his parameters.
            }
        }
        
        return false;
    } 

    
}
