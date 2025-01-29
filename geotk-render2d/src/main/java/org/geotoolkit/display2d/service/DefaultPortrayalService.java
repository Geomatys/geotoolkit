/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display2d.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.map.ExceptionPresentation;
import org.apache.sis.map.Presentation;
import org.apache.sis.map.coverage.RenderingWorkaround;
import org.apache.sis.util.privy.UnmodifiableArrayList;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.map.service.GraphicsPortrayer;
import org.apache.sis.map.service.RenderingException;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;

import static org.geotoolkit.display2d.GO2Utilities.getCached;
import static org.geotoolkit.display2d.GO2Utilities.mergeColors;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.canvas.J2DCanvasSVG;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.RenderingRules;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.RenderingRoutines;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.bandselect.BandSelectProcess;
import org.geotoolkit.storage.coverage.BandedCoverageResource;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.style.portrayal.PortrayalService;
import org.opengis.util.FactoryException;

/**
 * Default implementation of portrayal service.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class DefaultPortrayalService implements PortrayalService{

    /**
     * Cache the last CoverageWriter.
     */
    private static final AtomicReference<ImageCoverageWriter> WRITER_CACHE = new AtomicReference<>();

    /**
     * List available presentation writers.
     */
    private static final Map<String, PresentationWriter> PRESENTATION_FORMATS = new HashMap<>();

    /**
     * List of supported image mime-types and formats.
     */
    private static final Set<String> IMAGEIO_FORMATS = new HashSet<String>();
    static {
        IMAGEIO_FORMATS.addAll(Arrays.asList(ImageIO.getReaderMIMETypes()));
        IMAGEIO_FORMATS.addAll(Arrays.asList(ImageIO.getReaderFormatNames()));
        IMAGEIO_FORMATS.addAll(Arrays.asList(ImageIO.getWriterMIMETypes()));
        IMAGEIO_FORMATS.addAll(Arrays.asList(ImageIO.getWriterFormatNames()));

        final Iterator<PresentationWriter> iterator = ServiceLoader.load(PresentationWriter.class).iterator();
        while (iterator.hasNext()) {
            final PresentationWriter writer = iterator.next();
            PRESENTATION_FORMATS.put(writer.getMimeType(), writer);
        }
    }

    /**
     * Cache the link between mime-type -> java-type
     * exemple : image/png -> png
     */
    static final Map<String,String> MIME_CACHE = new ConcurrentHashMap<>();

    private DefaultPortrayalService(){}

    ////////////////////////////////////////////////////////////////////////////
    // PAINTING IN A RENDERED IMAGE ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static RenderedImage portray(final CanvasDef canvasDef, final SceneDef sceneDef) throws PortrayalException{

        final Hints hints = sceneDef.getHints();
        ColorModel cm = (ColorModel) hints.get(GO2Hints.KEY_COLOR_MODEL);

        final GridGeometry grid = canvasDef.getOrCreateGridGeometry();
        final CanvasMonitor monitor = canvasDef.getMonitor();
        final Color background = canvasDef.getBackground();
        if (cm == null && background != null && background.getAlpha() == 255) {
            //opaque background, we can remove the alpha channel
            cm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getColorModel();
        }

        final GridExtent extent = grid.getExtent();
        final int width = Math.toIntExact(extent.getSize(0));
        final int height = Math.toIntExact(extent.getSize(1));
        final BufferedImage image;
        if (cm != null) {
            image = new BufferedImage(cm,
                    cm.createCompatibleWritableRaster(width, height),
                    cm.isAlphaPremultiplied(), null);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        final Graphics2D g = image.createGraphics();
        if (sceneDef.getHints() != null) {
            g.setRenderingHints(sceneDef.getHints());
        }
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, width, height);
        }

        final GraphicsPortrayer portrayer = new GraphicsPortrayer();
        portrayer.setCanvas(g);
        portrayer.setDomain(CoverageUtilities.forceLowerToZero(grid));
        try {
            portrayer.portray(sceneDef.getContext());
        } catch (RenderingException ex) {
            throw new PortrayalException(ex);
        }
        return image;
    }

    public static void prepareCanvas(final J2DCanvas canvas, final CanvasDef canvasDef, final SceneDef sceneDef) throws PortrayalException{

        final ContextContainer2D renderer = new ContextContainer2D(canvas);
        canvas.setContainer(renderer);

        final Color bgColor = canvasDef.getBackground();
        if (bgColor != null) {
            canvas.setBackgroundPainter(new SolidColorPainter(bgColor));
        }

        final CanvasMonitor monitor = canvasDef.getMonitor();
        if (monitor != null) {
            canvas.setMonitor(monitor);
        }

        final Hints hints = sceneDef.getHints();
        if (hints != null) {
            for (Entry<?,?> entry : hints.entrySet()) {
                canvas.setRenderingHint((Key) entry.getKey(), entry.getValue());
            }
        }

        final MapLayers context = sceneDef.getContext();
        renderer.setContext(context);

        GridGeometry gridGeometry = canvasDef.getGridGeometry();
        if (gridGeometry != null) {
            try {
                canvas.setGridGeometry(gridGeometry);
            } catch (FactoryException ex) {
                throw new PortrayalException("Could not set objective crs",ex);
            }
        } else {
            final Envelope contextEnv = canvasDef.getEnvelope();
            final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();
            try {
                canvas.setObjectiveCRS(crs);
            } catch (TransformException | FactoryException ex) {
                throw new PortrayalException("Could not set objective crs",ex);
            }

            //we specifically say to not repect X/Y proportions
            canvas.setAxisProportions(!canvasDef.isStretchImage());
            // Note : that's a ugly hack to prevent weird override of gridgeometry caused by following method chain:
            // setVisibleArea -> setAxisRange -> setRange.
            if (contextEnv != null) {
                try {
                    canvas.setGridGeometry(canvasDef.getOrCreateGridGeometry());
                } catch (Exception e) {
                    // Rollback to previous behavior
                    try {
                        canvas.setVisibleArea(contextEnv);
                        if (canvasDef.getAzimuth() != 0) {
                            canvas.rotate( -Math.toRadians(canvasDef.getAzimuth()) );
                        }
                    } catch (NoninvertibleTransformException | TransformException ex) {
                        ex.addSuppressed(e);
                        throw new PortrayalException(ex);
                    }
                }
            }
        }

        //paints all extensions
        final List<PortrayalExtension> extensions = sceneDef.extensions();
        if (extensions != null) {
            for (final PortrayalExtension extension : extensions) {
                if (extension != null) extension.completeCanvas(canvas);
            }
        }

    }

    /**
     * Create a rendered image which tile model maps the given definition.
     * The image will be divided in the same number of tiles and size as the mosaic.
     * Unlike a call to a portray method, the returned rendered image will be calculated
     * progressively.
     *
     * @return RenderedImage , never null
     */
    public static RenderedImage prepareImage(final CanvasDef canvasDef, final SceneDef sceneDef,
            final Dimension gridSize, final Dimension tileSize, final double scale) throws PortrayalException{
        return new PortrayalRenderedImage(canvasDef, sceneDef, gridSize, tileSize, scale);
    }

    /**
     * Manipulate a MapContext as if it was an ARGB coverage of infinite resolution.
     *
     * @return GridCoverageReader, never null
     */
    public static GridCoverageResource asResource(final SceneDef sceneDef){
        return new PortrayalCoverageResource(sceneDef);
    }

    ////////////////////////////////////////////////////////////////////////////
    // PAINTING IN A STREAM or OUTPUT //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param outputDef : The compression parameter will not necesarly be used
     *              if the mime type write can not support it.
     * @throws PortrayalException
     */
    public static void portray(final CanvasDef canvasDef, final SceneDef sceneDef,
            final OutputDef outputDef) throws PortrayalException{

        final String mime = outputDef.getMime();
        if (mime.contains("jpeg") || mime.contains("jpg")) {
            //special case for jpeg format, the writer generate incorrect colors
            //if he find out an alpha channel, so we ensure to have a opaque background
            //which will result in at least an RGB palette
            final Color bgColor = canvasDef.getBackground();
            if (bgColor == null) {
                //we set the background white
                canvasDef.setBackground(Color.WHITE);
            } else {
                //we merge colors
                canvasDef.setBackground(mergeColors(Color.WHITE, bgColor));
            }
        }

        //directly return false if hints doesnt contain the coverage writer hint enabled
        final Hints hints = sceneDef.getHints();
        final Object val = (hints != null) ? hints.get(GO2Hints.KEY_COVERAGE_WRITER) : null;
        final boolean useCoverageWriter = GO2Hints.COVERAGE_WRITER_ON.equals(val);

        if (useCoverageWriter && portrayAsCoverage(canvasDef, sceneDef, outputDef)) {
            //we succeeded in writing it with coverage writer directly.
            return;
        }

        final PresentationWriter presWriter = PRESENTATION_FORMATS.get(mime);

        if (presWriter != null) {

            boolean close = false;
            OutputStream outStream = null;
            try {
                if (outputDef.getOutput() instanceof OutputStream) {
                    outStream = (OutputStream) outputDef.getOutput();
                } else {
                    outStream = IOUtilities.openWrite(outputDef.getOutput());
                    close = true;
                }
                final Stream<Presentation> stream = present(canvasDef, sceneDef);
                presWriter.write(canvasDef, sceneDef, stream, outStream);

            } catch (IOException ex) {
                throw new PortrayalException(ex.getMessage(), ex);
            } finally {
                if(outStream!=null && close){
                    try {
                        outStream.close();
                    } catch (IOException ex) {
                        throw new PortrayalException(ex.getMessage(), ex);
                    }
                }
            }

        } else if ("image/svg+xml".equalsIgnoreCase(mime)) {
            //special canvas for svg
            final Envelope contextEnv = canvasDef.getEnvelope();
            final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();
            final J2DCanvasSVG canvas = new J2DCanvasSVG(
                    crs,
                    canvasDef.getDimension(),
                    sceneDef.getHints());
            prepareCanvas(canvas, canvasDef, sceneDef);
            canvas.repaint();

            boolean close = false;
            OutputStream outStream = null;
            try {
                if (outputDef.getOutput() instanceof OutputStream) {
                    outStream = (OutputStream) outputDef.getOutput();
                } else {
                    outStream = IOUtilities.openWrite(outputDef.getOutput());
                    close = true;
                }
                final Writer out = new OutputStreamWriter(outStream,"UTF-8");
                canvas.getDocument(out);
            } catch (IOException ex) {
                throw new PortrayalException(ex.getMessage(), ex);
            } finally{
                if(outStream!=null && close){
                    try {
                        outStream.close();
                    } catch (IOException ex) {
                        throw new PortrayalException(ex.getMessage(), ex);
                    }
                }
            }

        } else {
            //use the rendering engine to generate an image
            RenderedImage image = portray(canvasDef,sceneDef);

            if (image == null) {
                throw new PortrayalException("No image created by the canvas.");
            }

            if (useCoverageWriter) {
                final Envelope env = canvasDef.getEnvelope();
                final Dimension dim = canvasDef.getDimension();
                final double[] resolution = new double[]{
                        env.getSpan(0) / (double) dim.width,
                        env.getSpan(1) / (double) dim.height};

                //check the image color model
                image = rectifyImageColorModel(image, mime);

                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setDomain(env);
                gcb.setValues(image);
                final GridCoverage coverage = gcb.build();
                writeCoverage(coverage, env, resolution, outputDef,null);
            } else {
                try {
                    //image color model check is done in the writeImage method
                    writeImage(image, outputDef);
                } catch (IOException ex) {
                    throw new PortrayalException(ex);
                }
            }
        }

    }

    /**
     * Detect single raster layers with a default raster style and no special parameters
     * if so we can directly use the grid coverage writer and avoid the rendering chain.
     * It significantly reduce memory usage (minus the buffered image size and graphic objects)
     * and time (minus ~35%).
     *
     * @return true if the optimization have been applied.
     * @throws PortrayalException
     */
    private static boolean portrayAsCoverage(final CanvasDef canvasDef, final SceneDef sceneDef,
            final OutputDef outputDef) throws PortrayalException {

        //works for one layer only
        final List<MapLayer> layers = MapBuilder.getLayers(sceneDef.getContext());
        if(layers.size() != 1) return false;

        //layer must be a coverage
        final MapLayer layer = layers.get(0);
        final Resource resource = layer.getData();
        if(!(resource instanceof GridCoverageResource)) return false;

        //we must not have extensions
        if(!sceneDef.extensions().isEmpty()) return false;

        //style must be a default raster style = native original style
        final List<? extends FeatureTypeStyle> ftss = ((Style) layer.getStyle()).featureTypeStyles();
        if(ftss.size() != 1) return false;
        final List<? extends Rule> rules = ftss.get(0).rules();
        if(rules.size() != 1) return false;
        final List<? extends Symbolizer> symbols = rules.get(0).symbolizers();
        if(symbols.size() != 1) return false;
        final Symbolizer s = symbols.get(0);
        if(!GO2Utilities.isDefaultRasterSymbolizer(s)) return false;

        //we can bypass the renderer
        try {
            final GridCoverageResource ref = (GridCoverageResource) resource;
            final String mime = outputDef.getMime();
            final Envelope env = canvasDef.getEnvelope();
            final Dimension dim = canvasDef.getDimension();
            final double[] resolution = new double[]{
                    env.getSpan(0) / (double)dim.width,
                    env.getSpan(1) / (double)dim.height};

            GridGeometry query = ref.getGridGeometry().derive().subgrid(env, resolution).sliceByRatio(0.5, 0, 1).build();

            GridCoverage coverage = ref.read(query);
            final RenderedImage image = coverage.render(null);

            // HACK TO FIX COLOR ERROR ON JPEG /////////////////////////////////
            if(mime.contains("jpeg") || mime.contains("jpg")){
                if(image.getColorModel().hasAlpha()){
                    final int nbBands = image.getSampleModel().getNumBands();
                    if(nbBands > 3){
                        //we can remove the fourth band assuming it is the alpha
                        coverage = new BandSelectProcess(coverage, new int[]{0,1,2}).executeNow();
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////

            writeCoverage(coverage, env, resolution, outputDef, canvasDef.getBackground());
        } catch (DisjointExtentException ex) {
            return false;
        } catch (DataStoreException | ProcessException ex) {
            throw new PortrayalException(ex);
        }
        return true;
    }

    /**
     * Write a coverage using the canvas, view and output definition.
     *
     * @param coverage : coverage to write
     * @param canvasDef : canvas definition
     * @param viewDef : view definition
     * @param outputDef : outpout definition
     * @throws PortrayalException if writing failed
     */
    private static void writeCoverage(final GridCoverage coverage, final Envelope env, final double[] resolution,
            final OutputDef outputDef, final Color backgroundColor) throws PortrayalException{
        final String mimeType = outputDef.getMime();

        String javaType = MIME_CACHE.get(mimeType);
        if(javaType == null){
            //search the mime type
            final String[] candidates = XImageIO.getFormatNamesByMimeType(mimeType, false, true);
            if(candidates.length > 0){
                javaType = candidates[0];
                //cache the resulting java type
                MIME_CACHE.put(mimeType, javaType);
            }
        }

        if(javaType == null){
            //no related java type, incorrect mime type
            throw new PortrayalException("No java type found for mime type : " + mimeType);
        }


        //get a writer
        ImageCoverageWriter writer = WRITER_CACHE.getAndSet(null);
        if(writer == null){
            writer = new ImageCoverageWriter();
        }

        try {
            final GridCoverageWriteParam writeParam = new GridCoverageWriteParam();
            writeParam.setEnvelope(env);
            writeParam.setResolution(resolution);
            writeParam.setFormatName(javaType);
            writeParam.setCompressionQuality(outputDef.getCompression());

            if (backgroundColor != null) {
                final int r = backgroundColor.getRed();
                final int g = backgroundColor.getGreen();
                final int b = backgroundColor.getBlue();
                final int a = backgroundColor.getAlpha();
                writeParam.setBackgroundValues(new double[]{r,g,b,a});
            }

            writer.setOutput(outputDef.getOutput());
            writer.write(coverage, writeParam);

        } catch (DataStoreException ex) {
            throw new PortrayalException(ex);
        } finally {
            try {
                writer.reset();
                if(!WRITER_CACHE.compareAndSet(null, writer)){
                    try {
                        writer.dispose();
                    } catch (DataStoreException ex) {
                        throw new PortrayalException(ex);
                    }
                }
            } catch (DataStoreException ex) {
                //the writer has problems, we better not put in back in the cache.
                try {
                    writer.dispose();
                } catch (DataStoreException ex1) {
                    Logger.getLogger("org.geotoolkit.display2d.service").log(Level.WARNING, null, ex1);
                }
                throw new PortrayalException(ex);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // VISITING A CONTEXT //////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static void visit(final CanvasDef canvasDef, final SceneDef sceneDef, final VisitDef visitDef)
            throws PortrayalException {

        final Envelope contextEnv = canvasDef.getEnvelope();
        final Dimension canvasDimension = canvasDef.getDimension();
        final Hints hints = sceneDef.getHints();
        final MapLayers context = sceneDef.getContext();
        final boolean strechImage = canvasDef.isStretchImage();

        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(contextEnv.getCoordinateReferenceSystem(),canvasDimension,hints);
        final ContextContainer2D renderer = new ContextContainer2D(canvas);
        canvas.setContainer(renderer);

        renderer.setContext(context);
        try {
            canvas.setObjectiveCRS(contextEnv.getCoordinateReferenceSystem());
        } catch (TransformException | FactoryException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        canvas.setAxisProportions(!strechImage);
        try {
            canvas.setVisibleArea(contextEnv);
        } catch (NoninvertibleTransformException | TransformException ex) {
            throw new PortrayalException(ex);
        }

        final Shape selectedArea = visitDef.getArea();
        final GraphicVisitor visitor = visitDef.getVisitor();

        try {
            canvas.getGraphicsIn(selectedArea, visitor);
        } catch (Exception ex) {
            if (ex instanceof PortrayalException) {
                throw (PortrayalException)ex;
            } else {
                throw new PortrayalException(ex);
            }
        } finally {
            visitor.endVisit();
            canvas.clearCache();
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    // PRESENTING A CONTEXT ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Generate presentation objects for a scene.
     * @param canvasDef
     * @param sceneDef
     * @return stream of Presentation instance.
     */
    public static Stream<Presentation> present(final CanvasDef canvasDef, final SceneDef sceneDef) throws PortrayalException {

        Stream<Presentation> stream = Stream.empty();

        final Envelope contextEnv = canvasDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();
        final Dimension dim = canvasDef.getDimension();
        final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);

        final J2DCanvasBuffered canvas = new J2DCanvasBuffered(crs,
                canvasDef.getDimension(),sceneDef.getHints());
        prepareCanvas(canvas, canvasDef, sceneDef);
        final RenderingContext2D renderContext = canvas.prepareContext(img.createGraphics());

        final MapLayers context = sceneDef.getContext();
        final List<MapLayer> layers = MapBuilder.getLayers(context);
        for (MapLayer layer : layers) {
            if (!layer.isVisible()) continue;

            final Resource resource = layer.getData();
            stream = Stream.concat(stream, present(layer, resource, renderContext));
        }

        return stream;
    }

    public static Stream<Presentation> present(MapLayer layer, Resource resource, RenderingContext2D renderContext) {

        final Style style = (Style) layer.getStyle();

        Stream<Presentation> stream = Stream.empty();

        FeatureType type = null;
        Expression defaultGeomPropertyName = null;
        if (resource instanceof FeatureSet) {
            try {
                type = ((FeatureSet) resource).getType();

                Optional<PropertyType> opt = FeatureExt.getDefaultGeometrySafe(type);
                if (opt.isPresent()) {
                    PropertyType defaultGeometry = opt.get();
                    String geomName = FeatureStoreUtilities.defaultGeometryPropertyNameToXpathForm(defaultGeometry);
                    defaultGeomPropertyName = GO2Utilities.FILTER_FACTORY.property(geomName);
                }
            } catch (DataStoreException ex) {
                ExceptionPresentation ep = new ExceptionPresentation(ex);
                ep.setLayer(layer);
                ep.setResource(resource);
                return Stream.of(ep);
            }
        } else if (resource instanceof GridCoverageResource
                || resource instanceof BandedCoverageResource) {
            type = null;
        } else if (resource instanceof Aggregate) {
            try {
                //combine each component resource in the stream
                for (Resource r : ((Aggregate) resource).components()) {
                    stream = Stream.concat(stream, present(layer, r, renderContext));
                }
            } catch (DataStoreException ex) {
                ExceptionPresentation ep = new ExceptionPresentation(ex);
                ep.setLayer(layer);
                ep.setResource(resource);
                return Stream.of(ep);
            }
            return stream;
        } else {
            //unknown type
            return Stream.empty();
        }

        final boolean keepAllProperties = GO2Utilities.mustPreserveAllProperties(renderContext);
        for (FeatureTypeStyle fts : style.featureTypeStyles()) {
            final List<Rule> rules = GO2Utilities.getValidRules(fts, renderContext.getSEScale(), type);
            if (rules.isEmpty()) continue;

            //prepare the renderers
            final CachedRule[] cachedRules = toCachedRules(rules, type);
            final RenderingRules renderers = new RenderingRules(cachedRules, renderContext, defaultGeomPropertyName);

            {   //special case for group symbolizers
                //group symbolizers must be alone in a FTS
                SymbolizerRenderer groupRenderer = null;
                int count = 0;
                for (int i = 0; i < renderers.renderers.length; i++) {
                    for (int k = 0; k < renderers.renderers[i].length; k++) {
                        count++;
                        if (renderers.renderers[i][k].getService().isGroupSymbolizer()) {
                            groupRenderer = renderers.renderers[i][k];
                        }
                    }
                }
                if (groupRenderer != null) {
                    if (count > 1) {
                        ExceptionPresentation ep = new ExceptionPresentation(new PortrayalException("Group symbolizer ("
                                        + groupRenderer.getService().getSymbolizerClass().getSimpleName()
                                        + ") must be alone in a FeatureTypeStyle element." ));
                        ep.setLayer(layer);
                        ep.setResource(resource);
                        stream = Stream.concat(stream, Stream.of(ep));
                    } else {
                        stream = Stream.concat(stream, groupRenderer.presentations(layer, resource));
                    }
                    continue;
                }
            }

            //extract the used names
            Set<String> names;
            if (keepAllProperties) {
                names = null;
            } else {
                names = GO2Utilities.propertiesNames(rules);
                if (names.contains("*")) {
                    //we need all properties
                    names = null;
                }
            }

            //performance routine, only one symbol to render
            if (renderers.rules.length == 1
               && (renderers.rules[0].getFilter() == null || renderers.rules[0].getFilter() == Filter.include())
               && renderers.rules[0].symbolizers().length == 1) {
                stream = Stream.concat(stream, renderers.renderers[0][0].presentations(layer, resource));
                continue;
            }

            if (resource instanceof GridCoverageResource) {

                boolean painted = false;
                for (int i=0; i<renderers.elseRuleIndex; i++) {
                    final CachedRule rule = renderers.rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.test(resource)) {
                        painted = true;
                        for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                            stream = Stream.concat(stream, renderer.presentations(layer, resource));
                        }
                    }
                }

                //the data hasn't been painted, paint it with the 'else' rules
                if (!painted) {
                    for (int i=renderers.elseRuleIndex; i<renderers.rules.length; i++) {
                        final CachedRule rule = renderers.rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.test(resource)) {
                            for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                                stream = Stream.concat(stream, renderer.presentations(layer, resource));
                            }
                        }
                    }
                }

            } else if (resource instanceof FeatureSet) {
                final FeatureSet fs = (FeatureSet) resource;

                //calculate max symbol size, to expand search envelope.
                double symbolsMargin = 0.0;
                for (CachedRule rule : cachedRules) {
                    for (CachedSymbolizer cs : rule.symbolizers()) {
                        symbolsMargin = Math.max(symbolsMargin, cs.getMargin(null, renderContext));
                    }
                }
                if (Double.isNaN(symbolsMargin) || Double.isInfinite(symbolsMargin)) {
                    //symbol margin can not be pre calculated, expect a max of 300pixels
                    symbolsMargin = 300f;
                }
                if (symbolsMargin > 0) {
                    final double scale = AffineTransforms2D.getScale(renderContext.getDisplayToObjective());
                    symbolsMargin = scale * symbolsMargin;
                }

                //optimize
                Query query = null;
                try {
                    try {
                        query = RenderingRoutines.prepareQuery(renderContext, fs, layer, names, rules, symbolsMargin);
                    } catch (PropertyNotFoundException ex) {
                        //can happen if the type has subtypes and properties are only on the subtype
                        query = null;
                    }
                    final Stream<Presentation> s = (query == null ? fs : fs.subset(query)).features(false).flatMap(new Function<Feature, Stream<Presentation>>() {
                        @Override
                        public Stream<Presentation> apply(Feature feature) {

                            Stream<Presentation> stream = Stream.empty();
                            boolean painted = false;
                            for (int i = 0; i < renderers.elseRuleIndex; i++) {
                                final CachedRule rule = renderers.rules[i];
                                final Filter ruleFilter = rule.getFilter();
                                //test if the rule is valid for this feature
                                if (ruleFilter == null || ruleFilter.test(feature)) {
                                    painted = true;
                                    for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                                        stream = Stream.concat(stream, renderer.presentations(layer, feature));
                                    }
                                }
                            }

                            //the feature hasn't been painted, paint it with the 'else' rules
                            if (!painted) {
                                for (int i = renderers.elseRuleIndex; i < renderers.rules.length; i++) {
                                    final CachedRule rule = renderers.rules[i];
                                    final Filter ruleFilter = rule.getFilter();
                                    //test if the rule is valid for this feature
                                    if (ruleFilter == null || ruleFilter.test(feature)) {
                                        for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                                            stream = Stream.concat(stream, renderer.presentations(layer, feature));
                                        }
                                    }
                                }
                            }

                            return stream;
                        }
                    });
                    stream = Stream.concat(stream, s);
                } catch (PortrayalException | DataStoreException ex) {
                    ExceptionPresentation ep = new ExceptionPresentation(ex);
                    ep.setLayer(layer);
                    ep.setResource(resource);
                    return Stream.of(ep);
                }

            }
        }

        return stream;
    }

    ////////////////////////////////////////////////////////////////////////////
    // OTHER USEFULL ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static CachedRule[] toCachedRules(Collection<? extends Rule> rules, final FeatureType expected){
        final CachedRule[] cached = new CachedRule[rules.size()];
        int i=0;
        for(Rule r : rules){
            cached[i] = getCached(r, expected);
            i++;
        }
        return cached;
    }

    /**
     * Write exception in a transparent image.
     *
     * @param e   The exception to write.
     * @param dim The dimension of the image.
     * @return The transparent image with the exception in it.
     */
    public static BufferedImage writeException(final Exception e, final Dimension dim){
        return writeException(e, dim, false);
    }

    /**
     * Write an exception in an image. It is possible to set the image as transparent or
     * opaque.
     *
     * @param e The exception to write.
     * @param dim The dimension of the image.
     * @param opaque If true, the exception will be written on an opaque white background.
     *               Otherwise the image will be transparent, only the exception trace will
     *               be displayed.
     * @return The image with the exception in it.
     */
    public static BufferedImage writeException(final Exception e, final Dimension dim, final boolean opaque){
        return writeException(e, dim, opaque, null);
    }


    /**
     * Write an exception in an image. It is possible to set the image as transparent or
     * opaque.
     *
     * @param e The exception to write.
     * @param dim The dimension of the image.
     * @param opaque If true, the exception will be written on an opaque white background.
     *               Otherwise the image will be transparent, only the exception trace will
     *               be displayed.
     * @param writingColor the color of the error message. if null, {@code Color.RED} will be used.
     *
     * @return The image with the exception in it.
     */
    public static BufferedImage writeException(final Exception e, final Dimension dim, final boolean opaque, final Color writingColor){

        final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        final Font f = new Font("Dialog",Font.BOLD,12);
        final FontMetrics metrics = g.getFontMetrics(f);
        final int fontHeight = metrics.getHeight();
        final int maxCharPerLine = dim.width / metrics.charWidth('A');
        final String message = e.getMessage();

        if (opaque) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, dim.width, dim.height);
        }
        Color writeColor;
        if (writingColor == null) {
            writeColor = Color.RED;
        } else {
            writeColor = writingColor;
        }

        g.setColor(writeColor);
        if(maxCharPerLine < 1){
            //not enough space to draw error, simply use a red background
            g.setColor(writeColor);
            g.fillRect(0, 0, dim.width, dim.height);
        }else{
            int y = fontHeight;
            String remain = message;

            while(remain != null && remain.length() > 0){
                int lastChar = (maxCharPerLine > remain.length()) ? remain.length() : maxCharPerLine;
                final String oneLine = remain.substring(0, lastChar);
                remain = remain.substring(lastChar);
                g.drawString(oneLine, 2, y);
                y += fontHeight ;
                if(y > dim.height){
                    //we are out of the painting area, no need to draw more text.
                    break;
                }
            }
        }
        g.dispose();

        return img;
    }

    /**
     * Write an image in a stream using the appropriate output configuration
     *
     * @param image : image to write in stream
     * @param outputDef : output configuration
     * @throws IOException
     */
    public static void writeImage(RenderedImage image, final OutputDef outputDef) throws IOException{
        final String mime = outputDef.getMime();
        image = rectifyImageColorModel(image, mime);

        final ImageWriter writer;
        if(outputDef.getSpi() != null){
            writer = outputDef.getSpi().createWriterInstance();
        }else{
            writer = XImageIO.getWriterByMIMEType(mime, outputDef.getOutput(), image);
        }

        try{
            final ImageWriteParam param = writer.getDefaultWriteParam();

            final Boolean progress = outputDef.getProgressive();
            if(progress != null && param.canWriteProgressive()){
                if(progress){
                    param.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
                }else{
                    param.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
                }
            }

            final String type = outputDef.getCompressionType();
            final Float compression = outputDef.getCompression();
            if(type != null || compression != null){
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                if(type != null){
                    param.setCompressionType(type);
                }
                if(compression != null){
                    param.setCompressionQuality(compression);
                }
            }

            //TODO is this useless ?
            param.setDestinationType(new ImageTypeSpecifier(image.getColorModel(), image.getSampleModel()));

            Object output = outputDef.getOutput();
            final ImageWriterSpi spi = writer.getOriginatingProvider();

            if (!XImageIO.isValidType(spi.getOutputTypes(), output)) {
                output = ImageIO.createImageOutputStream(output);
            }
            writer.setOutput(output);
            try{
                writer.write(null,new IIOImage(image, null, null),param);
            }catch(IIOException ex){
                throw new IOException(ex.getLocalizedMessage()+toImageInformation(image), ex);
            }
        }finally{
            XImageIO.dispose(writer);
        }
    }

    /**
     * Returns true if an image reader or writer exist for given format or mime type.
     *
     * @param formatOrMimeType
     * @return true if a reader or writer exists.
     */
    public static boolean isImageFormat(String formatOrMimeType) {
        return IMAGEIO_FORMATS.contains(formatOrMimeType);
    }

    /**
     * Returns true if a presentation writer exist for given format or mime type.
     *
     * @param formatOrMimeType
     * @return true if a presentation writer exists.
     */
    public static boolean isPresentationFormat(String formatOrMimeType) {
        return PRESENTATION_FORMATS.containsKey(formatOrMimeType);
    }

    ////////////////////////////////////////////////////////////////////////////
    // COLOR MODEL VERIFICATION ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    //mime types which writer does not support indexed color model
    //even if the canEncode method says "true"
    private static final List<String> INDEXED_CM_UNSUPPORTED =
            UnmodifiableArrayList.wrap(new String[] {
                "image/bmp",
                "image/x-portable-pixmap"});

    /**
     * Check that the given color model is supported by the mime type.
     * This is normaly handle by a JAI method "canEncodeImage" but it has
     * some bugs. So we hard code some cases here.
     * Returns the same colormodel if it is supported by the mime-type.
     * Returns a new colormodel otherwise.
     */
    public static ColorModel rectifyColorModel(final ColorModel model, final String mime){
        if(model instanceof IndexColorModel && INDEXED_CM_UNSUPPORTED.contains(mime)){
            return new DirectColorModel(24,
                                        0x00ff0000, // Red
                                        0x0000ff00, // Green
                                        0x000000ff, // Blue
                                        0x0           // Alpha
                                        );
        }
        return model;
    }

    /**
     * @see #rectifyColorModel(java.awt.image.ColorModel, java.lang.String)
     */
    public static RenderedImage rectifyImageColorModel(RenderedImage img, final String mime){
        final ColorModel cm = img.getColorModel();
        final ColorModel rcm = rectifyColorModel(cm, mime);
        if(cm != rcm){
            //color model has been changed
            final WritableRaster wraster = rcm.createCompatibleWritableRaster(img.getWidth(), img.getHeight());
            final BufferedImage rimg = new BufferedImage(rcm, wraster, rcm.isAlphaPremultiplied(), null);
            final Graphics2D g = rimg.createGraphics();
            g.drawRenderedImage(RenderingWorkaround.wrap(img), new AffineTransform());
            g.dispose();
            img = rimg;
        }
        return img;
    }

    /**
     *
     * @return String containing a technical description of the image.
     */
    private static String toImageInformation(final RenderedImage image){
        ArgumentChecks.ensureNonNull("image", image);
        final StringBuilder sb = new StringBuilder();
        sb.append("Image : ").append(Classes.getShortClassName(image)).append('\n');
        sb.append("Height : ").append(image.getHeight()).append('\n');
        sb.append("Width : ").append(image.getWidth()).append('\n');
        sb.append("ColorModel : ").append(image.getColorModel()).append('\n');
        sb.append("SampleModel : ").append(image.getSampleModel()).append('\n');
        return sb.toString();
    }

}
