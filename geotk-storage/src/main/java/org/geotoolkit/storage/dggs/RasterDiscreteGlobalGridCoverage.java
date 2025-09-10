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
package org.geotoolkit.storage.dggs;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.image.BufferedImages;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Discrete global grid coverage with data stored in a raster image.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class RasterDiscreteGlobalGridCoverage extends DiscreteGlobalGridCoverage{

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.storage.dggs");

    private final DiscreteGlobalGridGeometry geometry;
    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final List<ZonalIdentifier> zones;
    private final Map<ZonalIdentifier,Integer> index = new HashMap();
    private final List<SampleDimension> sampleDimensions;
    private final RenderedImage samples;
    private final Function<ZonalIdentifier,Point> zoneToGrid;
    /**
     * @todo not used yet, but will be.
     */
    private final Function<Point,ZonalIdentifier> gridToZone;

    private int minRefinement;
    private int maxRefinement;

    public RasterDiscreteGlobalGridCoverage(DiscreteGlobalGridGeometry geometry,
            List<SampleDimension> sampleDimensions,
            RenderedImage samples,
            Function<ZonalIdentifier,Point> zoneToGrid,
            Function<Point,ZonalIdentifier> gridToZone) {
        this.geometry = geometry;
        this.dggrs = geometry.getDiscreteGlobalGridReferenceSystem();
        this.sampleDimensions = sampleDimensions;
        this.zones = geometry.getZones();
        this.samples = samples;
        this.zoneToGrid = zoneToGrid;
        this.gridToZone = gridToZone;

        if (samples.getWidth() * samples.getHeight() != zones.size()) {
            throw new IllegalArgumentException("Number of image pixel do not match number of cells");
        }

        //build an index
        //todo to remove, need something better then a List<Zone>
        for (int i = 0, n = zones.size(); i < n; i++) {
            index.put(zones.get(i), i);
        }

        //find min and max refinement levels in the cells
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        minRefinement = this.dggrs.getGridSystem().getHierarchy().getGrids().size();
        maxRefinement = 0;
        try {
            for (ZonalIdentifier zone : zones) {
                final int level = coder.decode(zone).getLocationType().getRefinementLevel();
                if (level < minRefinement) minRefinement = level;
                if (level > maxRefinement) maxRefinement = level;
            }
        } catch (TransformException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    @Override
    public DiscreteGlobalGridGeometry getGeometry() {
        return geometry;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return dggrs.getGridSystem().getCrs();
    }

    @Override
    public Optional<Envelope> getEnvelope() {
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        GeneralEnvelope all = null;
        for (ZonalIdentifier zone : zones) {
            try {
                final Zone zo = coder.decode(zone);
                final Envelope env = zo.getEnvelope();
                if (env != null) {
                    if (all == null) {
                        all = new GeneralEnvelope(env);
                    } else {
                        all.add(env);
                    }
                }
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                return Optional.empty();
            }
        }
        return Optional.ofNullable(all);
    }

    @Override
    public double[] getResolution(boolean allowEstimate) throws DataStoreException {
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        final double[] res = new double[]{Double.NaN,Double.NaN};
        for (ZonalIdentifier zone : zones) {
            try {
                final Zone zo = coder.decode(zone);
                final Envelope env = zo.getEnvelope();
                if (env != null) {
                    double r0 = env.getSpan(0);
                    double r1 = env.getSpan(1);
                    if (Double.isNaN(res[0])) {
                        res[0] = r0;
                        res[1] = r1;
                    } else {
                        if (res[0] > r0) res[0] = r0;
                        if (res[1] > r1) res[1] = r1;
                    }
                }
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
        return res;
    }

    @Override
    public ZoneIterator createIterator() {
        return new Iterator(false);
    }

    @Override
    public WritableZoneIterator createWritableIterator() {
        return new Iterator(true);
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
            if (!CRS.equivalent(gridCrs2d, crs2d)) {
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
    public List<SampleDimension> getSampleDimensions() {
        return sampleDimensions;
    }

    @Override
    public Evaluator evaluator() {
        return new Eval();
    }

    private final class Iterator implements WritableZoneIterator {

        private int position = -1;

        private final PixelIterator cursor;
        private final DiscreteGlobalGridReferenceSystem.Coder coder;

        public Iterator(boolean writable) {
            coder = dggrs.createCoder();
            if (writable) {
                cursor = WritablePixelIterator.create(samples);
            } else {
                cursor = PixelIterator.create(samples);
            }

        }

        @Override
        public DiscreteGlobalGridReferenceSystem.Coder getCoder() {
            return coder;
        }

        @Override
        public void setSample(int band, double value) {
            final Point pt = zoneToGrid.apply(zones.get(position));
            cursor.moveTo(pt.x, pt.y);
            ((WritablePixelIterator)cursor).setSample(band, value);
        }

        @Override
        public int getNumBands() {
            return cursor.getNumBands();
        }

        @Override
        public ZonalIdentifier getPosition() {
            return zones.get(position);
        }

        @Override
        public Zone getZone() throws TransformException {
            return coder.decode(zones.get(position));
        }

        @Override
        public void moveTo(ZonalIdentifier zid) {
            Integer idx = index.get(zid);
            if (idx == null) {
                throw new IllegalArgumentException("Zone " + zid +" is not part of this coverage");
            }
            position = idx;
        }

        @Override
        public boolean next() {
            if (position < zones.size()-1) {
                position ++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public double getSampleDouble(int band) {
            final Point pt = zoneToGrid.apply(zones.get(position));
            cursor.moveTo(pt.x, pt.y);
            return cursor.getSampleDouble(band);
        }

        @Override
        public void rewind() {
            position = -1;
        }

        @Override
        public void close() throws DataStoreException {
        }

    }

    private final class Eval implements Evaluator {

        private final DiscreteGlobalGridReferenceSystem.Coder coder;
        private boolean nullIfOutside = false;
        private boolean wraparoundEnabled = false;
        private final PixelIterator cursor;

        public Eval() {
            coder = dggrs.createCoder();
            cursor = PixelIterator.create(samples);
        }

        @Override
        public BandedCoverage getCoverage() {
            return RasterDiscreteGlobalGridCoverage.this;
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
            coder.setPrecisionLevel(level);
            final ZonalIdentifier zoneId = coder.encodeIdentifier(dp);
            Integer idx = index.get(zoneId);
            if (idx != null) {
                final Point pt = zoneToGrid.apply(zones.get(idx));
                cursor.moveTo(pt.x, pt.y);
                return cursor.getPixel((double[])null);
            }
            return null;
        }

    }

}
