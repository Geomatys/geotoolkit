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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.TreeTable.Node;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.storage.AbstractDataStore;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.geotoolkit.feature.type.Name;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Abstract implementation of a coverage store.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCoverageStore extends CoverageStore {


    protected static final String NO_NAMESPACE = "no namespace";

    private final Logger Logger = Logging.getLogger(getClass().getPackage().getName());
    private final String defaultNamespace;
    protected final ParameterValueGroup parameters;
    protected final Set<StorageListener> listeners = new HashSet<>();

    private final HashMap<Name, CoverageReference> cachedRefs = new HashMap<>();

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
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return null;
    }

    @Override
    public ParameterValueGroup getConfiguration() {
        return parameters;
    }

    protected String getDefaultNamespace() {
        return defaultNamespace;
    }

    protected Logger getLogger(){
        return Logger;
    }


    /**
     * Returns the root node of the data store.
     * This node is the main access point to the content of the store.
     *
     * TODO move this in Apache SIS DataStore class when ready
     *
     * @return DataNode never null.
     */
    public abstract DataNode getRootNode() throws DataStoreException;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Classes.getShortClassName(this));
        try {
            final DataNode node = getRootNode();
            sb.append(' ');
            sb.append(node.toString());
        } catch (DataStoreException ex) {
            Logger.getLogger(AbstractDataStore.class.getName()).log(Level.WARNING, null, ex);
        }

        return sb.toString();
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        throw new DataStoreException("Creation of new coverage not supported.");
    }

    @Override
    public void delete(Name name) throws DataStoreException {
        throw new DataStoreException("Deletion of coverage not supported.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Convinient methods, fallback on getRootNode                            //
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public final Set<Name> getNames() throws DataStoreException {
        final Map<Name,CoverageReference> map = listReferences();
        return map.keySet();
    }

    @Override
    public final CoverageReference getCoverageReference(Name name) throws DataStoreException {
        final Map<Name,CoverageReference> map = listReferences();
        final CoverageReference ref = map.get(name);
        if(ref==null){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(name);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final Name n : map.keySet()){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
        return ref;
    }

    protected Map<Name,CoverageReference> listReferences() throws DataStoreException {
        if (cachedRefs.isEmpty()) {
            listReferences(getRootNode(), cachedRefs);
        }
        return cachedRefs;
    }

    private Map<Name,CoverageReference> listReferences(Node node, Map<Name,CoverageReference> map){

        if(node instanceof CoverageReference){
            final CoverageReference cr = (CoverageReference) node;
            map.put(cr.getName(), cr);
        }

        for(Node child : node.getChildren()){
            listReferences(child, map);
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
    public VersionControl getVersioning(Name typeName) throws VersioningException {
        throw new VersioningException("Versioning not supported");
    }

    @Override
    public CoverageReference getCoverageReference(Name name, Version version) throws DataStoreException {
        throw new DataStoreException("Versioning not supported");
    }

    ////////////////////////////////////////////////////////////////////////////
    // convinient methods                                                     //
    ////////////////////////////////////////////////////////////////////////////

    protected CoverageStoreManagementEvent fireCoverageAdded(final Name name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageAddEvent(this, name);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireCoverageUpdated(final Name name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageUpdateEvent(this, name);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireCoverageDeleted(final Name name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageDeleteEvent(this, name);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidAdded(final Name name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidAddEvent(this, name, pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidUpdated(final Name name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidUpdateEvent(this, name, pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidDeleted(final Name name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidDeleteEvent(this, name, pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicAdded(final Name name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicAddEvent(this, name, pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicUpdated(final Name name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicUpdateEvent(this, name, pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicDeleted(final Name name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicDeleteEvent(this, name, pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireDataUpdated(final Name name){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createDataUpdateEvent(this, name);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileAdded(final Name name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileAddEvent(this, name, pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileUpdated(final Name name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileUpdateEvent(this, name, pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileDeleted(final Name name,
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
    protected void typeCheck(final Name candidate) throws DataStoreException{

        final Collection<Name> names = getNames();
        if(!names.contains(candidate)){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(candidate);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final Name n : names){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
    }

    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendStructureEvent(final StorageEvent event){
        cachedRefs.clear();
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
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
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
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

}
