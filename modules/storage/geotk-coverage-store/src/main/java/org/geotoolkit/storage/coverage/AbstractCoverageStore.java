/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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

package org.geotoolkit.storage.coverage;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.TreeTable;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.storage.DataSet;
import org.geotoolkit.storage.DataStore;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.util.GenericName;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Abstract implementation of a coverage store.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractCoverageStore extends DataStore implements CoverageStore {

    protected static final String NO_NAMESPACE = "no namespace";

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");
    private final String defaultNamespace;
    protected final ParameterValueGroup parameters;
    protected final Set<StorageListener> storeListeners = new HashSet<>();

    private final HashMap<GenericName, CoverageResource> cachedRefs = new HashMap<>();

    protected AbstractCoverageStore(final ParameterValueGroup params) {
        this.parameters = params;

        ParameterValue pv = ParametersExt.getValue(params, AbstractCoverageStoreFactory.NAMESPACE.getName().getCode());
        String namespace = (pv==null) ? null : pv.stringValue();

        if (namespace == null) {
            defaultNamespace = "http://geotoolkit.org";
        } else if (namespace.equals(NO_NAMESPACE)) {
            defaultNamespace = null;
        } else {
            defaultNamespace = namespace;
        }

        //redirect warning listener events to default logger
        listeners.getLogger().setUseParentHandlers(false);
        listeners.getLogger().addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                getLogger().log(record);
            }
            @Override
            public void flush() {}
            @Override
            public void close() throws SecurityException {}
        });
    }

    /**
     * Create a new metadata containing information about this datastore and the
     * coverages it contains.
     *
     * Note : Analysis should be restricted to report only information currently
     * available in this dataset. Further computing should be performed externally.
     *
     * Note 2 : You can decide how extents are stored in the metadata by overriding
     * solely {@link #setSpatialInfo(java.util.Map) } only.
     *
     * @return Created metadata, Can be null if no data is available at the
     * moment.
     *
     * @throws DataStoreException If an error occurs while analyzing underlying
     * data.
     */
    @Override
    protected Metadata createMetadata() throws DataStoreException {
        final Resource root = getRootResource();
        if (root == null) {
            return null;
        }

        final DefaultMetadata rootMd = new DefaultMetadata();

        // Queries data specific information
        final Map<GenericName, GeneralGridGeometry> geometries = new HashMap<>();
        final List<CoverageResource> refs = flattenSubTree(root)
                .filter(node -> node instanceof CoverageResource)
                .map(node -> ((CoverageResource) node))
                .collect(Collectors.toList());

        for (final CoverageResource ref : refs) {
            final GridCoverageReader reader = ref.acquireReader();
            final SpatialMetadata md;
            final GeneralGridGeometry gg;
            try {
                md = reader.getCoverageMetadata(ref.getImageIndex());
                gg = reader.getGridGeometry(ref.getImageIndex());
                ref.recycle(reader);
            } catch (Exception e) {
                // If something turned wrong, we definitively get rid of the reader.
                reader.dispose();
                throw e;
            }

            if (gg != null) {
                geometries.put(ref.getName(), gg);
            }

            if (md != null) {
                final CoverageDescription cd = md.getInstanceForType(CoverageDescription.class); // ImageDescription
                if (cd != null)
                    rootMd.getContentInfo().add(cd);
            }
        }

        setSpatialInfo(rootMd, geometries);

        return rootMd;
    }

    /**
     * Compute extents to set in store's metadata. This analysis is separated in
     * a method so inheriting stores will be able to customize it easily.
     * This method is needed because geographic information could be read differently
     * according to its structure. Example :
     * - If the metadata represents two distinct data, we should have two distinct
     * extents
     * - If the metadata describes an non-continuous data cube, we should have a
     * single extent which contains multiple disjoint geographic/temporal/elevation
     * extents.
     *
     * Note : Default algorithm is really simple. We put all envelopes in a simple
     * extent, which will directly contain the list of geographic, temporal and
     * vertical extents for each reference.
     *
     * We'll also add all reference systems found in the input grid geometries if
     * they're not here already.
     *
     * @param md The metadata to update
     * @param geometries The grid geometries of each store's reference, grouped
     * by reference name.
     */
    protected void setSpatialInfo(final Metadata md, final Map<GenericName, GeneralGridGeometry> geometries) {
        if (geometries == null || geometries.isEmpty())
            return;

        // HACk : create temporary sets to automatically remove doublon extents.
        final DefaultExtent extent = new DefaultExtent() {
            @Override
            protected <E> Class<? extends Collection<E>> collectionType(Class<E> elementType) {
                return (Class) Set.class;
            }
        };

        final Set<CoordinateReferenceSystem> crss = new HashSet<>();
        geometries.forEach((name, gg) -> {
            try {
                extent.addElements(gg.getEnvelope());
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, "Extent cannot be computed for reference " + name, ex);
            }

            crss.add(gg.getCoordinateReferenceSystem());
        });

        /* Hack : copy original extents, so allocated sets are transformed into
         * lists. It is necessary, so if someone modifies an inner extent, the set
         * uniquenes won't be messed.
         */
        final DefaultDataIdentification ddi = new DefaultDataIdentification();
        ddi.getExtents().add(new DefaultExtent(extent));
        ((Collection)md.getIdentificationInfo()).add(ddi);

        // Ensure we'll have no doublon
        crss.removeAll(md.getReferenceSystemInfo());
        md.getReferenceSystemInfo().addAll((Collection)crss);
    }

    @Override
    public ParameterValueGroup getConfiguration() {
        return parameters;
    }

    protected String getDefaultNamespace() {
        return defaultNamespace;
    }

    protected Logger getLogger(){
        return LOGGER;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Classes.getShortClassName(this));
        try {
            final Resource node = getRootResource();
            sb.append(' ');
            sb.append(node.toString());
        } catch (DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.storage").log(Level.WARNING, null, ex);
        }

        return sb.toString();
    }

    @Override
    public CoverageResource create(GenericName name) throws DataStoreException {
        throw new DataStoreException("Creation of new coverage not supported.");
    }

    @Override
    public void delete(GenericName name) throws DataStoreException {
        throw new DataStoreException("Deletion of coverage not supported.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Convinient methods, fallback on getRootResource                            //
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public final Set<GenericName> getNames() throws DataStoreException {
        final Map<GenericName,CoverageResource> map = listReferences();
        return map.keySet();
    }

    @Override
    public final CoverageResource getCoverageResource(GenericName name) throws DataStoreException {
        final Map<GenericName,CoverageResource> map = listReferences();
        final CoverageResource ref = map.get(name);
        if(ref==null){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(name);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final GenericName n : map.keySet()){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
        return ref;
    }

    protected Map<GenericName,CoverageResource> listReferences() throws DataStoreException {
        if (cachedRefs.isEmpty()) {
            listReferences(getRootResource(), cachedRefs);
        }
        return cachedRefs;
    }

    private Map<GenericName,CoverageResource> listReferences(Resource candidate, Map<GenericName,CoverageResource> map){

        if(candidate instanceof CoverageResource){
            final CoverageResource cr = (CoverageResource) candidate;
            map.put(cr.getName(), cr);
        }

        if (candidate instanceof DataSet) {
            for(Resource child : ((DataSet)candidate).getResources()){
                listReferences(child, map);
            }
        }

        return map;
    }

    ////////////////////////////////////////////////////////////////////////////
    // versioning methods : handle nothing by default                         //
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean handleVersioning() {
        return false;
    }

    @Override
    public VersionControl getVersioning(GenericName typeName) throws VersioningException {
        throw new VersioningException("Versioning not supported");
    }

    @Override
    public CoverageResource getCoverageResource(GenericName name, Version version) throws DataStoreException {
        throw new DataStoreException("Versioning not supported");
    }

    ////////////////////////////////////////////////////////////////////////////
    // convinient methods                                                     //
    ////////////////////////////////////////////////////////////////////////////

    protected CoverageStoreManagementEvent fireCoverageAdded(final GenericName name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageAddEvent(this, name);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireCoverageUpdated(final GenericName name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageUpdateEvent(this, name);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireCoverageDeleted(final GenericName name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageDeleteEvent(this, name);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidAdded(final GenericName name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidAddEvent(this, name, pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidUpdated(final GenericName name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidUpdateEvent(this, name, pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidDeleted(final GenericName name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidDeleteEvent(this, name, pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicAdded(final GenericName name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicAddEvent(this, name, pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicUpdated(final GenericName name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicUpdateEvent(this, name, pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicDeleted(final GenericName name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicDeleteEvent(this, name, pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireDataUpdated(final GenericName name){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createDataUpdateEvent(this, name);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileAdded(final GenericName name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileAddEvent(this, name, pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileUpdated(final GenericName name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileUpdateEvent(this, name, pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileDeleted(final GenericName name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileDeleteEvent(this, name, pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    /**
     * Convinient method to check that the given type name exist.
     * Will raise a datastore exception if the name do not exist in this datastore.
     * @param candidate Name to test.
     * @throws DataStoreException if name do not exist.
     */
    protected void typeCheck(final GenericName candidate) throws DataStoreException{

        final Collection<GenericName> names = getNames();
        if(!names.contains(candidate)){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(candidate);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final GenericName n : names){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
    }

    public void addStorageListener(final StorageListener listener) {
        synchronized (storeListeners) {
            storeListeners.add(listener);
        }
    }

    public void removeStorageListener(final StorageListener listener) {
        synchronized (storeListeners) {
            storeListeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendStructureEvent(final StorageEvent event){
        cachedRefs.clear();
        final StorageListener[] lst;
        synchronized (storeListeners) {
            lst = storeListeners.toArray(new StorageListener[storeListeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward a data event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendContentEvent(final StorageEvent event){
        final StorageListener[] lst;
        synchronized (storeListeners) {
            lst = storeListeners.toArray(new StorageListener[storeListeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardStructureEvent(StorageEvent event){
        sendStructureEvent(event.copy(this));
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardContentEvent(StorageEvent event){
        sendContentEvent(event.copy(this));
    }

    /**
     * Send back a list of all nodes in a tree. Nodes are ordered by depth-first
     * encounter order.
     *
     * @param root Node to start flattening from. It will be included in result.
     * @return A list of all nodes under given root.
     * @throws NullPointerException If input node is null.
     */
    public static Stream<? extends Resource> flattenSubTree(final Resource root) throws NullPointerException {
        Stream<Resource> nodeStream = Stream.of(root);
        if (root instanceof DataSet) {
            nodeStream = Stream.concat( nodeStream,
                    ((DataSet) root).getResources().stream()
                            .flatMap(AbstractCoverageStore::flattenSubTree)
            );
        }
        return nodeStream;
    }
}
