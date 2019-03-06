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
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.*;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.canvas.J2DCanvasSVG;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.stateless.DefaultCachedRule;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.renderer.Presentation;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.opengis.style.portrayal.PortrayalService;

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
    private static final AtomicReference<GridCoverageWriter> WRITER_CACHE = new AtomicReference<>();

    /**
     * Cache the link between mime-type -> java-type
     * exemple : image/png -> png
     */
    static final Map<String,String> MIME_CACHE = new ConcurrentHashMap<>();

    private DefaultPortrayalService(){}


    /**
     * Portray a gridcoverage using amplitute windarrows/cercles
     *
     * @param coverage : grid coverage to render
     * @param mapArea : mapArea to render
     * @param canvasDimension : size of the wanted image
     * @return resulting image of the portraying operation
     */
    public static Image portray(final GridCoverage2D coverage, final Envelope mapArea,
            final Dimension canvasDimension, final boolean strechImage)
            throws PortrayalException{

        final MapContext context = convertCoverage(coverage);
        return portray(context,mapArea,canvasDimension,strechImage);
    }

    /**
     * Portray a gridcoverage using amplitute windarrows/cercles
     *
     * @param coverage : grid coverage to render
     * @param mapArea : mapArea to render
     * @param canvasDimension : size of the wanted image
     * @return resulting image of the portraying operation
     */
    public static BufferedImage portray(final GridCoverage2D coverage, final Rectangle2D mapArea,
            final Dimension canvasDimension, final boolean strechImage)
            throws PortrayalException{

        final MapContext context = convertCoverage(coverage);
        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(coverage.getCoordinateReferenceSystem(),canvasDimension);
        final ContextContainer2D renderer = new ContextContainer2D(canvas, false);
        canvas.setContainer(renderer);

        renderer.setContext(context);
        try {
            canvas.setObjectiveCRS(coverage.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.setAxisProportions(Double.NaN);
        try {
            canvas.setVisibleArea(mapArea);
        } catch (IllegalArgumentException ex) {
            throw new PortrayalException("Could not set map to requested area",ex);
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        }
        canvas.repaint();
        BufferedImage buffer = canvas.getSnapShot();
        canvas.dispose();

        return buffer;
    }

    ////////////////////////////////////////////////////////////////////////////
    // PAINTING IN A BUFFERED IMAGE ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Portray a MapContext and returns an Image.
     *
     * @param context : map context to render
     * @param contextEnv : map area to render
     * @param canvasDimension : size of the wanted image
     * @return resulting image of the portraying operation
     */
    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension, final boolean strechImage)
            throws PortrayalException{
        return portray(
                new CanvasDef(canvasDimension, null,strechImage),
                new SceneDef(context),
                new ViewDef(contextEnv)
                );
    }

    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension, final boolean strechImage, final float azimuth,
            final CanvasMonitor monitor, final Color background)
            throws PortrayalException{
        return portray(
                new CanvasDef(canvasDimension, background, strechImage),
                new SceneDef(context),
                new ViewDef(contextEnv, azimuth, monitor)
                );
    }

    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension,
            final boolean strechImage, final float azimuth, final CanvasMonitor monitor,
            final Color background, final Hints hints) throws PortrayalException{
        return portray(
                new CanvasDef(canvasDimension, background, strechImage),
                new SceneDef(context, hints),
                new ViewDef(contextEnv, azimuth, monitor)
                );
    }

    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension,
            final boolean strechImage, final float azimuth, final CanvasMonitor monitor,
            final Color background, final Hints hints, final PortrayalExtension ... extensions) throws PortrayalException{
        return portray(
                new CanvasDef(canvasDimension, background, strechImage),
                new SceneDef(context, hints, extensions),
                new ViewDef(contextEnv, azimuth, monitor)
                );
    }

    public static BufferedImage portray(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef) throws PortrayalException{

        final Envelope contextEnv = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();

        final J2DCanvasBuffered canvas = new J2DCanvasBuffered(
                crs,
                canvasDef.getDimension(),
                sceneDef.getHints());

        prepareCanvas(canvas, canvasDef, sceneDef, viewDef);

        canvas.repaint();
        final BufferedImage buffer = canvas.getSnapShot();
        canvas.dispose();

        return buffer;
    }

    public static void prepareCanvas(final J2DCanvas canvas, final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef) throws PortrayalException{

        final Envelope contextEnv = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();

        final ContextContainer2D renderer = new ContextContainer2D(canvas, false);
        canvas.setContainer(renderer);

        final Color bgColor = canvasDef.getBackground();
        if(bgColor != null){
            canvas.setBackgroundPainter(new SolidColorPainter(bgColor));
        }

        final CanvasMonitor monitor = viewDef.getMonitor();
        if(monitor != null){
            canvas.setMonitor(monitor);
        }

        final Hints hints = sceneDef.getHints();
        if(hints != null){
            for(Entry<?,?> entry : hints.entrySet()){
                canvas.setRenderingHint((Key)entry.getKey(), entry.getValue());
            }
        }

        final MapContext context = sceneDef.getContext();
        renderer.setContext(context);
        try {
            canvas.setObjectiveCRS(crs);
        } catch (TransformException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        if(canvasDef.isStretchImage()) canvas.setAxisProportions(Double.NaN);
        try {
            canvas.setVisibleArea(contextEnv);
            if (viewDef.getAzimuth() != 0) {
                canvas.rotate( -Math.toRadians(viewDef.getAzimuth()) );
            }
        } catch (NoninvertibleTransformException | TransformException ex) {
            throw new PortrayalException(ex);
        }

        //paints all extensions
        final List<PortrayalExtension> extensions = sceneDef.extensions();
        if(extensions != null){
            for(final PortrayalExtension extension : extensions){
                if(extension != null) extension.completeCanvas(canvas);
            }
        }

    }

    /**
     * Create a rendered image which tile model maps the given definition.
     * The image will be divided in the same number of tiles and size as the mosaic.
     * Unlike a call to a portray method, the returned rendered image will be calculated
     * progressively.
     *
     * @param mosaic
     * @param def
     * @param sceneDef
     * @param viewDef
     * @return RenderedImage , never null
     */
    public static RenderedImage prepareImage(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef,
            final Dimension gridSize, final Dimension tileSize, final double scale) throws PortrayalException{
        return new PortrayalRenderedImage(canvasDef, sceneDef, viewDef, gridSize, tileSize, scale);
    }

    /**
     * Manipulate a MapContext as if it was an ARGB coverage of infinite resolution.
     *
     * @param sceneDef
     * @return GridCoverageReader, never null
     */
    public static GridCoverageReader asCoverageReader(final SceneDef sceneDef){
        return new PortrayalCoverageReader(sceneDef);
    }

    ////////////////////////////////////////////////////////////////////////////
    // PAINTING IN A STREAM or OUTPUT //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Portray a MapContext and outpur it in the given
     * stream.
     *
     * @param context : Mapcontext to render
     * @param contextEnv : MapArea to render
     * @param output : output srteam or file or url
     * @param mime : mime output type
     * @param canvasDimension : size of the wanted image
     */
    public static void portray(final MapContext context, final Envelope contextEnv,
            final Object output, final String mime, final Dimension canvasDimension,
            final boolean strechImage) throws PortrayalException {
        portray( new CanvasDef(canvasDimension,null,strechImage),
                new SceneDef(context),
                new ViewDef(contextEnv),
                new OutputDef(mime, output)
                );
    }

    /**
     * Portray a MapContext and outpur it in the given
     * stream.
     *
     * @param context : Mapcontext to render
     * @param contextEnv : MapArea to render
     * @param output : output srteam or file or url
     * @param mime : mime output type
     * @param canvasDimension : size of the wanted image
     * @param hints : canvas hints
     */
    public static void portray(final MapContext context, final Envelope contextEnv,
            final Color background, final Object output, final String mime,
            final Dimension canvasDimension, final Hints hints, final boolean strechImage)
            throws PortrayalException {
        portray( new CanvasDef(canvasDimension,background,strechImage),
                new SceneDef(context,hints),
                new ViewDef(contextEnv),
                new OutputDef(mime, output)
                );
    }

    public static void portray(final MapContext context, final Envelope contextEnv,
            final Color background, final Object output,
            final String mime, final Dimension canvasDimension, final Hints hints,
            final boolean strechImage, final PortrayalExtension ... extensions) throws PortrayalException {
        portray( new CanvasDef(canvasDimension,background,strechImage),
                new SceneDef(context,hints,extensions),
                new ViewDef(contextEnv),
                new OutputDef(mime, output)
                );
    }

    public static void portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension,
            final boolean strechImage, final float azimuth, final CanvasMonitor monitor,
            final Color background, final Object output, final String mime, final Hints hints,
            final PortrayalExtension ... extensions) throws PortrayalException{
        portray( new CanvasDef(canvasDimension,background,strechImage),
                new SceneDef(context,hints,extensions),
                new ViewDef(contextEnv,azimuth,monitor),
                new OutputDef(mime, output)
                );
    }

    /**
     *
     * @param canvasDef
     * @param sceneDef
     * @param viewDef
     * @param outputDef : The compression parameter will not necesarly be used
     *              if the mime type write can not support it.
     * @throws PortrayalException
     */
    public static void portray(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef,
            final OutputDef outputDef) throws PortrayalException{

        final String mime = outputDef.getMime();
        if(mime.contains("jpeg") || mime.contains("jpg")){
            //special case for jpeg format, the writer generate incorrect colors
            //if he find out an alpha channel, so we ensure to have a opaque background
            //which will result in at least an RGB palette
            final Color bgColor = canvasDef.getBackground();
            if(bgColor == null){
                //we set the background white
                canvasDef.setBackground(Color.WHITE);
            }else{
                //we merge colors
                canvasDef.setBackground(mergeColors(Color.WHITE, bgColor));
            }
        }

        //directly return false if hints doesnt contain the coverage writer hint enabled
        final Hints hints = sceneDef.getHints();
        final Object val = (hints!=null)?hints.get(GO2Hints.KEY_COVERAGE_WRITER):null;
        final boolean useCoverageWriter = GO2Hints.COVERAGE_WRITER_ON.equals(val);

        if(useCoverageWriter && portrayAsCoverage(canvasDef, sceneDef, viewDef, outputDef)){
            //we succeeded in writing it with coverage writer directly.
            return;
        }

        if("image/svg+xml".equalsIgnoreCase(mime)){
            //special canvas for svg
            final Envelope contextEnv = viewDef.getEnvelope();
            final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();
            final J2DCanvasSVG canvas = new J2DCanvasSVG(
                    crs,
                    canvasDef.getDimension(),
                    sceneDef.getHints());
            prepareCanvas(canvas, canvasDef, sceneDef, viewDef);
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

        }else{
            //use the rendering engine to generate an image
            BufferedImage image = portray(canvasDef,sceneDef,viewDef);

            if(image == null){
                throw new PortrayalException("No image created by the canvas.");
            }


            if(useCoverageWriter){
                final Envelope env = viewDef.getEnvelope();
                final Dimension dim = canvasDef.getDimension();
                final double[] resolution = new double[]{
                        env.getSpan(0) / (double)dim.width,
                        env.getSpan(1) / (double)dim.height};

                //check the image color model
                image = (BufferedImage) rectifyImageColorModel(image, mime);

                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setEnvelope(env);
                gcb.setRenderedImage(image);
                final GridCoverage2D coverage = gcb.getGridCoverage2D();
                writeCoverage(coverage, env, resolution, outputDef,null);
            }else{
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
    private static boolean portrayAsCoverage(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef,
            final OutputDef outputDef) throws PortrayalException {

        //works for one layer only
        final List<MapLayer> layers = sceneDef.getContext().layers();
        if(layers.size() != 1) return false;

        //layer must be a coverage
        final MapLayer layer = layers.get(0);
        final Resource resource = layer.getResource();
        if(!(resource instanceof GridCoverageResource)) return false;

        //we must not have extensions
        if(!sceneDef.extensions().isEmpty()) return false;

        //style must be a default raster style = native original style
        final List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();
        if(ftss.size() != 1) return false;
        final List<MutableRule> rules = ftss.get(0).rules();
        if(rules.size() != 1) return false;
        final List<Symbolizer> symbols = rules.get(0).symbolizers();
        if(symbols.size() != 1) return false;
        final Symbolizer s = symbols.get(0);
        if(!GO2Utilities.isDefaultRasterSymbolizer(s)) return false;

        //we can bypass the renderer
        try{
            final GridCoverageResource ref = (GridCoverageResource) resource;
            final CoverageReader reader = ref.acquireReader();
            final String mime = outputDef.getMime();
            final Envelope env = viewDef.getEnvelope();
            final Dimension dim = canvasDef.getDimension();
            final double[] resolution = new double[]{
                    env.getSpan(0) / (double)dim.width,
                    env.getSpan(1) / (double)dim.height};

            final GridCoverageReadParam readParam = new GridCoverageReadParam();
            readParam.setEnvelope(viewDef.getEnvelope());
            readParam.setResolution(resolution);

            GridCoverage2D coverage = (GridCoverage2D)reader.read(readParam);
            final RenderedImage image = coverage.getRenderedImage();
            ref.recycle(reader);

            // HACK TO FIX COLOR ERROR ON JPEG /////////////////////////////////
            if(mime.contains("jpeg") || mime.contains("jpg")){
                if(image.getColorModel().hasAlpha()){
                    final int nbBands = image.getSampleModel().getNumBands();
                    if(nbBands > 3){
                        //we can remove the fourth band assuming it is the alpha
                        coverage = (GridCoverage2D) Operations.DEFAULT.selectSampleDimension(coverage, new int[]{0,1,2});
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////

            writeCoverage(coverage, env, resolution, outputDef, canvasDef.getBackground());
        }catch(CoverageStoreException ex){
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
        GridCoverageWriter writer = WRITER_CACHE.getAndSet(null);
        if(writer == null){
            writer = new ImageCoverageWriter();
        }

        try{
            final GridCoverageWriteParam writeParam = new GridCoverageWriteParam();
            writeParam.setEnvelope(env);
            writeParam.setResolution(resolution);
            writeParam.setFormatName(javaType);
            writeParam.setCompressionQuality(outputDef.getCompression());

            if(backgroundColor != null){
                final int r = backgroundColor.getRed();
                final int g = backgroundColor.getGreen();
                final int b = backgroundColor.getBlue();
                final int a = backgroundColor.getAlpha();
                writeParam.setBackgroundValues(new double[]{r,g,b,a});
            }

            writer.setOutput(outputDef.getOutput());
            writer.write(coverage, writeParam);

        }catch(CoverageStoreException ex){
            throw new PortrayalException(ex);
        }finally{
            try {
                writer.reset();
                if(!WRITER_CACHE.compareAndSet(null, writer)){
                    try {
                        writer.dispose();
                    } catch (CoverageStoreException ex) {
                        throw new PortrayalException(ex);
                    }
                }
            } catch (CoverageStoreException ex) {
                //the writer has problems, we better not put in back in the cache.
                try {
                    writer.dispose();
                } catch (CoverageStoreException ex1) {
                    Logging.getLogger("org.geotoolkit.display2d.service").log(Level.WARNING, null, ex1);
                }
                throw new PortrayalException(ex);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // VISITING A CONTEXT //////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static void visit( final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension, final boolean strechImage, final Hints hints,
            final Shape selectedArea, final GraphicVisitor visitor)
            throws PortrayalException {
        visit(    new CanvasDef(canvasDimension,null,strechImage),
                new SceneDef(context,hints),
                new ViewDef(contextEnv),
                new VisitDef(selectedArea, visitor)
                );
    }

    public static void visit(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef, final VisitDef visitDef)
            throws PortrayalException {

        final Envelope contextEnv = viewDef.getEnvelope();
        final Dimension canvasDimension = canvasDef.getDimension();
        final Hints hints = sceneDef.getHints();
        final MapContext context = sceneDef.getContext();
        final boolean strechImage = canvasDef.isStretchImage();

        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(contextEnv.getCoordinateReferenceSystem(),canvasDimension,hints);
        final ContextContainer2D renderer = new ContextContainer2D(canvas, false);
        canvas.setContainer(renderer);

        renderer.setContext(context);
        try {
            canvas.setObjectiveCRS(contextEnv.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.setAxisProportions(Double.NaN);
        try {
            canvas.setVisibleArea(contextEnv);
        } catch (NoninvertibleTransformException | TransformException ex) {
            throw new PortrayalException(ex);
        }

        final Shape selectedArea = visitDef.getArea();
        final GraphicVisitor visitor = visitDef.getVisitor();
        try {
            canvas.getGraphicsIn(selectedArea, visitor, VisitFilter.INTERSECTS);
        } catch(Exception ex) {
            if(ex instanceof PortrayalException){
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
     *
     * @param canvasDef
     * @param sceneDef
     * @param viewDef
     * @return
     * @throws PortrayalException
     */
    public static Spliterator<Presentation> present(final CanvasDef canvasDef,
            final SceneDef sceneDef, final ViewDef viewDef) throws PortrayalException, DataStoreException{

        final Envelope contextEnv = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();
        final Dimension dim = canvasDef.getDimension();
        final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);

        final J2DCanvasBuffered canvas = new J2DCanvasBuffered(crs,
                canvasDef.getDimension(),sceneDef.getHints());
        prepareCanvas(canvas, canvasDef, sceneDef, viewDef);
        final RenderingContext2D renderContext = new RenderingContext2D(canvas);
        canvas.prepareContext(renderContext, img.createGraphics(), new Rectangle(canvasDef.getDimension()));

        final List<Presentation> presentations = new ArrayList<>();

        final MapContext context = sceneDef.getContext();
        final List<MapLayer> layers = context.layers();
        for (MapLayer layer : layers) {
            if (!layer.isVisible()) continue;
            if (!(layer instanceof FeatureMapLayer)) continue;

            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final FeatureSet resource = fml.getResource();
            final FeatureType type = resource.getType();
            final List<Rule> rules = StatelessFeatureLayerJ2D.getValidRules(renderContext, fml, type);

            if (rules.isEmpty()) continue;

            final CachedRule[] cachedRules = StatelessFeatureLayerJ2D.toCachedRules(rules, type);

            //prepare the renderers
            final DefaultCachedRule renderers = new DefaultCachedRule(cachedRules, renderContext);

            final StatelessContextParams params = new StatelessContextParams(canvas,layer);
            params.update(renderContext);
            final ProjectedFeature projectedFeature = new ProjectedFeature(params,null);

            try (final Stream<Feature> stream = resource.features(false)) {

                final Iterator<ProjectedFeature> ite = stream.map((Feature t) -> {
                    projectedFeature.setCandidate(t);
                    return projectedFeature;
                }).iterator();

                //performance routine, only one symbol to render
                if (renderers.rules.length == 1
                   && (renderers.rules[0].getFilter() == null || renderers.rules[0].getFilter() == Filter.INCLUDE)
                   && renderers.rules[0].symbolizers().length == 1) {
                    renderers.renderers[0][0].presentation(ite).forEachRemaining(presentations::add);
                    continue;
                }

                while (ite.hasNext()) {
                    final ProjectedObject projectedCandidate = ite.next();

                    boolean painted = false;
                    for (int i=0; i<renderers.elseRuleIndex; i++) {
                        final CachedRule rule = renderers.rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                            painted = true;
                            for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                                renderer.presentation(projectedCandidate).forEachRemaining(presentations::add);
                            }
                        }
                    }

                    //the feature hasn't been painted, paint it with the 'else' rules
                    if(!painted){
                        for (int i=renderers.elseRuleIndex; i<renderers.rules.length; i++) {
                            final CachedRule rule = renderers.rules[i];
                            final Filter ruleFilter = rule.getFilter();
                            //test if the rule is valid for this feature
                            if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                                for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                                    renderer.presentation(projectedCandidate).forEachRemaining(presentations::add);
                                }
                            }
                        }
                    }
                }
            }
        }

        return presentations.spliterator();
    }

    ////////////////////////////////////////////////////////////////////////////
    // OTHER USEFULL ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

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

    private static MapContext convertCoverage(final GridCoverage2D coverage){
        final MutableStyle style = STYLE_FACTORY.style(STYLE_FACTORY.rasterSymbolizer());
        final MapLayer layer = MapBuilder.createCoverageLayer(coverage, style,"coveragename");

        final MapContext context = MapBuilder.createContext(layer.getBounds().getCoordinateReferenceSystem());
        context.layers().add(layer);

        return context;
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
            g.drawRenderedImage(img, new AffineTransform());
            g.dispose();
            img = rimg;
        }
        return img;
    }

    /**
     *
     * @param image
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
