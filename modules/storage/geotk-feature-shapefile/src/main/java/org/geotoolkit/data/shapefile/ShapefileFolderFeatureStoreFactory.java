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

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.EnumSet;
import java.util.logging.Level;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.PathFilterVisitor;
import org.geotoolkit.nio.PosixPathMatcher;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.feature.AbstractFolderFeatureStoreFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * FeatureStore for a folder of Shapefiles.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = "shapefile-folder",
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        resourceTypes = {FeatureSet.class})
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        geometryTypes ={Point.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class ShapefileFolderFeatureStoreFactory extends AbstractFolderFeatureStoreFactory{

    /** factory identification **/
    public static final String NAME = derivateName(ShapefileFeatureStoreFactory.NAME);

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(NAME,ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

    public ShapefileFolderFeatureStoreFactory(){
        super(PARAMETERS_DESCRIPTOR);
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ShapefileFeatureStoreFactory getSingleFileFactory() {
        return (ShapefileFeatureStoreFactory) DataStores.getProviderById(ShapefileFeatureStoreFactory.NAME);
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreFolderDescription);
    }

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

        final Boolean emptyDirectory = (Boolean) params.parameter(EMPTY_DIRECTORY.getName().toString()).getValue();
        final Boolean recursive = (Boolean) params.parameter(RECURSIVE.getName().toString()).getValue();

        final URI url = (URI)obj;
        try {
            Path path = IOUtilities.toPath(url);
            if (Files.isDirectory(path)){
                if(Boolean.TRUE.equals(emptyDirectory)){
                    return true;
                }
                return containsShp(path, Boolean.TRUE.equals(recursive));
            }
        } catch (IOException e) {
            // Should not happen if the url is well-formed.
            LOGGER.log(Level.INFO, e.getLocalizedMessage());
        }

        return false;
    }

    private static boolean containsShp(Path folder, boolean recursive) throws IOException {

        int depth = recursive ? Integer.MAX_VALUE : 1;
        PathFilterVisitor visitor = new PathFilterVisitor(new PosixPathMatcher("*.shp", Boolean.TRUE));
        Files.walkFileTree(folder, EnumSet.of(FileVisitOption.FOLLOW_LINKS), depth, visitor);

        return !(visitor.getMatchedPaths().isEmpty());
    }

}
