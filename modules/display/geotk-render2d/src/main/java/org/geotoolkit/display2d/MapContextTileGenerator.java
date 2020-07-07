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
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.media.jai.RasterFactory;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.ext.dynamicrange.DynamicRangeSymbolizer;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.memory.InMemoryPyramidResource;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.geotoolkit.storage.multires.DefaultTileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.NamesExt;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Displacement;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrix;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapContextTileGenerator extends AbstractTileGenerator {

    private CanvasDef canvasDef;
    private SceneDef sceneDef;
    private final double[] empty;
    private final List<SampleDimension> sampleDimensions = new ArrayList<>();

    public MapContextTileGenerator(MapContext context, Hints hints) {
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
    public Tile generateTile(TileMatrixSet pyramid, TileMatrix mosaic, Point tileCoord) throws DataStoreException {
        final LinearTransform tileGridToCrs = TileMatrices.getTileGridToCRS(mosaic, tileCoord, PixelInCell.CELL_CENTER);
        final Dimension tileSize = mosaic.getTileSize();
        final GridGeometry gridGeom = new GridGeometry(
                new GridExtent(tileSize.width, tileSize.height),
                PixelInCell.CELL_CENTER,
                tileGridToCrs, mosaic.getUpperLeftCorner().getCoordinateReferenceSystem());

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

    @Override
    public void generate(TileMatrixSet pyramid, Envelope env, NumberRange resolutions,
            ProcessListener listener) throws DataStoreException, InterruptedException {

        //check if we can optimize tiles generation
        boolean rasterOptimisation = true;

        search:
        for (MapLayer layer : sceneDef.getContext().layers()) {
            final Resource resource = layer.getResource();
            final MutableStyle style = layer.getStyle();
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
                                Double dx = displacement.getDisplacementX().evaluate(null, Double.class);
                                Double dy = displacement.getDisplacementX().evaluate(null, Double.class);
                                if ( (dx != null && dx != 0.0) || (dy != null && dy != 0.0)) {
                                    rasterOptimisation = false;
                                    break search;
                                }
                            }
                            if (perpendicularOffset != null) {
                                Double off = perpendicularOffset.evaluate(null, Double.class);
                                if (off != null && off != 0.0) {
                                    rasterOptimisation = false;
                                    break search;
                                }
                            }
                            if (stroke != null) {
                                Double op = stroke.getOpacity().evaluate(null, Double.class);
                                Double wd = stroke.getWidth().evaluate(null, Double.class);
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
                    env = Envelopes.transform(env, pyramid.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            //generate lower level from data
            final TileMatrix[] mosaics = pyramid.getTileMatrices().toArray(new TileMatrix[0]);
            Arrays.sort(mosaics, (TileMatrix o1, TileMatrix o2) -> Double.compare(o1.getScale(), o2.getScale()));

            MapContext parent = sceneDef.getContext();
            Hints hints = sceneDef.getHints();

            final long total = countTiles(pyramid, env, resolutions);
            final AtomicLong al = new AtomicLong();
            //send an event only every few seconds
            final AtomicLong tempo = new AtomicLong(System.currentTimeMillis());
            final String msg = " / "+ NumberFormat.getIntegerInstance(Locale.FRANCE).format(total);

            for (final TileMatrix mosaic : mosaics) {
                if (resolutions == null || resolutions.contains(mosaic.getScale())) {

                    final Rectangle rect = TileMatrices.getTilesInEnvelope(mosaic, env);

                    final CanvasDef canvasDef = new CanvasDef();
                    canvasDef.setBackground(this.canvasDef.getBackground());
                    canvasDef.setEnvelope(mosaic.getEnvelope());
                    final SceneDef sceneDef = new SceneDef(parent, hints);

                    //one thread per line, the progressive image generates multiple tiles when drawing
                    //this approach is more efficient from profiling result then using tile by tile
                    //generation
                    LongStream.range(rect.y, rect.y+rect.height).parallel().forEach(new LongConsumer() {
                        @Override
                        public void accept(final long y) {
                            final long[] nb = new long[2];
                            final NumberFormat format = NumberFormat.getIntegerInstance(Locale.FRANCE);
                            try {
                                final ProgressiveImage img = new ProgressiveImage(canvasDef, sceneDef,
                                    mosaic.getGridSize(), mosaic.getTileSize(), mosaic.getScale(), 0);

                                Stream<Tile> stream = img.generate(rect.x, rect.x+rect.width, (int)y, skipEmptyTiles);

                                if (listener != null) {
                                    stream = stream.map(new Function<Tile, Tile>() {
                                        @Override
                                        public Tile apply(Tile t) {
                                            nb[0]++;
                                            if (nb[0]%1000 == 0) {
                                                final long time = System.currentTimeMillis();
                                                if (tempo.updateAndGet((long operand) -> ((time-operand)  > 3000) ? time : operand) == time) {
                                                    long v = al.addAndGet(nb[0]);
                                                    nb[1] += nb[0];
                                                    nb[0] = 0;
                                                    listener.progressing(new ProcessEvent(DUMMY, format.format(v)+msg, (float) (( ((double)v)/((double)total) )*100.0)  ));
                                                }
                                            }
                                            return t;
                                        }
                                    });
                                }
                                mosaic.writeTiles(stream, null);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            al.addAndGet(nb[0]);
                            nb[1] += nb[0];
                            nb[0] = 0;
                            long v = al.addAndGet(rect.width - nb[1]); //empty tiles
                            if (listener != null) {
                                final long time = System.currentTimeMillis();
                                if (tempo.updateAndGet((long operand) -> ((time-operand)  > 3000) ? time : operand) == time) {
                                    listener.progressing(new ProcessEvent(DUMMY, format.format(v)+msg, (float) (( ((double)v)/((double)total) )*100.0)  ));
                                }
                            }
                        }
                    });

                    //last level event
                    final NumberFormat format = NumberFormat.getIntegerInstance(Locale.FRANCE);
                    long v = al.get();
                    if (listener != null) {
                        listener.progressing(new ProcessEvent(DUMMY, format.format(v)+msg, (float) (( ((double)v)/((double)total) )*100.0)  ));
                    }

                    //modify context
                    final DefaultTileMatrixSet pm = new DefaultTileMatrixSet(pyramid.getCoordinateReferenceSystem());
                    pm.getMosaicsInternal().add(mosaic);
                    final InMemoryPyramidResource r = new InMemoryPyramidResource(NamesExt.create("test"));
                    r.setSampleDimensions(sampleDimensions);
                    r.getModels().add(pm);

                    final MapContext mc = MapBuilder.createContext();
                    mc.layers().add(MapBuilder.createCoverageLayer(r));
                    parent = mc;
                    hints = new Hints(hints);
                    hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                }
            }
        } else {
            super.generate(pyramid, env, resolutions, listener);
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
