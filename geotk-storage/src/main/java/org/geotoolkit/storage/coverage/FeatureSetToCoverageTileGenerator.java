/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.feature.jts.JTS;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.opengis.feature.Feature;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureSetToCoverageTileGenerator extends AbstractTileGenerator {

    /**
     * Source FeatureSet for mask creation.
     */
    private FeatureSet featureSet;
    private boolean antiAliasing = false;
    private List<SampleDimension> outputSampleDimensions;
    private BiFunction<Feature, Integer, double[]> featureToSamples;
    private RenderedImage template;

    /**
     * Set the FeatureSet to extract coverage samples from.
     * @param featureSet not null.
     */
    public void setFeatureSet(FeatureSet featureSet) {
        ArgumentChecks.ensureNonNull("featureSet", featureSet);
        this.featureSet = featureSet;
    }

    public FeatureSet getFeatureSet() {
        return featureSet;
    }

    /**
     * Set to true to apply AntiAliasing on Geometry rasterisation.
     * @param aliasing true to enable AntiAliasing
     */
    public void setAntialiasing(boolean aliasing) {
        this.antiAliasing = aliasing;
    }

    public boolean isAntialiasing() {
        return antiAliasing;
    }

    /**
     * Set the resulting coverage sample dimensions.
     * @param sampleDimensions not null, must contains at least one entry.
     */
    public void setSampleDimensions(List<SampleDimension> sampleDimensions) {
        ArgumentChecks.ensureNonEmpty("sample dimensions", sampleDimensions);
        this.outputSampleDimensions = sampleDimensions;
    }

    public List<SampleDimension> getSampleDimensions() {
        return outputSampleDimensions;
    }

    /**
     * Optional function used to extract samples for each coverage band.
     * Function will be called with a null Feature when no features exist.
     * The Integer value ranges from 0 to 255 with intermediate values only if antialiasing is enabled.
     */
    public void setFeatureToSamples(BiFunction<Feature, Integer, double[]> featureToSamples) {
        this.featureToSamples = featureToSamples;
    }

    public BiFunction<Feature, Integer, double[]> getFeatureToSamples() {
        return featureToSamples;
    }

    /**
     * Set coverage image template with its SampleModel and ColorModel.
     * It is not guarantee that both or any of the models will be preserved, it is a best effort to do so.
     * If the template is null then default models will be used.
     * @param template can be null.
     */
    public void setTemplate(RenderedImage template) {
        this.template = template;
    }

    public RenderedImage getTemplate() {
        return template;
    }

    /**
     * <p>
     * If feature to pixel transform is defined the resulting image matches given transform template.
     * If the template was null then a Double type raster type will be created
     * <p>
     * If feature to pixel transform is undefined and antialiasing is enabled, an indexed gray
     * image will be produced, template is ignored.
     * Black as outside geometry, White as inside geometry, gray on aliased pixels.
     * 0 as outside geometry, 255 as inside geometry.
     * <p>
     * If feature to pixel transform is undefined and antialiasing is disabled, an
     * 2bit black/white image will be produced, template is ignored.
     * Black as outside geometry, White as inside geometry.
     * 0 as no outside geometry, 1 as inside geometry.
     *
     */
    public GridCoverage generate(GridGeometry gridGeometry) throws DataStoreException {
        if (featureSet == null) {
            throw new IllegalArgumentException("FeatureSet is undefined");
        }
        if (gridGeometry.getDimension() != 2) {
            throw new IllegalArgumentException("Only 2D GridGeometry supported");
        }

        //TODO : we should preserve the original gridgeometry translation
        gridGeometry = CoverageUtilities.forceLowerToZero(gridGeometry);

        final GridExtent extent = gridGeometry.getExtent();
        final int width = Math.toIntExact(extent.getSize(0));
        final int height = Math.toIntExact(extent.getSize(1));
        final Area paintArea = new Area(new Rectangle(0, 0, width, height));

        final PropertyType geomProperty = FeatureExt.getDefaultGeometry(featureSet.getType());
        // IMPORTANT: Get string representation of the geometry name outside of loop.
        // The reason is that AbstractName.toString() is synchronized, therefore it could hurt performance a lot.
        final String geometryName = geomProperty.getName().toString();
        final CoordinateReferenceSystem defaultGeomCrs = FeatureExt.getCRS(geomProperty);
        final FilterFactory ff = DefaultFilterFactory.forFeatures();

        //reduce feature set to wanted envelope
        final FeatureQuery query = new FeatureQuery();
        query.setSelection(ff.bbox(ff.property(geometryName), gridGeometry.getEnvelope()));
        final FeatureSet subset = featureSet.subset(query);
        final CoordinateReferenceSystem targetCrs = gridGeometry.getCoordinateReferenceSystem();


        //create the mask
        final BufferedImage mask = new BufferedImage(width, height, antiAliasing ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_BYTE_BINARY);
        final MathTransform crsToGrid;
        try {
            crsToGrid = gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER).inverse();
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        RenderedImage result;

        interface CheckedFunction<T, R> {R apply(T t) throws DataStoreException;}
        final CheckedFunction<Feature, Geometry> toGridGeometry = new CheckedFunction<Feature, Geometry>() {
            CoordinateReferenceSystem geomcrs = null;
            MathTransform geomCrsToGridCrs = null;
            MathTransform geomCrsToDisplay = null;
            @Override
            public Geometry apply(Feature feature) throws DataStoreException {
                Object geom = feature.getPropertyValue(geometryName);
                if (geom instanceof Geometry g) {
                    try {
                        CoordinateReferenceSystem cdtcrs = JTS.getCoordinateReferenceSystem(g);
                        if (cdtcrs == null) cdtcrs = defaultGeomCrs;
                        if (geomcrs != cdtcrs) {
                            geomcrs = cdtcrs;
                            geomCrsToGridCrs = CRS.findOperation(geomcrs, targetCrs, null).getMathTransform();
                            geomCrsToDisplay = MathTransforms.concatenate(geomCrsToGridCrs, crsToGrid);
                        }
                        g = JTS.transform(g, geomCrsToDisplay);
                    } catch (TransformException | FactoryException ex) {
                        throw new DataStoreException(ex);
                    }
                    return g;
                }
                return null;
            }
        };
        if (featureToSamples == null) {
            /*
            In this cas we can aggregate all geometries as a single one before painting.
            This approach is more efficient and avoid aliasing problem on geometry shared edges.
            */
            Geometry paintShape = JTS.fromAWT(new GeometryFactory(), paintArea, 1.0);
            Geometry geometry = null;
            try (Stream<Feature> stream = subset.features(false)) {
                final List<Geometry> all = new ArrayList<>();
                final Iterator<Feature> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    final Feature feature = iterator.next();
                    Geometry g = toGridGeometry.apply(feature);
                    if (g != null) {
                        g = paintShape.intersection(g);
                        g = DouglasPeuckerSimplifier.simplify(g, 0.5);
                        if (g.isEmpty()) continue;
                        all.add(g);
                    }
                }
                if (!all.isEmpty()) {
                    geometry = all.get(0).getFactory().createGeometryCollection(GeometryFactory.toGeometryArray(all));
                    geometry = geometry.buffer(0);
                }
            }
            if (geometry != null) {
                //fill the mask
                final Graphics2D graphics = mask.createGraphics();
                graphics.setColor(Color.WHITE);
                graphics.setStroke(new BasicStroke(1));
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
                graphics.fill(JTS.asShape(geometry));
                graphics.dispose();
            }

            result = mask;
        } else {
            /*
            In this case we must process each feature one by one.
            */
            if (outputSampleDimensions == null) {
                throw new IllegalArgumentException("Sample dimensions are undefined");
            }
            final BufferedImage image;
            if (template == null) {
                image = BufferedImages.createImage(width, height, outputSampleDimensions.size(), DataBuffer.TYPE_DOUBLE);
            } else {
                if (template.getSampleModel().getNumBands() != outputSampleDimensions.size()) {
                    throw new IllegalArgumentException("Template sample model and sample dimensions must have the same size");
                }
                image = BufferedImages.createImage(template, Math.toIntExact(extent.getSize(0)), Math.toIntExact(extent.getSize(1)), null, null);
            }

            double[] noData = featureToSamples.apply(null, 0);
            BufferedImages.setAll(image, noData);
            final Graphics2D graphics = mask.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.setStroke(new BasicStroke(1));
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

            try (final WritablePixelIterator writer = WritablePixelIterator.create(image)) {

                try (Stream<Feature> stream = subset.features(false)) {
                    final Iterator<Feature> iterator = stream.iterator();
                    while (iterator.hasNext()) {
                        final Feature feature = iterator.next();
                        Geometry g = toGridGeometry.apply(feature);
                        if (g != null) {
                            Area s = new Area(JTS.asShape(g));
                            s.intersect(paintArea);

                            graphics.fill(s);

                            //fill result image
                            Rectangle area = s.getBounds();
                            if (antiAliasing) {
                                area.x -=1;
                                area.y -=1;
                                area.width +=2;
                                area.height +=2;
                            }
                            area = area.intersection(new Rectangle(0, 0, width, height));
                            if (!area.isEmpty()) {
                                PixelIterator ite = new PixelIterator.Builder().setRegionOfInterest(area).create(mask);
                                int lastPixel = -1;
                                double[] samples = null;
                                while (ite.next()) {
                                    final int pixel = ite.getSample(0);
                                    if (pixel <= 0) continue;

                                    final Point position = ite.getPosition();
                                    if (pixel != lastPixel) {
                                        lastPixel = pixel;
                                        samples = featureToSamples.apply(feature, pixel);
                                    }
                                    writer.moveTo(position.x, position.y);
                                    writer.setPixel(samples);
                                }
                            }

                            //reset mask
                            BufferedImages.setAll(mask, new double[]{0});
                        }
                    }
                }
            }

            graphics.dispose();
            result = image;
        }

        //create result coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(gridGeometry);
        gcb.setValues(result);
        if (outputSampleDimensions != null) gcb.setRanges(outputSampleDimensions);
        return gcb.build();
    }

    @Override
    public Tile generateTile(WritableTileMatrixSet wtms, WritableTileMatrix matrix, long[] tileCoord) throws DataStoreException {
        final int[] tileSize = ((org.geotoolkit.storage.multires.TileMatrix)matrix).getTileSize();
        final GridGeometry gridGeomNd = matrix.getTilingScheme().derive().subgrid(new GridExtent(null, tileCoord, tileCoord, true)).build().upsample(tileSize);
        final GridCoverage coverage = generate(gridGeomNd);
        return new CoverageResourceTile(tileCoord, coverage);
    }

    @Override
    protected boolean isEmpty(Tile tile) throws DataStoreException {
        return false;
    }

}
