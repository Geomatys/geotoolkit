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
import java.io.IOException;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.DefaultContextContainer2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.ImageIOUtilities;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.portrayal.PortrayalService;

import static org.geotoolkit.display2d.GO2Utilities.*;

/**
 * Default implementation of portrayal service.
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPortrayalService implements PortrayalService{
        
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
        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);
        canvas.setBackgroundPainter(new SolidColorPainter(canvasDef.getBackground()));

        final CanvasMonitor monitor = viewDef.getMonitor();
        if(monitor != null){
            canvas.setMonitor(monitor);
        }

        final Hints hints = sceneDef.getHints();
        if(hints != null){
            for(Object key : hints.keySet()){
                canvas.setRenderingHint((Key)key, hints.get(key));
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
                extension.completeCanvas(canvas);
            }
        }

        canvas.getController().repaint();
        final BufferedImage buffer = canvas.getSnapShot();
        canvas.dispose();

        return buffer;
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

        final BufferedImage image = portray(canvasDef,sceneDef,viewDef);

        if(image == null){
            throw new PortrayalException("No image created by the canvas.");
        }

        try {
            final ImageWriter writer = ImageIOUtilities.getImageWriter(image, outputDef.getMime(), outputDef.getOutput());
            final ImageWriteParam param = writer.getDefaultWriteParam();

            final Float compression = outputDef.getCompression();
            if(compression != null && param.canWriteCompressed()){
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(compression);
            }

            final Boolean progress = outputDef.getProgressive();
            if(progress != null){
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


        } catch (IOException ex) {
            throw new PortrayalException(ex);
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

    private static MapContext convertCoverage(final GridCoverage2D coverage){
        final MutableStyle style = STYLE_FACTORY.style(STYLE_FACTORY.rasterSymbolizer());
        final MapLayer layer = MapBuilder.createCoverageLayer(coverage, style,"coveragename");

        final MapContext context = MapBuilder.createContext(layer.getBounds().getCoordinateReferenceSystem());
        context.layers().add(layer);

        return context;
    }

}
