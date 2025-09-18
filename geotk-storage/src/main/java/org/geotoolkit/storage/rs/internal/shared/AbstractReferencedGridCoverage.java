/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs.internal.shared;

import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.measure.IncommensurableException;
import org.apache.sis.coverage.BandedCoverage;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.Utilities;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.storage.rs.AddressIterator;
import org.geotoolkit.storage.rs.ReferencedGridCoverage;
import org.geotoolkit.storage.rs.ReferencedGridGeometry;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Referenced Coverage backed by a list of samples stored in TupleArrays.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractReferencedGridCoverage extends ReferencedGridCoverage{

    protected final GenericName name;
    protected final ReferencedGridGeometry gridGeometry;
    protected final GridExtent extent;
    protected final int dimension;
    //todo allow other kind of base reference system in the futur
    protected final DiscreteGlobalGridReferenceSystem horizontalRs;
    protected final DiscreteGlobalGridGeometry horizontalGrid;
    protected final Map<Object,Integer> index = new HashMap();
    protected final ReferenceSystem rs;
    protected final List<ReferenceSystem> singleRS;
    protected final CoordinateReferenceSystem compoundCrs;
    protected final int horizontalRsIndex;
    /**
     * Store the step between each cell for each dimension.
     * At the last dimension, the step size is 1.
     */
    protected final long[] dimStep;
    /**
     * Store the size of each dimension
     */
    protected final long[] dimSize;
    /**
     * Store the offset of each dimension
     */
    protected final long[] dimOffsets;

    //samples
    protected final FeatureType type;
    protected final List<SampleDimension> sampleDimensions;
    protected final String[] mapping;

    public AbstractReferencedGridCoverage(final GenericName name, ReferencedGridGeometry gridGeometry, FeatureType sampleType) throws FactoryException {
        this.name = name;
        this.gridGeometry = gridGeometry;
        this.extent = gridGeometry.getExtent();
        this.dimension = extent.getDimension();
        this.rs = gridGeometry.getReferenceSystem();
        this.horizontalRs = ReferenceSystems.getHorizontalComponent(rs).map(DiscreteGlobalGridReferenceSystem.class::cast).orElseThrow();
        this.horizontalGrid = (DiscreteGlobalGridGeometry) gridGeometry.slice(horizontalRs).get();
        this.singleRS = ReferenceSystems.getSingleComponents(rs, true);
        this.horizontalRsIndex = singleRS.indexOf(horizontalRs);

        //compute equivalent CRS
        final List<CoordinateReferenceSystem> ccrs = new ArrayList<>();
        for (int i = 0, n = singleRS.size(); i < n ;i++) {
            if (i == horizontalRsIndex) {
                ccrs.add(horizontalRs.getGridSystem().getCrs());
            } else {
                ccrs.add((CoordinateReferenceSystem) singleRS.get(i));
            }
        }
        compoundCrs = CRS.compound(ccrs.toArray(CoordinateReferenceSystem[]::new));

        //compute size of each dimension
        dimSize = new long[dimension];
        dimOffsets = new long[dimension];
        for (int i = 0; i < dimSize.length; i++) {
            dimSize[i] = extent.getSize(i);
            dimOffsets[i] = extent.getLow(i);
        }
        dimStep = new long[dimension];
        dimStep[dimension-1] = 1;
        for (int i = dimension - 2; i >= 0; i--) {
            dimStep[i] = dimStep[i+1] * dimSize[i+1];
        }

        //build an index
        //todo to remove, need something better then a List<Zone>
        final List<Object> zones = this.horizontalGrid.getZoneIds();
        for (int i = 0, n = zones.size(); i < n; i++) {
            index.put(zones.get(i), i);
        }

        type = sampleType;
        sampleDimensions = new ArrayList<>();
        for (PropertyType pt : type.getProperties(true)) {
            final String sdname = pt.getName().toString();
            final SampleDimension sd = new SampleDimension.Builder().setName(sdname).build();
            sampleDimensions.add(sd);
        }

        mapping = sampleDimensions.stream().map(SampleDimension::getName).map(Objects::toString).toArray(String[]::new);
    }

    @Override
    public List<SampleDimension> getSampleDimensions() {
        return Collections.unmodifiableList(sampleDimensions);
    }

    @Override
    public FeatureType getSampleType() {
        return type;
    }

    @Override
    public ReferencedGridGeometry getGeometry() {
        return gridGeometry;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return compoundCrs;
    }

    @Override
    public Optional<Envelope> getEnvelope() {
        return Optional.ofNullable(gridGeometry.getEnvelope());
    }

    @Override
    public double[] getResolution(boolean allowEstimate) {
        return gridGeometry.getResolution(allowEstimate);
    }

    @Override
    public GridCoverage sample(GridGeometry fullArea, GridGeometry tileArea) throws CannotEvaluateException {
        final List<SampleDimension> sampleDimensions = getSampleDimensions();

        try {
            final GridExtent extent = tileArea.getExtent();
            final int width = Math.toIntExact(extent.getSize(0));
            final int height = Math.toIntExact(extent.getSize(1));
            final long lowX = extent.getLow(0);
            final long lowY = extent.getLow(1);
            final MathTransform gridToCRS = tileArea.getGridToCRS(PixelInCell.CELL_CENTER);
            final BufferedImage image = BufferedImages.createImage(width, height, sampleDimensions.size(), DataBuffer.TYPE_DOUBLE);
            final WritableRaster raster = image.getRaster();

            // Verify no overflow is possible before allocating any array
            final int nbPts = Math.multiplyExact(width, height);
            final int xyLength = Math.multiplyExact(nbPts, 2);
            final double[] xyGrid = new double[xyLength];
            for (int y=0;y<height;y++) {
                for (int x=0;x<width;x++) {
                    int idx = (y * width + x) * 2;
                    xyGrid[idx] = lowX + x;
                    xyGrid[idx+1] = lowY + y;
                }
            }

            //convert to crs
            final double[] xyTin;
            final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(getCoordinateReferenceSystem());
            final CoordinateReferenceSystem gridCrs2d = CRS.getHorizontalComponent(tileArea.getCoordinateReferenceSystem());
            if (!Utilities.equalsIgnoreMetadata(gridCrs2d, crs2d)) {
                MathTransform trs = CRS.findOperation(gridCrs2d, crs2d, null).getMathTransform();
                trs = MathTransforms.concatenate(gridToCRS, trs);
                xyTin = xyGrid;
                trs.transform(xyTin, 0, xyTin, 0, xyTin.length/2);
            } else {
                gridToCRS.transform(xyGrid, 0, xyGrid, 0, xyGrid.length/2);
                xyTin = xyGrid;
            }

            final Evaluator evaluator = evaluator();
            evaluator.setNullIfOutside(true);
            final double[] none = new double[raster.getNumBands()];
            Arrays.fill(none, Double.NaN);

            final ThreadLocal<Evaluator> evaluators = ThreadLocal.withInitial(() -> {
                Evaluator eval = evaluator();
                eval.setNullIfOutside(true);
                return eval;
            });

            IntStream.range(0, xyTin.length/2).parallel().forEach((int i) -> {
                int imgx = i % width;
                int imgy = i / width;
                final DirectPosition2D dp = new DirectPosition2D();
                dp.x = xyTin[i*2];
                dp.y = xyTin[i*2+1];
                final double[] sample = evaluators.get().apply(dp);
                if (sample != null) {
                    raster.setPixel(imgx, imgy, sample);
                } else {
                    raster.setPixel(imgx, imgy, none);
                }
            });
            return new GridCoverageBuilder()
                    .setDomain(tileArea)
                    .setRanges(getSampleDimensions())
                    .setValues(raster)
                    .build();
        } catch (TransformException | FactoryException ex) {
            throw new CannotEvaluateException(ex.getMessage(), ex);
        }
    }

    @Override
    public Evaluator evaluator() {
        return new Eval();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.gridGeometry);
        hash = 59 * hash + Objects.hashCode(this.sampleDimensions);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractReferencedGridCoverage other = (AbstractReferencedGridCoverage) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.gridGeometry, other.gridGeometry)) {
            return false;
        }
        return Objects.equals(this.sampleDimensions, other.sampleDimensions);
    }

    private final class Eval implements Evaluator {

        private final DiscreteGlobalGridReferenceSystem.Coder coder;
        private boolean nullIfOutside = false;
        private boolean wraparoundEnabled = false;
        private final AddressIterator iterator;

        //cache last transforms
        private CoordinateReferenceSystem lastPosCrs;
        private MathTransform[] lastPosTransform;

        public Eval() {
            coder = horizontalRs.createCoder();
            iterator = createIterator();
            lastPosTransform = new MathTransform[singleRS.size()];
        }

        @Override
        public BandedCoverage getCoverage() {
            return AbstractReferencedGridCoverage.this;
        }

        @Override
        public boolean isNullIfOutside() {
            return nullIfOutside;
        }

        @Override
        public void setNullIfOutside(boolean flag) {
            this.nullIfOutside = flag;
        }

        @Override
        public boolean isWraparoundEnabled() {
            return wraparoundEnabled;
        }

        @Override
        public void setWraparoundEnabled(boolean allow) {
            this.wraparoundEnabled = allow;
        }

        @Override
        public double[] apply(DirectPosition dp) throws CannotEvaluateException {
            try {
                final NumberRange<Integer> refinementRange = horizontalGrid.getRefinementRange();
                final int minRefinement = (int) refinementRange.getMinDouble();
                final int maxRefinement = (int) refinementRange.getMaxDouble();
                if (minRefinement == maxRefinement) {
                    double[] cell = apply(dp, minRefinement);
                    if (cell != null) return cell;
                } else {
                    for (int i = minRefinement; i <= maxRefinement; i++) {
                        double[] cell = apply(dp, i);
                        if (cell != null) return cell;
                    }
                }
            } catch (TransformException | IncommensurableException ex) {
                throw new CannotEvaluateException(ex.getMessage(), ex);
            }

            if (nullIfOutside) {
                return null;
            } else {
                throw new PointOutsideCoverageException();
            }
        }

        private double[] apply(DirectPosition dp, int level) throws CannotEvaluateException, IncommensurableException, TransformException {
            updateSourceTransform(dp.getCoordinateReferenceSystem());
            final double[] coordinates = dp.getCoordinates();

            coder.setPrecisionLevel(level);
            final Object zoneId;
            try {
                zoneId = coder.encodeIdentifier(dp);
            } catch (IllegalArgumentException ex) {
                //coordinate outside dggrs supported area
                return null;
            }
            final Integer horIdx = index.get(zoneId);
            if (horIdx != null) {
                //compute the other dimensions
                final int[] gridPosition = new int[singleRS.size()];
                for (int i = 0; i < singleRS.size(); i++) {
                    if (i == horizontalRsIndex) {
                        gridPosition[i] = horIdx;
                    } else if (lastPosTransform[i] != null) {
                        final double[] target = new double[1];
                        lastPosTransform[i].transform(coordinates, 0, target, 0, 1);
                        gridPosition[i] = (int) Math.round(target[0]);
                        if (gridPosition[i] < 0 || gridPosition[i] > extent.getHigh(i)) {
                            //outside grid
                            return null;
                        }
                    }
                }

                //get cell value
                iterator.moveTo(gridPosition);
                final Feature sample = iterator.getSample();
                final double[] cell = new double[mapping.length];
                for (int i = 0; i < cell.length; i++) {
                    Object value = sample.getPropertyValue(mapping[i]);
                    cell[i] = value instanceof Number n ? n.doubleValue() : Double.NaN;
                }
                return cell;
            }
            return null;
        }

        private void updateSourceTransform(CoordinateReferenceSystem source) {
            if (lastPosCrs == source) return;

            lastPosTransform = new MathTransform[singleRS.size()];
            for (int i = 0; i < singleRS.size(); i++) {
                if (i != horizontalRsIndex) {
                    try {
                        MathTransform trs = CRS.findOperation(source, (CoordinateReferenceSystem) singleRS.get(i), null).getMathTransform();
                        lastPosTransform[i] = trs;
                    } catch (FactoryException ex) {
                    }
                }
            }
            lastPosCrs = source;
        }

    }

}
