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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import static org.apache.sis.storage.DataStoreProvider.LOCATION;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.PathFilterVisitor;
import org.geotoolkit.nio.PosixPathMatcher;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
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
public class ShapefileFolderProvider extends DataStoreProvider {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data");

    /** factory identification **/
    public static final String NAME = derivateName(ShapefileProvider.NAME);

    /**
     * url to the folder.
     */
    public static final ParameterDescriptor<URI> FOLDER_PATH = new ParameterBuilder()
            .addName("path")
            .addName(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.paramPathAlias))
            .setRemarks(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.paramPathRemarks))
            .setRequired(true)
            .create(URI.class, null);

    /**
     * recursively search folder.
     */
    public static final ParameterDescriptor<Boolean> RECURSIVE = new ParameterBuilder()
            .addName("recursive")
            .addName(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.recursive))
            .setRemarks(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.recursive_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.TRUE);

    public static final ParameterDescriptor<Boolean> EMPTY_DIRECTORY = new ParameterBuilder()
            .addName("emptyDirectory")
            .addName(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.emptyDirectory))
            .setRemarks(org.geotoolkit.storage.Bundle.formatInternational(org.geotoolkit.storage.Bundle.Keys.emptyDirectory_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    private final ParameterDescriptorGroup paramDesc;

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            derivateDescriptor(NAME,ShapefileProvider.PARAMETERS_DESCRIPTOR);

    public ShapefileFolderProvider(){
        paramDesc = PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return paramDesc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new DefaultFolderFeatureStore(params,this);
    }

    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        //we can open an empty featurestore of this type
        //the open featurestore will always work, it will just be empty if there are no files in it.
        return open(params);
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        //always false
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    /**
     * Derivate a folder factory name from original single file factory.
     */
    protected static String derivateName(final String name){
        return name+"-folder";
    }

    /**
     * Create a Folder FeatureStore descriptor group based on the single file factory
     * parameters.
     *
     * @param name factory name
     * @param sd single file factory parameters.
     * @return ParameterDescriptorGroup
     */
    protected static ParameterDescriptorGroup derivateDescriptor(
            final String name, final ParameterDescriptorGroup sd){

        final List<GeneralParameterDescriptor> params = new ArrayList<>(sd.descriptors());
        for(int i=0;i<params.size();i++){
            if(params.get(i).getName().getCode().equals(DataStoreFactory.IDENTIFIER.getName().getCode())){
                params.remove(i);
                break;
            }
        }

        params.remove(ShapefileProvider.PATH);
        params.add(0, FOLDER_PATH);
        params.add(1, RECURSIVE);
        params.add(2, EMPTY_DIRECTORY);

        final ParameterBuilder pb = new ParameterBuilder();
        pb.addName(name);
        //old name as alias for backward compatibility
        for (String singleName : IdentifiedObjects.getNames(sd, null)) {
            pb.addName(singleName+"Folder");
        }

        return pb.createGroup(params.toArray(new GeneralParameterDescriptor[params.size()]));
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    public ShapefileProvider getSingleFileFactory() {
        return (ShapefileProvider) DataStores.getProviderById(ShapefileProvider.NAME);
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreFolderDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreFolderTitle);
    }

    public boolean canProcess(final ParameterValueGroup params) {
        boolean valid;
        {
            valid = canProcessAbs(params);
            if (valid) {
                final Object obj = params.parameter(FOLDER_PATH.getName().toString()).getValue();
                if(!(obj instanceof URI)){
                    return false;
                }

                final URI path = (URI)obj;
                Path pathFile = Paths.get(path);
                valid = Files.isDirectory(pathFile);
            }
        }

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

    private boolean canProcessAbs(final ParameterValueGroup params) {
        if(params == null){
            return false;
        }

        final ParameterDescriptorGroup desc = getOpenParameters();
        if(!desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())){
            return false;
        }

        final ConformanceResult result = Parameters.isValid(params, desc);
        return (result != null) && Boolean.TRUE.equals(result.pass());
    }


    private static boolean containsShp(Path folder, boolean recursive) throws IOException {

        int depth = recursive ? Integer.MAX_VALUE : 1;
        PathFilterVisitor visitor = new PathFilterVisitor(new PosixPathMatcher("*.shp", Boolean.TRUE));
        Files.walkFileTree(folder, EnumSet.of(FileVisitOption.FOLLOW_LINKS), depth, visitor);

        return !(visitor.getMatchedPaths().isEmpty());
    }

    @Override
    public org.apache.sis.storage.DataStore open(StorageConnector connector) throws DataStoreException {
        GeneralParameterDescriptor desc;
        try {
            desc = getOpenParameters().descriptor(LOCATION);
        } catch (ParameterNotFoundException e) {
            throw new DataStoreException("Unsupported input");
        }

        if (!(desc instanceof ParameterDescriptor)) {
            throw new DataStoreException("Unsupported input");
        }

        try {
            final Object locationValue = connector.commit(((ParameterDescriptor)desc).getValueClass(), NAME);
            final ParameterValueGroup params = getOpenParameters().createValue();
            params.parameter(LOCATION).setValue(locationValue);

            if (canProcess(params)) {
                return open(params);
            }
        } catch(IllegalArgumentException ex) {
            throw new DataStoreException("Unsupported input:" + ex.getMessage());
        }

        throw new DataStoreException("Unsupported input");
    }

    /**
     * @param params
     * @see #checkIdentifier(org.opengis.parameter.ParameterValueGroup)
     * @throws DataStoreException if identifier is not valid
     */
    protected void ensureCanProcess(final ParameterValueGroup params) throws DataStoreException{
        final boolean valid = canProcess(params);
        if(!valid){
            throw new DataStoreException("Parameter values not supported by this factory.");
        }
    }

}
