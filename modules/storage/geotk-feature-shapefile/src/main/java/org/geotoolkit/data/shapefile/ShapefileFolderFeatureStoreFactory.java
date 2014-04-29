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
package org.geotoolkit.data.shapefile;

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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

/**
 * FeatureStore for a folder of Shapefiles.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShapefileFolderFeatureStoreFactory extends AbstractFolderFeatureStoreFactory{

    /** factory identification **/
    public static final DefaultServiceIdentification IDENTIFICATION = derivateIdentification(ShapefileFeatureStoreFactory.IDENTIFICATION);
    public static final String NAME = IDENTIFICATION.getCitation().getTitle().toString();

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(IDENTIFIER,ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

    public ShapefileFolderFeatureStoreFactory(){
        super(PARAMETERS_DESCRIPTOR);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public FileFeatureStoreFactory getSingleFileFactory() {
        return FeatureStoreFinder.getAllFactories(ShapefileFeatureStoreFactory.class).iterator().next();
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/shapefile/bundle", "datastoreFolderDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/shapefile/bundle", "datastoreFolderTitle");
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

        final Boolean emptyDirectory = (Boolean) params.parameter(EMPTY_DIRECTORY.getName().toString()).getValue();
        if (pathFile.exists() && pathFile.isDirectory()){
            if(emptyDirectory.booleanValue()){
                return true;
            }
           File[] shapeFiles = pathFile.listFiles(new ExtentionFileNameFilter(".shp"));
           return shapeFiles.length>0;
        }
        return false;
    }




    //FileNameFilter implementation
    public static class ExtentionFileNameFilter implements FilenameFilter{

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
