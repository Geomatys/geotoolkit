/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.data.gml;

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

import static org.geotoolkit.data.gml.GMLFeatureStore.*;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;

/**
 * FeatureStore for a folder of GML files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GMLFolderFeatureStoreFactory extends AbstractFolderFeatureStoreFactory{

    /** factory identification **/
    public static final DefaultServiceIdentification IDENTIFICATION = derivateIdentification(GMLFeatureStoreFactory.IDENTIFICATION);
    public static final String NAME = IDENTIFICATION.getCitation().getTitle().toString();

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(IDENTIFIER,GMLFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

    public GMLFolderFeatureStoreFactory(){
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
        return FeatureStoreFinder.getAllFactories(GMLFeatureStoreFactory.class).iterator().next();
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
            final File[] files = pathFile.listFiles(new ExtentionFileNameFilter(".gml",".xml"));
            return (files.length>0);
        }
        return false;
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, false, false, GEOMS_ALL);
    }

    //FileNameFilter implementation
    public static class ExtentionFileNameFilter implements FilenameFilter {

        private final String[] ext;

        public ExtentionFileNameFilter(String ... ext){
            this.ext = ext;
            for(int i=0;i<ext.length;i++){
                ext[i] = ext[i].toLowerCase();
            }
        }
        @Override
        public boolean accept(File dir, String name) {
            for(String str : ext){
                if(name.toLowerCase().endsWith(str)) return true;
            }
            return false;
        }
    }
}
