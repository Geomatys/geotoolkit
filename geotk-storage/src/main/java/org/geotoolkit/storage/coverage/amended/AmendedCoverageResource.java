/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015-2019, Geomatys
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
package org.geotoolkit.storage.coverage.amended;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.privy.AxisDirections;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.event.CoverageStoreManagementEvent;
import org.geotoolkit.storage.event.StorageEvent;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

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
public class AmendedCoverageResource implements Resource, GridCoverageResource, StoreResource {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.coverage");

    protected final Set<StoreListener> listeners = new HashSet<>();
    protected final GridCoverageResource ref;
    protected final DataStore store;

    //source unmodified informations
    protected GridGeometry refGridGeom;
    protected List<SampleDimension> refDims;

    //overrided informations
    protected CoordinateReferenceSystem overrideCRS;
    protected PixelInCell overridePixelInCell;
    protected MathTransform overrideGridToCrs;
    protected List<SampleDimension> overrideDims;

    public AmendedCoverageResource(GridCoverageResource ref, DataStore store) {
        this.store = store;
        this.ref = ref;
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return ref.getIdentifier();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.of(getGridGeometry().getEnvelope());
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

    public synchronized GridGeometry getOriginalGridGeometry() throws DataStoreException {
        if (refGridGeom == null) {
            refGridGeom = ref.getGridGeometry();
        }
        return refGridGeom;
    }

    public synchronized List<SampleDimension> getOriginalSampleDimensions() throws DataStoreException {
        if (refDims == null) {
            refDims = ref.getSampleDimensions();
        }
        return refDims;
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
        try {
            sendStructureEvent(new CoverageStoreManagementEvent(this, CoverageStoreManagementEvent.Type.COVERAGE_UPDATE, getIdentifier().orElse(null), null, null));
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
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
        try {
            sendStructureEvent(new CoverageStoreManagementEvent(this, CoverageStoreManagementEvent.Type.COVERAGE_UPDATE, getIdentifier().orElse(null), null, null));
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Get overriden sample dimensions.
     *
     * @return List, can be null
     */
    public List<SampleDimension> getOverrideDims() {
        return overrideDims;
    }

    /**
     * Set sample dimensions override.
     *
     * @param overrideDims , can be null
     */
    public void setOverrideDims(List<SampleDimension> overrideDims) {
        this.overrideDims = overrideDims;
    }

    /**
     * Get overriden grid geometry.
     *
     * @return overridden grid geometry or original one is there are no overrides.
     */
    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        final GridGeometry originalGridGeometry = getOriginalGridGeometry();
        if (isGridGeometryOverriden()) {
            final GridExtent extent = originalGridGeometry.getExtent();
            return new GridGeometry(
                    extent,
                    overridePixelInCell!=null ? overridePixelInCell : PixelInCell.CELL_CENTER,
                    overrideGridToCrs!=null ? overrideGridToCrs : originalGridGeometry.getGridToCRS(PixelInCell.CELL_CENTER),
                    overrideCRS!=null ? overrideCRS : originalGridGeometry.getCoordinateReferenceSystem());
        } else {
            return originalGridGeometry;
        }
    }

    /**
     * Get overriden sample dimensions
     *
     * @return overridden sample dimensions or original ones is there are no overrides.
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        if (overrideDims != null) {
            return overrideDims;
        }else{
            return getOriginalSampleDimensions();
        }
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return ref.getMetadata();
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    private void sendStructureEvent(final StorageEvent event){
        final StoreListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StoreListener[listeners.size()]);
        }
        for (final StoreListener listener : lst) {
            listener.eventOccured(event);
        }
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        final GridCoverageReadParam param = new GridCoverageReadParam();
        if (range != null && range.length > 0) {
            param.setSourceBands(range);
            param.setDestinationBands(IntStream.range(0, range.length).toArray());
        }

        if (domain != null && domain.isDefined(org.apache.sis.coverage.grid.GridGeometry.ENVELOPE)) {
            /*
             * Modify envelope: when we encounter a slice, use the median value instead of the slice width
             * to avoid multiple coverage occurence of coverages at envelope border intersections.
             */
            Envelope envelope = domain.getEnvelope();
            int startDim = 0;
            GeneralEnvelope modified = null;
            final GridExtent extent = domain.getExtent();
            for (final SingleCRS part : CRS.getSingleComponents(envelope.getCoordinateReferenceSystem())) {
                final int crsDim = part.getCoordinateSystem().getDimension();
                if (crsDim == 1 && extent.getSize(startDim) == 1) {
                    if (modified == null) {
                        envelope = modified = new GeneralEnvelope(envelope);
                    }
                    double m = modified.getMedian(startDim);
                    modified.setRange(startDim, m, m);
                } else if (crsDim == 3) {
                    //might be 3d geographic/projected crs, see if we can split a vertical axis
                    VerticalCRS vcrs = CRS.getVerticalComponent(part, true);
                    if (vcrs != null) {
                        int idx = AxisDirections.indexOfColinear(part.getCoordinateSystem(), vcrs.getCoordinateSystem());
                        if (idx >= 0) {
                            if (modified == null) {
                                envelope = modified = new GeneralEnvelope(envelope);
                            }
                            try {
                                final int vidx = startDim + idx;
                                final MathTransform gridToCRS = domain.getGridToCRS(PixelInCell.CELL_CENTER);
                                final TransformSeparator ts = new TransformSeparator(gridToCRS);
                                ts.addTargetDimensions(vidx);
                                final MathTransform vtrs = ts.separate();
                                final double[] vcoord = new double[]{extent.getLow(vidx)};
                                vtrs.transform(vcoord, 0, vcoord, 0, 1);
                                final double m = vcoord[0];
                                modified.setRange(startDim+idx, m, m);
                            } catch (TransformException | FactoryException ex) {
                                //we have try, no luck
                            }
                        }
                    }
                }
                startDim += crsDim;
            }

            param.setEnvelope(envelope);
            final double[] resolution = domain.getResolution(true);
            param.setResolution(resolution);
        }

        return read(param);
    }

    private GridCoverage read(GridCoverageReadParam param) throws DataStoreException, CancellationException {

        GridCoverage coverage;
        if (isGridGeometryOverriden()) {

            final CoordinateReferenceSystem overrideCRS = getOverrideCRS();
            final MathTransform overrideGridToCrs = getOverrideGridToCrs();
            final PixelInCell overridePixelInCell = getOverridePixelInCell();
            final GridGeometry overrideGridGeometry = getGridGeometry();
            final GridGeometry originalGridGeometry = getOriginalGridGeometry();

            //convert parameters to fit overrides
            double[] queryRes = param==null ? null : param.getResolution();
            CoordinateReferenceSystem queryCrs = param==null ? null : param.getCoordinateReferenceSystem();
            Envelope queryEnv = param==null ? null : param.getEnvelope();

            //find requested envelope
            if (queryEnv == null && queryCrs != null) {
                try {
                    queryEnv = Envelopes.transform(overrideGridGeometry.getEnvelope(),queryCrs);
                } catch (TransformException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            //convert resolution to coverage crs
            double[] coverageRes = queryRes;
            if (queryRes != null) {
                try {
                    coverageRes = ReferencingUtilities.convertResolution(queryEnv, queryRes,
                            overrideGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            //get envelope in coverage crs
            Envelope coverageEnv;
            if (queryEnv == null) {
                //if no envelope is defined, use the full extent
                coverageEnv = overrideGridGeometry.getEnvelope();
            } else {
                try {
                    coverageEnv = Envelopes.transform(queryEnv, overrideGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            //change the crs to original one
            if (overrideCRS != null) {
                coverageEnv = new GeneralEnvelope(coverageEnv);
                if (originalGridGeometry.isDefined(GridGeometry.CRS)) {
                    ((GeneralEnvelope) coverageEnv).setCoordinateReferenceSystem(
                        originalGridGeometry.getCoordinateReferenceSystem());

                } else {
                    ((GeneralEnvelope) coverageEnv).setCoordinateReferenceSystem(null);
                }
            }

            //change the queried envelope
            MathTransform fixedToOriginal = null;
            MathTransform originalToFixed = null;
            if (overrideGridToCrs != null || overridePixelInCell != null) {
                try {
                    final MathTransform overrideCrsToGrid = overrideGridGeometry.getGridToCRS(PixelInCell.CELL_CENTER).inverse();
                    fixedToOriginal = MathTransforms.concatenate(overrideCrsToGrid, originalGridGeometry.getGridToCRS(PixelInCell.CELL_CENTER));
                    originalToFixed = fixedToOriginal.inverse();
                    coverageEnv = Envelopes.transform(fixedToOriginal, coverageEnv);
                    coverageEnv = new GeneralEnvelope(coverageEnv);
                    ((GeneralEnvelope) coverageEnv).setCoordinateReferenceSystem(
                            originalGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new DataStoreException(ex);
                }
            }

            if (originalToFixed!=null && coverageRes!=null) {
                //adjust resolution
                double s = AffineTransforms2D.getScale((AffineTransform2D)originalToFixed);
                coverageRes[0] /= s;
                coverageRes[1] /= s;
            }

            //query original reader
            final GridCoverageReadParam refParam = new GridCoverageReadParam(param);
            refParam.setResolution(coverageRes);
            refParam.setCoordinateReferenceSystem(null);
            refParam.setEnvelope(null);
            if (coverageEnv != null) {
                refParam.setCoordinateReferenceSystem(coverageEnv.getCoordinateReferenceSystem());
                refParam.setEnvelope(coverageEnv);
            }
            coverage = ref.read(toBaseGridGeometry(refParam));

            //fix coverage transform and crs
            final GridCoverageBuilder gcb = new GridCoverageBuilder();

            CoordinateReferenceSystem crs = coverage.getGridGeometry().isDefined(GridGeometry.CRS) ? coverage.getCoordinateReferenceSystem() : null;
            GridExtent extent = coverage.getGridGeometry().getExtent();
            MathTransform gridToCRS = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER);

            if (overrideCRS != null) {
                crs = overrideCRS;
            }
            if (overrideGridToCrs != null || overridePixelInCell != null) {
                gridToCRS = MathTransforms.concatenate(gridToCRS, originalToFixed);
            }

            gcb.setDomain(new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCRS, crs));
            gcb.setRanges(coverage.getSampleDimensions());
            gcb.setValues(coverage.render(null));
            coverage = gcb.build();

        } else {
            coverage = ref.read(toBaseGridGeometry(param));
        }

        //override sample dimensions
        final List<SampleDimension> overrideDims = getOverrideDims();
        if (overrideDims != null) {
            List<SampleDimension> sd = coverage.getSampleDimensions();

            //check if size match
            List<SampleDimension> overs = overrideDims;
            int[] sourceBands = param.getSourceBands();
            if (sourceBands != null) {
                //we make an extra check not all readers honor the band selection parameters
                if (sd == null || overrideDims.size() != sd.size()) {
                    overs = new ArrayList<>();
                    for (int i : sourceBands) {
                        overs.add(overrideDims.get(i));
                    }
                }
            }

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setValues(coverage.render(null));
            gcb.setDomain(coverage.getGridGeometry());
            gcb.setRanges(overs.toArray(new SampleDimension[overs.size()]));
            coverage = gcb.build();
        }

        return coverage;
    }

    private GridGeometry toBaseGridGeometry(GridCoverageReadParam param) throws DataStoreException {
        GridGeometry gridGeometry = ref.getGridGeometry();
        Envelope envelope = param.getEnvelope();
        double[] resolution = param.getResolution();

        return gridGeometry.derive().subgrid(envelope, resolution).build();

    }

}
