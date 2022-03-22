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
package org.geotoolkit.display2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.media.jai.RasterFactory;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.ext.dynamicrange.DynamicRangeSymbolizer;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.memory.InMemoryTiledGridCoverageResource;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.geotoolkit.storage.multires.DefaultTileMatrixSet;
import org.geotoolkit.storage.multires.ImageTileMatrix;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.geotoolkit.util.NamesExt;
import org.opengis.filter.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Displacement;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapContextTileGenerator extends AbstractTileGenerator {

    private CanvasDef canvasDef;
    private SceneDef sceneDef;
    private final double[] empty;
    private final List<SampleDimension> sampleDimensions = new ArrayList<>();

    public MapContextTileGenerator(MapLayers context, Hints hints) {
        this(new SceneDef(context, hints), new CanvasDef());
    }

    public MapContextTileGenerator(SceneDef sceneDef, CanvasDef canvasDef) {
        this.sceneDef = sceneDef;
        this.canvasDef = canvasDef;

        final Color backColor = canvasDef.getBackground();
        if (backColor != null && backColor.getAlpha() == 255
                && ((sceneDef.getHints() == null) || !sceneDef.getHints().containsKey(GO2Hints.KEY_COLOR_MODEL))) {
            //change hints color model to avoid generating images with alpha
            // Bug OpenJDK : issue with TYPE_INT_RGB, replace by TYPE_3BYTE_BGR
            final ColorModel noAlphaCm = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).getColorModel();

            Hints h = sceneDef.getHints();
            if (h == null) h = new Hints();
            h.put(GO2Hints.KEY_COLOR_MODEL, noAlphaCm);
            sceneDef.setHints(h);
        }

        //compute empty pixels samples
        ColorModel cm = null;
        if (sceneDef.getHints() != null) {
            cm = (ColorModel) sceneDef.getHints().get(GO2Hints.KEY_COLOR_MODEL);
        }
        if (cm == null) cm = ColorModel.getRGBdefault();
        final SampleModel sm = cm.createCompatibleSampleModel(1, 1);
        final WritableRaster raster = RasterFactory.createWritableRaster(sm, new Point(0, 0));
        final BufferedImage img = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), new Hashtable());
        if (backColor != null) {
            Graphics g = img.getGraphics();
            g.setColor(backColor);
            g.fillRect(0, 0, 1, 1);
        }
        final PixelIterator ite = PixelIterator.create(img);
        ite.moveTo(0, 0);
        empty = ite.getPixel((double[])null);

        for (int i=0, n=img.getSampleModel().getNumBands(); i<n; i++) {
            sampleDimensions.add(new SampleDimension.Builder().setName(i).build());
        }

    }

    @Override
    public Tile generateTile(WritableTileMatrixSet pyramid, WritableTileMatrix mosaic, long[] tileCoord) throws DataStoreException {
        final LinearTransform tileGridToCrs = TileMatrices.getTileGridToCRS(mosaic, tileCoord, PixelInCell.CELL_CENTER);
        final Dimension tileSize = ((ImageTileMatrix) mosaic).getTileSize();
        final GridGeometry gridGeom = new GridGeometry(
                new GridExtent(tileSize.width, tileSize.height),
                PixelInCell.CELL_CENTER,
                tileGridToCrs, mosaic.getTilingScheme().getCoordinateReferenceSystem());

        final CanvasDef canvas = new CanvasDef();
        canvas.setGridGeometry(gridGeom);
        canvas.setBackground(canvasDef.getBackground());
        try {
            final BufferedImage image = DefaultPortrayalService.portray(canvas, sceneDef);
            return new DefaultImageTile(image, tileCoord);
        } catch (PortrayalException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    private static Double doubleValue(final Expression e) {
        final Number v = (Number) e.apply(null);
        return (v != null) ? v.doubleValue() : null;
    }

    @Override
    public void generate(WritableTileMatrixSet tileMatrixSet, Envelope env, NumberRange resolutions,
            ProcessListener listener) throws DataStoreException, InterruptedException {

        //check if we can optimize tiles generation
        boolean rasterOptimisation = true;

        search:
        for (MapLayer layer : MapBuilder.getLayers(sceneDef.getContext())) {
            final Style style = layer.getStyle();
            for (FeatureTypeStyle fts : style.featureTypeStyles()) {
                for (Rule rule : fts.rules()) {
                    double scaleMin = rule.getMinScaleDenominator();
                    double scaleMax = rule.getMaxScaleDenominator();
                    //TODO more accurate test for each mosaic
                    //TODO convert mosaic scale to symbology encoding scale
                    // CanvasUtilities.computeSEScale
                    if (scaleMin != 0.0 || scaleMax < 5.0E9) {
                        rasterOptimisation = false;
                        break search;
                    }

                    for (Symbolizer symbolizer : rule.symbolizers()) {
                        if (symbolizer instanceof RasterSymbolizer ||
                            symbolizer instanceof DynamicRangeSymbolizer) {
                            //ok
                        } else if (symbolizer instanceof PolygonSymbolizer) {
                            PolygonSymbolizer ps = (PolygonSymbolizer) symbolizer;

                            //check if we have a plain fill
                            Displacement displacement = ps.getDisplacement();
                            Fill fill = ps.getFill();
                            Stroke stroke = ps.getStroke();
                            Expression perpendicularOffset = ps.getPerpendicularOffset();

                            if (displacement != null) {
                                Double dx = doubleValue(displacement.getDisplacementX());
                                Double dy = doubleValue(displacement.getDisplacementX());
                                if ( (dx != null && dx != 0.0) || (dy != null && dy != 0.0)) {
                                    rasterOptimisation = false;
                                    break search;
                                }
                            }
                            if (perpendicularOffset != null) {
                                Double off = doubleValue(perpendicularOffset);
                                if (off != null && off != 0.0) {
                                    rasterOptimisation = false;
                                    break search;
                                }
                            }
                            if (stroke != null) {
                                Double op = doubleValue(stroke.getOpacity());
                                Double wd = doubleValue(stroke.getWidth());
                                if ( (op == null || op == 0.0) || (wd == null || wd == 0.0)) {
                                    //not visible
                                } else {
                                    rasterOptimisation = false;
                                    break search;
                                }
                            }
                            if (fill != null) {
                                if (fill.getGraphicFill() != null) {
                                    rasterOptimisation = false;
                                    break search;
                                }
                            }
                        } else {
                            rasterOptimisation = false;
                            break search;
                        }
                    }
                }
            }
        }

        if (rasterOptimisation) {
            /*
            We can generate the pyramid starting from the lowest level then going up
            using the previously generated level.
            */
            if (env != null) {
                try {
                    CoordinateReferenceSystem targetCrs = tileMatrixSet.getCoordinateReferenceSystem();
                    Envelope baseEnv = env;
                    env = Envelopes.transform(env, targetCrs);
                    double[] minres = new double[]{resolutions.getMinDouble(), resolutions.getMinDouble()};
                    double[] maxres = new double[]{resolutions.getMaxDouble(), resolutions.getMaxDouble()};
                    minres = ReferencingUtilities.convertResolution(baseEnv, minres, targetCrs, null);
                    maxres = ReferencingUtilities.convertResolution(baseEnv, maxres, targetCrs, null);
                    resolutions = NumberRange.create(minres[0], true, maxres[0], true);
                } catch (TransformException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            //generate lower level from data
            final WritableTileMatrix[] tileMatrices = tileMatrixSet.getTileMatrices().values().toArray(new WritableTileMatrix[0]);
            Arrays.sort(tileMatrices, TileMatrices.SCALE_COMPARATOR);

            MapLayers parent = sceneDef.getContext();
            Hints hints = sceneDef.getHints();

            final long total = TileMatrices.countTiles(tileMatrixSet, env, resolutions);
            final AtomicLong al = new AtomicLong();
            //send an event only every few seconds
            final AtomicLong tempo = new AtomicLong(System.currentTimeMillis());
            final String msg = " / "+ NumberFormat.getIntegerInstance(Locale.FRANCE).format(total);

            for (final WritableTileMatrix tileMatrix : tileMatrices) {
                if (resolutions == null || resolutions.contains(TileMatrices.getScale(tileMatrix))) {

                    final Rectangle rect;
                    try {
                        GridExtent area = TileMatrices.getTilesInEnvelope(tileMatrix, env);
                        rect = new Rectangle(
                            (int) area.getLow(0),
                            (int) area.getLow(1),
                            (int) area.getSize(0),
                            (int) area.getSize(1));
                    } catch (NoSuchDataException ex) {
                        continue;
                    }

                    final CanvasDef canvasDef = new CanvasDef();
                    canvasDef.setBackground(this.canvasDef.getBackground());
                    canvasDef.setEnvelope(tileMatrix.getTilingScheme().getEnvelope());
                    final SceneDef sceneDef = new SceneDef(parent, hints);


                    //one thread per line, the progressive image generates multiple tiles when drawing
                    //this approach is more efficient from profiling result then using tile by tile
                    //generation
                    Stream<Tile> stream = LongStream.range(rect.y, rect.y+rect.height).parallel().boxed().flatMap((Long y) -> {
                        try {
                            final ProgressiveImage img = new ProgressiveImage(canvasDef, sceneDef,
                                    tileMatrix.getTilingScheme(), tileMatrix.getTileSize(), 0);
                            return img.generate(rect.x, rect.x+rect.width, y.intValue(), skipEmptyTiles);
                        } catch (Exception ex) {
                            LOGGER.log(Level.INFO, "Failed to generate a tile {0}", ex.getMessage());
                            LOGGER.log(Level.FINER, "Failed to generate a tile ", ex);
                            return Stream.empty();
                        }
                    });
                    if (listener != null) {
                        final NumberFormat format = NumberFormat.getIntegerInstance(Locale.FRANCE);
                        stream = stream.map((Tile t) -> {
                            long n = al.incrementAndGet();
                            if (n % 1000 == 0) {
                                final long time = System.currentTimeMillis();
                                if (tempo.updateAndGet((long operand) -> ((time-operand)  > 3000) ? time : operand) == time) {
                                    listener.progressing(new ProcessEvent(DUMMY, format.format(n)+msg, (float) (( ((double)n)/((double)total) )*100.0)  ));
                                }
                            }
                            return t;
                        });
                    }
                    tileMatrix.writeTiles(stream);

                    //last level event
                    final NumberFormat format = NumberFormat.getIntegerInstance(Locale.FRANCE);
                    long v = al.get();
                    if (listener != null) {
                        listener.progressing(new ProcessEvent(DUMMY, format.format(v)+msg, (float) (( ((double)v)/((double)total) )*100.0)  ));
                    }

                    //modify context
                    final DefaultTileMatrixSet.Writable pm = new DefaultTileMatrixSet.Writable(tileMatrixSet.getCoordinateReferenceSystem()){};
                    pm.getMosaicsInternal().insertByScale(tileMatrix);
                    final InMemoryTiledGridCoverageResource r = new InMemoryTiledGridCoverageResource(NamesExt.create("test"));
                    r.setSampleDimensions(sampleDimensions);
                    r.getTileMatrixSets().add(pm);

                    final MapLayers mc = MapBuilder.createContext();
                    mc.getComponents().add(MapBuilder.createLayer(r));
                    parent = mc;
                    hints = new Hints(hints);
                    hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                }
            }
        } else {
            super.generate(tileMatrixSet, env, resolutions, listener);
        }
    }

    @Override
    protected boolean isEmpty(Tile tile) throws DataStoreException {
        try {
            RenderedImage img = ((ImageTile) tile).getImage();
            return BufferedImages.isAll(img, empty);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }
    }

}
