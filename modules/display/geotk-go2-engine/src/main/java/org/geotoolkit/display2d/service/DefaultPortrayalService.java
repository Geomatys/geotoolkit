/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.display2d.canvas.J2DCanvas;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.DefaultContextContainer2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.ImageIOUtilities;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Symbolizer;
import org.opengis.style.portrayal.PortrayalService;

import static org.geotoolkit.display2d.GO2Utilities.*;

/**
 * Default implementation of portrayal service.
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPortrayalService implements PortrayalService{

    private static final GridCoverageFactory GCF = new GridCoverageFactory();

    /**
     * Cache the last CoverageWriter.
     */
    private static final AtomicReference<GridCoverageWriter> WRITER_CACHE = new AtomicReference<GridCoverageWriter>();

    /**
     * Cache the link between mime-type -> java-type
     * exemple : image/png -> png
     */
    static final Map<String,String> MIME_CACHE = new ConcurrentHashMap<String, String>();

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
        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);

        renderer.setContext(context);
        try {
            canvas.getController().setObjectiveCRS(coverage.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.getController().setAxisProportions(Double.NaN);
        try {
            canvas.getController().setVisibleArea(mapArea);
        } catch (IllegalArgumentException ex) {
            throw new PortrayalException("Could not set map to requested area",ex);
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        }
        canvas.getController().repaint();
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
            final Color background, Hints hints) throws PortrayalException{
        return portray(
                new CanvasDef(canvasDimension, background, strechImage),
                new SceneDef(context, hints),
                new ViewDef(contextEnv, azimuth, monitor)
                );
    }

    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension,
            final boolean strechImage, final float azimuth, final CanvasMonitor monitor,
            final Color background, Hints hints, PortrayalExtension ... extensions) throws PortrayalException{
        return portray(
                new CanvasDef(canvasDimension, background, strechImage),
                new SceneDef(context, hints, extensions),
                new ViewDef(contextEnv, azimuth, monitor)
                );
    }

    public static BufferedImage portray(CanvasDef canvasDef, SceneDef sceneDef, ViewDef viewDef) throws PortrayalException{

        final Envelope contextEnv = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();

        final J2DCanvasBuffered canvas = new J2DCanvasBuffered(
                crs,
                canvasDef.getDimension(),
                sceneDef.getHints());

        prepareCanvas(canvas, canvasDef, sceneDef, viewDef);

        canvas.getController().repaint();
        final BufferedImage buffer = canvas.getSnapShot();
        canvas.dispose();

        return buffer;
    }

    public static void prepareCanvas(J2DCanvas canvas, CanvasDef canvasDef, SceneDef sceneDef, ViewDef viewDef) throws PortrayalException{

        final Envelope contextEnv = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();

        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
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
            canvas.getController().setObjectiveCRS(crs);
        } catch (TransformException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        final CanvasController2D control = canvas.getController();
        if(canvasDef.isStretchImage()) control.setAxisProportions(Double.NaN);
        try {
            control.setVisibleArea(contextEnv);
            if (viewDef.getAzimuth() != 0) {
                control.rotate( -Math.toRadians(viewDef.getAzimuth()) );
            }
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
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
            final Dimension canvasDimension, Hints hints, final boolean strechImage)
            throws PortrayalException {
        portray( new CanvasDef(canvasDimension,background,strechImage),
                new SceneDef(context,hints),
                new ViewDef(contextEnv),
                new OutputDef(mime, output)
                );
    }

    public static void portray(final MapContext context, final Envelope contextEnv,
            final Color background, final Object output,
            final String mime, final Dimension canvasDimension, Hints hints,
            final boolean strechImage, PortrayalExtension ... extensions) throws PortrayalException {
        portray( new CanvasDef(canvasDimension,background,strechImage),
                new SceneDef(context,hints,extensions),
                new ViewDef(contextEnv),
                new OutputDef(mime, output)
                );
    }

    public static void portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension,
            final boolean strechImage, final float azimuth, final CanvasMonitor monitor,
            final Color background, final Object output, final String mime, Hints hints,
            PortrayalExtension ... extensions) throws PortrayalException{
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
    public static void portray(CanvasDef canvasDef, SceneDef sceneDef, ViewDef viewDef,
            OutputDef outputDef) throws PortrayalException{

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

        //use the rendering engine to generate an image
        final BufferedImage image = portray(canvasDef,sceneDef,viewDef);

        if(image == null){
            throw new PortrayalException("No image created by the canvas.");
        }

        if(useCoverageWriter){
            final Envelope env = viewDef.getEnvelope();
            final Dimension dim = canvasDef.getDimension();
            final double[] resolution = new double[]{
                    env.getSpan(0) / (double)dim.width,
                    env.getSpan(1) / (double)dim.height};

            final GridCoverage2D coverage = GCF.create("PortrayalTempCoverage", image, env);
            writeCoverage(coverage, env, resolution, outputDef,null);
        }else{
            try {
                writeImage(image, outputDef);
            } catch (IOException ex) {
                throw new PortrayalException(ex);
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
    private static boolean portrayAsCoverage(CanvasDef canvasDef, SceneDef sceneDef, ViewDef viewDef,
            OutputDef outputDef) throws PortrayalException {

        //works for one layer only
        final List<MapLayer> layers = sceneDef.getContext().layers();
        if(layers.size() != 1) return false;

        //layer must be a coverage
        final MapLayer layer = layers.get(0);
        if(!(layer instanceof CoverageMapLayer)) return false;

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
        final CoverageMapLayer cml = (CoverageMapLayer) layer;
        final GridCoverageReader reader = cml.getCoverageReader();
        final String mime = outputDef.getMime();
        final Envelope env = viewDef.getEnvelope();
        final Dimension dim = canvasDef.getDimension();
        final double[] resolution = new double[]{
                env.getSpan(0) / (double)dim.width,
                env.getSpan(1) / (double)dim.height};

        final GridCoverageReadParam readParam = new GridCoverageReadParam();
        readParam.setEnvelope(viewDef.getEnvelope());
        readParam.setResolution(resolution);
        
        try{            
            GridCoverage2D coverage = (GridCoverage2D)reader.read(0, readParam);
            final RenderedImage image = coverage.getRenderedImage();

            // HACK TO FIX COLOR ERROR ON JPEG /////////////////////////////////
            if(mime.contains("jpeg") || mime.contains("jpg")){
                if(image.getColorModel().hasAlpha()){
                    final int nbBands = image.getSampleModel().getNumBands();
                    System.out.println("hasalpha");
                    if(nbBands > 3){
                        //we can remove the fourth band assuming it is the alpha
                        System.out.println("remove alpha band");
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
    private static void writeCoverage(GridCoverage coverage, Envelope env, double[] resolution,
            OutputDef outputDef, Color backgroundColor) throws PortrayalException{
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
                    Logger.getLogger(DefaultPortrayalService.class.getName()).log(Level.WARNING, null, ex1);
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

    public static void visit(CanvasDef canvasDef, SceneDef sceneDef, ViewDef viewDef, VisitDef visitDef)
            throws PortrayalException {

        final Envelope contextEnv = viewDef.getEnvelope();
        final Dimension canvasDimension = canvasDef.getDimension();
        final Hints hints = sceneDef.getHints();
        final MapContext context = sceneDef.getContext();
        final boolean strechImage = canvasDef.isStretchImage();

        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(contextEnv.getCoordinateReferenceSystem(),canvasDimension,hints);
        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);

        renderer.setContext(context);
        try {
            canvas.getController().setObjectiveCRS(contextEnv.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.getController().setAxisProportions(Double.NaN);
        try {
            canvas.getController().setVisibleArea(contextEnv);
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
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
    // OTHER USEFULL ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Write exception in a transparent image.
     *
     * @param e   The exception to write.
     * @param dim The dimension of the image.
     * @return The transparent image with the exception in it.
     */
    public static BufferedImage writeException(Exception e, Dimension dim){
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
    public static BufferedImage writeException(Exception e, Dimension dim, boolean opaque){

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

        g.setColor(Color.RED);
        if(maxCharPerLine < 1){
            //not enough space to draw error, simply use a red background
            g.setColor(Color.RED);
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
    public static void writeImage(RenderedImage image, OutputDef outputDef) throws IOException{
        final ImageWriter writer = ImageIOUtilities.getImageWriter(image, outputDef.getMime(), outputDef.getOutput());
        final ImageWriteParam param = writer.getDefaultWriteParam();

        final Float compression = outputDef.getCompression();
        if(compression != null && param.canWriteCompressed()){
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(compression);
        }

        final Boolean progress = outputDef.getProgressive();
        if(progress != null && param.canWriteProgressive()){
            if(progress){
                param.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
            }else{
                param.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
            }
        }

        Object output = outputDef.getOutput();
        final ImageWriterSpi spi = writer.getOriginatingProvider();

        ImageOutputStream stream = null;
        if (!ImageIOUtilities.isValidType(spi.getOutputTypes(), output)) {
            stream = ImageIO.createImageOutputStream(output);
            output = stream;
        }
        writer.setOutput(output);
        writer.write(null,new IIOImage(image, null, null),param);
        writer.dispose();
        if (stream != null) {
            stream.close();
        }
    }

    private static MapContext convertCoverage(final GridCoverage2D coverage){
        final MutableStyle style = STYLE_FACTORY.style(STYLE_FACTORY.rasterSymbolizer());
        final MapLayer layer = MapBuilder.createCoverageLayer(coverage, style,"coveragename");

        final MapContext context = MapBuilder.createContext(layer.getBounds().getCoordinateReferenceSystem());
        context.layers().add(layer);

        return context;
    }

}
