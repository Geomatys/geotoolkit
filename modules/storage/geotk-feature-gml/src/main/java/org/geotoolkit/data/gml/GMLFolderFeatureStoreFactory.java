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
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.PosixDirectoryFilter;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

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
        return Bundle.formatInternational(Bundle.Keys.datastoreFolderDescription);
    }

    /**
     * {@inheritDoc}
     */
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

        final Object obj = params.parameter(FOLDER_PATH.getName().toString()).getValue();
        if(!(obj instanceof URI)){
            return false;
        }

        final URI url = (URI)obj;

        try {
            Path path = IOUtilities.toPath(url);
            if (Files.isDirectory(path)){
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path, new PosixDirectoryFilter("*.gml", true))) {
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
