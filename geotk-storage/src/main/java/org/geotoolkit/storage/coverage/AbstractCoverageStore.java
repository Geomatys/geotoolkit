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

import org.geotoolkit.storage.event.CoverageStoreManagementEvent;
import org.geotoolkit.storage.event.CoverageStoreContentEvent;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.storage.event.WarningEvent;
import org.apache.sis.util.Classes;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.event.StorageEvent;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Abstract implementation of a coverage store.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractCoverageStore extends DataStore implements AutoCloseable, Resource {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.storage.coverage");
    protected final Parameters parameters;

    protected AbstractCoverageStore(final ParameterValueGroup params) {
        this.parameters = Parameters.castOrWrap(params);

        //redirect warning listener events to default logger
        listeners.addListener(WarningEvent.class, new StoreListener<WarningEvent>() {
            @Override public void eventOccured(WarningEvent t) {
                t.getDescription().setLoggerName("org.geotoolkit.storage.coverage");
            }
        });
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.empty();
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
    public Metadata getMetadata() throws DataStoreException {

        final DefaultMetadata rootMd = new DefaultMetadata();

        // Queries data specific information
        final Map<GenericName, GridGeometry> geometries = new HashMap<>();
        final Collection<org.apache.sis.storage.GridCoverageResource> refs = DataStores.flatten(this,true, org.apache.sis.storage.GridCoverageResource.class);

        for (final org.apache.sis.storage.GridCoverageResource ref : refs) {
            final Metadata md;
            final GridGeometry gg;
            try {
                md = ref.getMetadata();
                gg = ref.getGridGeometry();
            } catch (Exception e) {
                // If something turned wrong, we definitively get rid of the reader.
                throw e;
            }

            if (gg != null) {
                geometries.put(ref.getIdentifier().orElse(null), gg);
            }

            if (md != null && md.getContentInfo() != null) {
                for (ContentInformation ci : md.getContentInfo()) {
                    if (ci instanceof CoverageDescription) {
                        rootMd.getContentInfo().add(ci);
                    }
                }
            }
        }
        setSpatialInfo(rootMd, geometries);
        return rootMd;
    }

    /**
     * Compute extents to set in store's metadata. This analysis is separated in
     * a method so inheriting stores will be able to customize it easily.
     * This method is needed because geographic information could be features differently
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
    protected void setSpatialInfo(final Metadata md, final Map<GenericName, GridGeometry> geometries) {
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
            if (gg.isDefined(GridGeometry.ENVELOPE)) {
                try {
                    extent.addElements(gg.getEnvelope());
                } catch (TransformException | IncompleteGridGeometryException ex) {
                    LOGGER.log(Level.WARNING, "Extent cannot be computed for reference " + name, ex);
                }
            }
            if (gg.isDefined(GridGeometry.CRS)) {
                try {
                    crss.add(gg.getCoordinateReferenceSystem());
                } catch (IncompleteGridGeometryException ex) {
                    LOGGER.log(Level.WARNING, "CRS cannot be computed for reference " + name, ex);
                }
            }
        });

        /* Hack : copy original extents, so allocated sets are transformed into
         * lists. It is necessary, so if someone modifies an inner extent, the set
         * uniquenes won't be messed.
         */
        final DefaultDataIdentification ddi = new DefaultDataIdentification();
        ddi.getExtents().add(new DefaultExtent(extent));
        ((Collection)md.getIdentificationInfo()).add(ddi);

        // Ensure we'll have no duplicate
        crss.removeAll(md.getReferenceSystemInfo());
        md.getReferenceSystemInfo().addAll((Collection)crss);
    }

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.ofNullable(parameters);
    }

    protected Logger getLogger(){
        return LOGGER;
    }


    @Override
    public String toString() {
        return Classes.getShortClassName(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // convinient methods                                                     //
    ////////////////////////////////////////////////////////////////////////////

    protected CoverageStoreManagementEvent fireCoverageAdded(final GenericName name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageAddEvent(this, name);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireCoverageUpdated(final GenericName name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageUpdateEvent(this, name);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireCoverageDeleted(final GenericName name){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createCoverageDeleteEvent(this, name);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidAdded(final GenericName name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidAddEvent(this, name, pyramidId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidUpdated(final GenericName name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidUpdateEvent(this, name, pyramidId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidDeleted(final GenericName name, final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidDeleteEvent(this, name, pyramidId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicAdded(final GenericName name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicAddEvent(this, name, pyramidId, mosaicId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicUpdated(final GenericName name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicUpdateEvent(this, name, pyramidId, mosaicId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicDeleted(final GenericName name, final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicDeleteEvent(this, name, pyramidId, mosaicId);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireDataUpdated(final GenericName name){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createDataUpdateEvent(this, name);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileAdded(final GenericName name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileAddEvent(this, name, pyramidId, mosaicId, tiles);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileUpdated(final GenericName name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileUpdateEvent(this, name, pyramidId, mosaicId, tiles);
        sendEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileDeleted(final GenericName name,
            final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileDeleteEvent(this, name, pyramidId, mosaicId, tiles);
        sendEvent(event);
        return event;
    }

    /**
     * Convinient method to check that the given type name exist.
     * Will raise a datastore exception if the name do not exist in this datastore.
     * @param candidate Name to test.
     * @throws DataStoreException if name do not exist.
     */
    protected void typeCheck(final GenericName candidate) throws DataStoreException{

        final Collection<GenericName> names = DataStores.getNames(this, true, DataSet.class);
        if (!names.contains(candidate)) {
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(candidate);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final GenericName n : names){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
    }

    /**
     * Forward event to all listeners.
     * @param event , event to send to listeners.
     *
     * @todo should specify the event type.
     */
    protected void sendEvent(final StoreEvent event) {
        listeners.fire(StoreEvent.class, event);
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardEvent(StorageEvent event){
        sendEvent(event.copy(this));
    }
}
