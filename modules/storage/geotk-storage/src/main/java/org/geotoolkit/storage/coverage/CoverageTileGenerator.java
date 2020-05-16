/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.image.Interpolation;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.storage.memory.InMemoryPyramidResource;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.geotoolkit.storage.multires.DefaultPyramid;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.util.Streams;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageTileGenerator extends AbstractTileGenerator {

    private final GridCoverageResource resource;
    private final double[] empty;

    private InterpolationCase interpolation = InterpolationCase.NEIGHBOR;
    private double[] fillValues;
    private boolean coverageIsHomogeneous = true;
    private boolean skipExistingTiles = false;

    public CoverageTileGenerator(GridCoverageResource resource) throws DataStoreException {
        ArgumentChecks.ensureNonNull("resource", resource);

        this.resource = resource;

        try {
            List<SampleDimension> sampleDimensions = resource.getSampleDimensions();
            if (sampleDimensions == null || sampleDimensions.isEmpty()) {
                throw new DataStoreException("Base resource sample dimensions are undefined");
            }
            empty = new double[sampleDimensions.size()];
            for (int i=0;i<empty.length;i++) {
                empty[i] = getEmptyValue(sampleDimensions.get(i));
            }

        } catch (DataStoreException ex) {
            throw ex;
        }
    }

    public void setInterpolation(InterpolationCase interpolation) {
        ArgumentChecks.ensureNonNull("interpolation", interpolation);
        this.interpolation = interpolation;
    }

    public InterpolationCase getInterpolation() {
        return interpolation;
    }

    /**
     * Indicate if coverage is homogeneous, which is most of the time true.
     * This allows the generator to start by generation the lowest level tiles
     * and generate upper one based on the previous.
     *
     * If the source coverage is something starter composed of various coverages
     * at different scales it is necessary to set this value to false.
     *
     * @return true if coverage is homogeneous.
     */
    public boolean isCoverageIsHomogeneous() {
        return coverageIsHomogeneous;
    }

    /**
     *
     * @param coverageIsHomogeneous
     */
    public void setCoverageIsHomogeneous(boolean coverageIsHomogeneous) {
        this.coverageIsHomogeneous = coverageIsHomogeneous;
    }

    public boolean isSkipExistingTiles() {
        return skipExistingTiles;
    }

    /**
     * Indicate if existing tiles should be regenerated.
     *
     * @param skipExistingTiles
     */
    public void setSkipExistingTiles(boolean skipExistingTiles) {
        this.skipExistingTiles = skipExistingTiles;
    }

    public void setFillValues(double[] fillValues) {
        this.fillValues = fillValues;
    }

    public double[] getFillValues() {
        return fillValues;
    }

    private static double getEmptyValue(SampleDimension dim){
        //dim = dim.forConvertedValues(true);
        double fillValue = Double.NaN;
        final double[] nodata = SampleDimensionUtils.getNoDataValues(dim);
        if (nodata!=null && nodata.length>0) {
            fillValue = nodata[0];
        }
        return fillValue;
    }

    @Override
    protected boolean isEmpty(Tile tileData) throws DataStoreException {
        ImageTile it = (ImageTile) tileData;
        try {
            final RenderedImage image = it.getImage();
            return BufferedImages.isAll(image, empty);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public void generate(Pyramid pyramid, Envelope env, NumberRange resolutions,
            ProcessListener listener) throws DataStoreException, InterruptedException {
        if (!coverageIsHomogeneous) {
            super.generate(pyramid, env, resolutions, listener);
            return;
        }

        /*
        We can generate the pyramid starting from the lowest level then going up
        using the previously generated level.
        */
        if (env != null) {
            try {
                env = Envelopes.transform(env, pyramid.getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        //generate lower level from data
        final Mosaic[] mosaics = pyramid.getMosaics().toArray(new Mosaic[0]);
        Arrays.sort(mosaics, (Mosaic o1, Mosaic o2) -> Double.compare(o1.getScale(), o2.getScale()));

        final long total = countTiles(pyramid, env, resolutions);
        final AtomicLong al = new AtomicLong();

        GridCoverageResource resource = this.resource;

        for (final Mosaic mosaic : mosaics) {
            if (resolutions == null || resolutions.contains(mosaic.getScale())) {

                final Rectangle rect = Pyramids.getTilesInEnvelope(mosaic, env);

                final long nbTile = ((long)rect.width) * ((long)rect.height);
                final long eventstep = Math.min(1000, Math.max(1, nbTile/100l));
                Stream<Tile> stream = LongStream.range(0, nbTile).parallel()
                        .mapToObj(new LongFunction<Tile>() {
                            @Override
                            public Tile apply(long value) {
                                final long x = rect.x + (value % rect.width);
                                final long y = rect.y + (value / rect.width);

                                Tile data = null;
                                try {
                                    if (skipExistingTiles && !mosaic.isMissing((int) x, (int) y)) {
                                        //tile already exist
                                        return null;
                                    }

                                    //do not regenerate existing tiles
                                    //if (!mosaic.isMissing((int)x, (int)y)) return;

                                    final Point coord = new Point((int)x, (int)y);
                                    try {
                                        data = generateTile(pyramid, mosaic, coord);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } finally {
                                    long v = al.incrementAndGet();
                                    if (listener != null & (v % eventstep == 0))  {
                                        listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+mosaic.getIdentifier()+" scale="+mosaic.getScale(), (float) (( ((double)v)/((double)total) )*100.0)  ));
                                    }
                                }
                                return data;
                            }
                        })
                        .filter(this::emptyFilter);

                Streams.batchExecute(stream, (Collection<Tile> t) -> {
                    try {
                        mosaic.writeTiles(t.stream(), null);
                    } catch (DataStoreException ex) {
                        ex.printStackTrace();
                    }
                }, 200);

                long v = al.get();
                if (listener != null) {
                    listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+mosaic.getIdentifier()+" scale="+mosaic.getScale(), (float) (( ((double)v)/((double)total) )*100.0)  ));
                }

                //modify context
                final DefaultPyramid pm = new DefaultPyramid(pyramid.getCoordinateReferenceSystem());
                pm.getMosaicsInternal().add(mosaic);
                final InMemoryPyramidResource r = new InMemoryPyramidResource(NamesExt.create("test"));
                r.setSampleDimensions(resource.getSampleDimensions());
                r.getModels().add(pm);

                resource = r;
            }
        }
    }

    @Override
    public Tile generateTile(Pyramid pyramid, Mosaic mosaic, Point tileCoord) throws DataStoreException {
        return generateTile(pyramid, mosaic, tileCoord, resource);
    }

    private Tile generateTile(Pyramid pyramid, Mosaic mosaic, Point tileCoord, GridCoverageResource resource) throws DataStoreException {
        final Dimension tileSize = mosaic.getTileSize();
        final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
        final LinearTransform gridToCrsNd = Pyramids.getTileGridToCRS(mosaic, tileCoord, PixelInCell.CELL_CENTER);
        final long[] high = new long[crs.getCoordinateSystem().getDimension()];
        high[0] = tileSize.width-1; //inclusive
        high[1] = tileSize.height-1; //inclusive
        final GridExtent extent = new GridExtent(null, null, high, true);
        final GridGeometry gridGeomNd = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrsNd, crs);

        GridCoverage coverage;
        try {
            coverage = resource.read(gridGeomNd);
        } catch (NoSuchDataException ex) {
            //create an empty tile
            final BufferedImage img = BufferedImages.createImage(tileSize.width, tileSize.height, empty.length, DataBuffer.TYPE_DOUBLE);
            final WritablePixelIterator ite = WritablePixelIterator.create(img);
            while (ite.next()) {
                ite.setPixel(fillValues == null ? empty : fillValues);
            }
            ite.close();
            return new DefaultImageTile(img, tileCoord);
        } catch (DataStoreException ex) {
            throw ex;
        }

        //at this point we should have a coverage 2D
        //if not, this means the source coverage has more dimensions then the pyramid
        //resample coverage to exact tile grid geometry
        try {
            GridCoverageProcessor processor = new GridCoverageProcessor();
            switch (interpolation) {
                case NEIGHBOR : processor.setInterpolation(Interpolation.NEAREST);
                case BILINEAR : processor.setInterpolation(Interpolation.BILINEAR);
                case LANCZOS : processor.setInterpolation(Interpolation.LANCZOS);
                default: processor.setInterpolation(Interpolation.BILINEAR);
            }
            coverage = processor.resample(coverage, gridGeomNd);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        RenderedImage image = coverage.render(null);
        ImageProcessor ip = new ImageProcessor();
        image = ip.prefetch(image, null);
        return new DefaultImageTile(image, tileCoord);
    }

}
