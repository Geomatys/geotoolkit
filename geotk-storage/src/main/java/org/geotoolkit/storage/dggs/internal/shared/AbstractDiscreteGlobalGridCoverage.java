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
package org.geotoolkit.storage.dggs.internal.shared;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.measure.IncommensurableException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.wrapper.jts.JTS;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.storage.rs.internal.shared.BandedCodeIterator;
import org.geotoolkit.storage.rs.internal.shared.WritableBandedCodeIterator;
import org.locationtech.jts.geom.Polygon;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractDiscreteGlobalGridCoverage extends DiscreteGlobalGridCoverage{

    protected final DiscreteGlobalGridGeometry gridGeometry;
    protected final DiscreteGlobalGridReferenceSystem dggrs;
    protected final List<Object> zones;
    protected final Map<Object,Integer> index = new HashMap();

    public AbstractDiscreteGlobalGridCoverage(DiscreteGlobalGridGeometry gridGeometry) {
        this.gridGeometry = gridGeometry;
        this.dggrs = gridGeometry.getReferenceSystem();
        this.zones = this.gridGeometry.getZoneIds();

        //build an index
        //todo to remove, need something better then a List<Zone>
        for (int i = 0, n = zones.size(); i < n; i++) {
            index.put(zones.get(i), i);
        }
    }

    @Override
    public DiscreteGlobalGridGeometry getGeometry() {
        return gridGeometry;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return getGeometry().getReferenceSystem().getGridSystem().getCrs();
    }

    @Override
    public Optional<Envelope> getEnvelope() {
        return Optional.ofNullable(gridGeometry.getEnvelope());
    }

    @Override
    public double[] getResolution(boolean allowEstimate) throws DataStoreException {
        return gridGeometry.getResolution(allowEstimate);
    }

    @Override
    public abstract BandedCodeIterator createIterator();

    @Override
    public abstract WritableBandedCodeIterator createWritableIterator();

    @Override
    public GridCoverage sample(GridGeometry fullArea, GridGeometry tileArea) throws CannotEvaluateException {
        return sampleByMask(fullArea, tileArea);
    }

    private GridCoverage sampleByMask(GridGeometry fullArea, GridGeometry tileArea) throws CannotEvaluateException {
        final List<SampleDimension> sampleDimensions = getSampleDimensions();

        try {
            tileArea = CoverageUtilities.forceLowerToZero(tileArea);

            //start by creating a mask
            final GridExtent extent = tileArea.getExtent();
            final int width = Math.toIntExact(extent.getSize(0));
            final int height = Math.toIntExact(extent.getSize(1));
            final MathTransform gridToCRS = tileArea.getGridToCRS(PixelInCell.CELL_CENTER);
            final BufferedImage maskImage = BufferedImages.createImage(width, height, 1, DataBuffer.TYPE_BYTE);
            final Graphics2D g = maskImage.createGraphics();
            g.setColor(Color.WHITE);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            final WritableRaster maskRaster = maskImage.getRaster();
            final Rectangle imgRect = new Rectangle(width, height);
            final Area bbox = new Area(imgRect);

            final MathTransform zoneToCrs = CRS.findOperation(dggrs.getGridSystem().getCrs(), tileArea.getCoordinateReferenceSystem(), null).getMathTransform();
            final MathTransform zoneToGrid = MathTransforms.concatenate(zoneToCrs, gridToCRS.inverse());

            //prepare the coverage
            final double[] fillValue = new double[sampleDimensions.size()];
            Arrays.fill(fillValue, Double.NaN);
            final BufferedImage image = BufferedImages.createImage(width, height, sampleDimensions.size(), DataBuffer.TYPE_DOUBLE, fillValue);
            final WritableRaster raster = image.getRaster();

            final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
            final BandedCodeIterator zoneIterator = createIterator();
            double[] cell = null;
            while (zoneIterator.next()) {
                final int[] position = zoneIterator.getPosition();
                final Object zid = zones.get(position[0]);
                final Zone zone = coder.decode(zid);
                final Polygon polygon = DiscreteGlobalGridSystems.toJTSPolygon(zone.getGeographicExtent());
                final Shape shape = JTS.asShape(JTS.transform(polygon, zoneToGrid));
                final Area area = new Area(shape);
                area.intersect(bbox);
                if (!area.isEmpty()) {
                    g.fill(area);
                    Rectangle bounds = area.getBounds();

                    //fill the corresponding pixel in the target
                    final PixelIterator rite = new PixelIterator.Builder().setRegionOfInterest(bounds).create(maskImage);
                    while (rite.next()) {
                        if (rite.getSample(0) != 0) {
                            Point pt = rite.getPosition();
                            cell = zoneIterator.getCell(cell);
                            raster.setPixel(pt.x, pt.y, cell);
                            maskRaster.setSample(pt.x, pt.y, 0, 0);
                        }
                    }
                }
            }

            g.dispose();

            return new GridCoverageBuilder()
                    .setDomain(tileArea)
                    .setRanges(getSampleDimensions())
                    .setValues(raster)
                    .build();
        } catch (TransformException | FactoryException ex) {
            throw new CannotEvaluateException(ex.getMessage(), ex);
        }
    }

    private GridCoverage sampleByEvaluator(GridGeometry fullArea, GridGeometry tileArea) throws CannotEvaluateException {
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

    private final class Eval implements Evaluator {

        private final DiscreteGlobalGridReferenceSystem.Coder coder;
        private boolean nullIfOutside = false;
        private boolean wraparoundEnabled = false;
        private final BandedCodeIterator iterator;

        public Eval() {
            coder = dggrs.createCoder();
            iterator = createIterator();
        }

        @Override
        public AbstractDiscreteGlobalGridCoverage getCoverage() {
            return AbstractDiscreteGlobalGridCoverage.this;
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
                final NumberRange<Integer> refinementRange = getGeometry().getRefinementRange();
                final int minRefinement = refinementRange.getMinValue();
                final int maxRefinement = refinementRange.getMaxValue();
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
            final Object zoneId;
            try {
                zoneId = coder.encodeIdentifier(dp);
            } catch (IllegalArgumentException ex) {
                //coordinate outside dggrs supported area
                return null;
            }
            final Integer idx = index.get(zoneId);
            if (idx != null) {
                iterator.moveTo(new int[]{idx});
                return iterator.getCell((double[])null);
            }
            return null;
        }

    }
}
