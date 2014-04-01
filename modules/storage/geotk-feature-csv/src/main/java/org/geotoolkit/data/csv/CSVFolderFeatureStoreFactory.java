/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.data.csv;

import org.geotoolkit.data.AbstractFeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.data.AbstractFolderFeatureStoreFactory;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import static org.geotoolkit.data.csv.CSVFeatureStore.*;

/**
 * FeatureStore for a folder of CSV files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVFolderFeatureStoreFactory extends AbstractFolderFeatureStoreFactory{

    /** factory identification **/
    public static final DefaultServiceIdentification IDENTIFICATION = derivateIdentification(CSVFeatureStoreFactory.IDENTIFICATION);
    public static final String NAME = IDENTIFICATION.getCitation().getTitle().toString();

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(IDENTIFIER,CSVFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

    public CSVFolderFeatureStoreFactory(){
        super(PARAMETERS_DESCRIPTOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileFeatureStoreFactory getSingleFileFactory() {
        return FeatureStoreFinder.getAllFactories(CSVFeatureStoreFactory.class).iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString(BUNDLE_PATH, "datastoreFolderDescription");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString(BUNDLE_PATH, "datastoreFolderTitle");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        final boolean valid = super.canProcess(params);
        if (!valid) {
            return false;
        }

        final Object obj = params.parameter(URLFOLDER.getName().toString()).getValue();
        if(!(obj instanceof URL)){
            return false;
        }

        final URL path = (URL)obj;
        File pathFile;
        try {
            pathFile = new File(path.toURI());
        } catch (URISyntaxException e) {
            // Should not happen if the url is well-formed.
            LOGGER.log(Level.INFO, e.getLocalizedMessage());
            pathFile = new File(path.toExternalForm());
        }
        if (pathFile.exists() && pathFile.isDirectory()){
            File[] CSVFiles = pathFile.listFiles(new ExtentionFileNameFilter(".csv"));
            return (CSVFiles.length>0);
        }
        return false;
    }

    //FileNameFilter implementation
    public static class ExtentionFileNameFilter implements FilenameFilter {

        private String ext;

        public ExtentionFileNameFilter(String ext){
            this.ext = ext.toLowerCase();
        }
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(ext);
        }
    }
}
