/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.data.AbstractFolderFeatureStoreFactory;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;

/**
 * FeatureStore for a folder of MIF files.
 *
 * @author Alexis Manin (Geomatys)
 */
public class MIFFolderFeatureStoreFactory extends AbstractFolderFeatureStoreFactory {

    /** factory identification **/
    public static final DefaultServiceIdentification IDENTIFICATION = derivateIdentification(MIFFeatureStoreFactory.IDENTIFICATION);
    public static final String NAME = IDENTIFICATION.getCitation().getTitle().toString();

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(IDENTIFIER, MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

    public MIFFolderFeatureStoreFactory(){
        super(PARAMETERS_DESCRIPTOR);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public FileFeatureStoreFactory getSingleFileFactory() {
        return FeatureStoreFinder.getAllFactories(MIFFeatureStoreFactory.class).iterator().next();
    }

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreFolderDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreFolderTitle);
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

        final Boolean emptyDirectory = (Boolean) params.parameter(EMPTY_DIRECTORY.getName().toString()).getValue();
        final URL url = (URL)obj;
        try {
            Path path = IOUtilities.toPath(url);

            if (Files.exists(path) && Files.isDirectory(path)){
                if(emptyDirectory){
                    return true;
                }

                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path, "*.mif")) {
                    return dirStream.iterator().hasNext();
                }
            }
        } catch (IOException e) {
            // Should not happen if the url is well-formed.
            LOGGER.log(Level.INFO, e.getLocalizedMessage());
        }

        return false;
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, false, false, GEOMS_ALL);
    }
    
}
