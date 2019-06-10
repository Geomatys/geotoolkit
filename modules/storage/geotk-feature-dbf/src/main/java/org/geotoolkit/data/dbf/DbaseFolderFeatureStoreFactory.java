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
package org.geotoolkit.data.dbf;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import org.geotoolkit.data.AbstractFolderFeatureStoreFactory;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * FeatureStore for a folder of DBF files.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadataExt(resourceTypes = ResourceType.VECTOR, canCreate = true, canWrite = true)
public class DbaseFolderFeatureStoreFactory extends AbstractFolderFeatureStoreFactory{

    public static final String NAME = derivateName(DbaseFeatureStoreFactory.NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(NAME,DbaseFeatureStoreFactory.PARAMETERS_DESCRIPTOR);


    public DbaseFolderFeatureStoreFactory(){
        super(PARAMETERS_DESCRIPTOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DbaseFeatureStoreFactory getSingleFileFactory() {
        return DataStores.getAllFactories(DbaseFeatureStoreFactory.class).iterator().next();
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.databaseFolderDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.databaseFolderTitle);
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

        final URI path = (URI)obj;
        Path pathFile = Paths.get(path);

        if (Files.isDirectory(pathFile)){
            boolean dbfFiles = false;
            boolean shpFiles = false;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(pathFile)) {
                for (Path candidate : stream) {
                    final String ext = IOUtilities.extension(candidate);
                    if ("dbf".equalsIgnoreCase(ext)) {
                        dbfFiles = true;
                    }
                    if ("shp".equalsIgnoreCase(ext)) {
                        shpFiles = true;
                    }

                }
            } catch (IOException e) {
                LOGGER.log(Level.FINE, e.getLocalizedMessage());
            }
            return (dbfFiles && shpFiles);
        }
        return false;
    }

}
