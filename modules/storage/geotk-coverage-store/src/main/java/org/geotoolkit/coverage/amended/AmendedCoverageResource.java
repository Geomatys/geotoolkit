/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.amended;

import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.coverage.GridSampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.CoverageWriter;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

/**
 * Decorates a coverage reference adding possibility to override properties.
 * <br>
 * <br>
 * List of properties which can be override : <br>
 * <ul>
 *  <li>CRS</li>
 *  <li>GridToCRS</li>
 *  <li>PixelInCell</li>
 *  <li>Sample dimensions</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 */
public class AmendedCoverageResource implements Resource,GridCoverageResource{

    protected final Set<ChangeListener> listeners = new HashSet<>();
    protected final GridCoverageResource ref;
    protected final DataStore store;

    //source unmodified informations
    protected GridGeometry refGridGeom;
    protected List<GridSampleDimension> refDims;

    //overrided informations
    protected CoordinateReferenceSystem overrideCRS;
    protected PixelInCell overridePixelInCell;
    protected MathTransform overrideGridToCrs;
    protected List<GridSampleDimension> overrideDims;

    public AmendedCoverageResource(GridCoverageResource ref, DataStore store) {
        this.store = store;
        this.ref = ref;
    }

    @Override
    public DataStore getStore() {
        return store;
    }

    @Override
    public NamedIdentifier getIdentifier() {
        return ref.getIdentifier();
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return getGridGeometry(0).getEnvelope();
    }

    private void loadRefData(int index) throws CoverageStoreException {
        if(refGridGeom==null){
            final GridCoverageReader reader = ref.acquireReader();
            refGridGeom = reader.getGridGeometry(index);
            refDims = reader.getSampleDimensions(index);
            ref.recycle(reader);
        }
    }

    /**
     * Get decorated coverage reference.
     *
     * @return CoverageResource, never null.
     */
    public GridCoverageResource getDecorated(){
        return ref;
    }

    /**
     * Check if at least one of the grid geometry properties has been overriden.
     *
     * @return true of grid geometry is override
     */
    public boolean isGridGeometryOverriden(){
        return overrideCRS!=null
            || overrideGridToCrs!=null
            || overridePixelInCell!=null;
    }

    /**
     *
     * @param index
     * @return
     * @throws CoverageStoreException
     */
    public GridGeometry getOriginalGridGeometry(int index) throws CoverageStoreException{
        loadRefData(index);
        return refGridGeom;
    }

    /**
     * Get overriden CRS.
     *
     * @return CoordinateReferenceSystem, can be null
     */
    public CoordinateReferenceSystem getOverrideCRS() {
        return overrideCRS;
    }

    /**
     * Set crs override.
     *
     * @param overrideCRS , can be null
     */
    public void setOverrideCRS(CoordinateReferenceSystem overrideCRS) {
        if(this.overrideCRS==overrideCRS) return;
        this.overrideCRS = overrideCRS;
        sendStructureEvent(new CoverageStoreManagementEvent(this, CoverageStoreManagementEvent.Type.COVERAGE_UPDATE, getIdentifier(), null, null));
    }

    /**
     * Get overriden PixelInCell.
     *
     * @return PixelInCell, can be null
     */
    public PixelInCell getOverridePixelInCell() {
        return overridePixelInCell;
    }

    /**
     * Set PixelInCell override.
     * Default is PixelInCell.CELL_CENTER.
     *
     * @param overridePixelInCell , can be null
     */
    public void setOverridePixelInCell(PixelInCell overridePixelInCell) {
        this.overridePixelInCell = overridePixelInCell;
    }

    /**
     * Get overriden grid to crs transform.
     *
     * @return MathTransform, can be null
     */
    public MathTransform getOverrideGridToCrs() {
        return overrideGridToCrs;
    }

    /**
     * Set grid to crs transform override.
     * This transform is relative to PixelInCell.CELL_CENTER if PixelInCell property has not been override.
     *
     * @param overrideGridToCrs , can be null
     */
    public void setOverrideGridToCrs(MathTransform overrideGridToCrs) {
        if(this.overrideGridToCrs==overrideGridToCrs) return;
        this.overrideGridToCrs = overrideGridToCrs;
        sendStructureEvent(new CoverageStoreManagementEvent(this, CoverageStoreManagementEvent.Type.COVERAGE_UPDATE, getIdentifier(), null, null));
    }

    /**
     * Get overriden sample dimensions.
     *
     * @return List, can be null
     */
    public List<GridSampleDimension> getOverrideDims() {
        return overrideDims;
    }

    /**
     * Set sample dimensions override.
     *
     * @param overrideDims , can be null
     */
    public void setOverrideDims(List<GridSampleDimension> overrideDims) {
        this.overrideDims = overrideDims;
    }

    /**
     * Get overriden grid geometry.
     *
     * @param index image index in reader
     * @return overridden grid geometry or original one is there are no overrides.
     * @throws CoverageStoreException
     */
    public GridGeometry getGridGeometry(int index) throws CoverageStoreException{
        loadRefData(index);
        if(isGridGeometryOverriden()){
            if(refGridGeom instanceof GridGeometry2D){
                final GridExtent extent = refGridGeom.getExtent();
                return new GridGeometry2D(
                        extent,
                        overridePixelInCell!=null ? overridePixelInCell : PixelInCell.CELL_CENTER,
                        overrideGridToCrs!=null ? overrideGridToCrs : refGridGeom.getGridToCRS(PixelInCell.CELL_CENTER),
                        overrideCRS!=null ? overrideCRS : refGridGeom.getCoordinateReferenceSystem(),
                        null);
            }else{
                final GridExtent extent = refGridGeom.getExtent();
                return new GridGeometry(
                        extent,
                        overridePixelInCell!=null ? overridePixelInCell : PixelInCell.CELL_CENTER,
                        overrideGridToCrs!=null ? overrideGridToCrs : refGridGeom.getGridToCRS(PixelInCell.CELL_CENTER),
                        overrideCRS!=null ? overrideCRS : refGridGeom.getCoordinateReferenceSystem());
            }
        }else{
            return refGridGeom;
        }
    }

    /**
     * Get overriden sample dimensions
     *
     * @param index image index in reader
     * @return overridden sample dimensions or original ones is there are no overrides.
     * @throws CoverageStoreException
     */
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException{
        loadRefData(index);
        if(overrideDims!=null){
            return overrideDims;
        }else{
            return refDims;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getImageIndex() {
        return ref.getImageIndex();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageDescription getCoverageDescription() {
        return ref.getCoverageDescription();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isWritable() throws DataStoreException {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        return new AmendedCoverageReader(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new CoverageStoreException("Not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void recycle(CoverageReader reader) {
        ((AmendedCoverageReader)reader).dispose();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void recycle(CoverageWriter writer) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Image getLegend() throws DataStoreException {
        return ref.getLegend();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return ref.getMetadata();
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    private void sendStructureEvent(final StorageEvent event){
        final ChangeListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new ChangeListener[listeners.size()]);
        }
        for (final ChangeListener listener : lst) {
            listener.changeOccured(event);
        }
    }

}
