/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
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
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.HintKey;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.DefaultContextContainer2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.portrayal.PortrayalService;

import static org.geotoolkit.display2d.style.GO2Utilities.*;

/**
 * Default implementation of portrayal service.
 * @author Johann Sorel (Geomatys)
 */
public class DefaultPortrayalService implements PortrayalService{
        
    private DefaultPortrayalService(){}
    
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
        portray(context, contextEnv, null, output, mime, canvasDimension, null, strechImage);
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
        portray(context, contextEnv, null,null,background,output,mime,canvasDimension, hints,strechImage);
    }

    public static void portray(final MapContext context, final Envelope contextEnv,
            final Date start, final Date end, final Color background, final Object output, 
            final String mime, final Dimension canvasDimension, Hints hints, 
            final boolean strechImage) throws PortrayalException {

        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(contextEnv.getCoordinateReferenceSystem(),canvasDimension,hints);
        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);
        canvas.setBackground(background);
        
        try {
            renderer.setContext(context);
            canvas.getController().setObjectiveCRS(contextEnv.getCoordinateReferenceSystem());
            canvas.getController().setTemporalRange(start, end);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.getController().setAxisProportions(Double.NaN);
        try {
            canvas.getController().setVisibleArea(contextEnv);
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        }
        canvas.getController().repaint();
        final BufferedImage image = canvas.getSnapShot();

        //dispose the canvas, avoid memory leack
        canvas.dispose();

        if(image == null){
            throw new PortrayalException("No image created by the canvas.");
        }
        
        try {
            writeImage(image, mime, output);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        }
        
    }

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

        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(contextEnv.getCoordinateReferenceSystem(),canvasDimension);
        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);
        

        try {
            renderer.setContext(context);
            canvas.getController().setObjectiveCRS(contextEnv.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.getController().setAxisProportions(Double.NaN);
        try {
            canvas.getController().setVisibleArea(contextEnv);
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        }
        canvas.getController().repaint();
        BufferedImage buffer = canvas.getSnapShot();
        canvas.dispose();

        return buffer;
    }

    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension, final boolean strechImage, final float azimuth,
            final CanvasMonitor monitor, final Color background)
            throws PortrayalException{
        return portray(context,contextEnv,null,null,canvasDimension,strechImage,azimuth,monitor,background);
    }

    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Date start, final Date end, final Dimension canvasDimension,
            final boolean strechImage, final float azimuth, final CanvasMonitor monitor,
            final Color background) throws PortrayalException{
        return portray(context,contextEnv,start,end,canvasDimension,strechImage,azimuth,monitor,background,null);
    }

    public static BufferedImage portray(final MapContext context, final Envelope contextEnv,
            final Date start, final Date end, final Dimension canvasDimension,
            final boolean strechImage, final float azimuth, final CanvasMonitor monitor,
            final Color background, Hints hints) throws PortrayalException{

        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(contextEnv.getCoordinateReferenceSystem(),canvasDimension,hints);
        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);
        canvas.setBackground(background);
        canvas.setMonitor(monitor);

        if(hints != null){
            for(Object key : hints.keySet()){
                canvas.setRenderingHint((HintKey)key, hints.get(key));
            }
        }

        try {
            renderer.setContext(context);
            canvas.getController().setObjectiveCRS(contextEnv.getCoordinateReferenceSystem());
            canvas.getController().setTemporalRange(start, end);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        //we specifically say to not repect X/Y proportions
        final CanvasController2D control = canvas.getController();
        if(strechImage) control.setAxisProportions(Double.NaN);
        try {
            control.setVisibleArea(contextEnv);
            if (azimuth != 0) {
                control.rotate( -Math.toRadians(azimuth) );
            }
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        }
        canvas.getController().repaint();
        BufferedImage buffer = canvas.getSnapShot();
        canvas.dispose();

        return buffer;
    }


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

        try {
            renderer.setContext(context);
            canvas.getController().setObjectiveCRS(coverage.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.getController().setAxisProportions(Double.NaN);
        try {
            canvas.getController().setVisibleArea(mapArea);
        } catch (IllegalArgumentException ex) {
            throw new PortrayalException(ex);
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        }
        canvas.getController().repaint();
        BufferedImage buffer = canvas.getSnapShot();
        canvas.dispose();

        return buffer;
    }

    public static void visit( final MapContext context, final Envelope contextEnv,
            final Dimension canvasDimension, final boolean strechImage, final Hints hints,
            final Shape selectedArea, final GraphicVisitor visitor)
            throws PortrayalException {

        final J2DCanvasBuffered canvas = new  J2DCanvasBuffered(contextEnv.getCoordinateReferenceSystem(),canvasDimension,hints);
        final ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);

        try {
            renderer.setContext(context);
            canvas.getController().setObjectiveCRS(contextEnv.getCoordinateReferenceSystem());
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        //we specifically say to not repect X/Y proportions
        if(strechImage) canvas.getController().setAxisProportions(Double.NaN);
        try {
            canvas.getController().setVisibleArea(contextEnv);
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        }

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


    public static BufferedImage writeException(Exception e, Dimension dim){

        final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        final Font f = new Font("Dialog",Font.BOLD,12);
        final FontMetrics metrics = g.getFontMetrics(f);
        final int fontHeight = metrics.getHeight();
        final int maxCharPerLine = dim.width / metrics.charWidth('A');
        final String message = e.getMessage();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, dim.width, dim.height);

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

    public static void writeImage(final BufferedImage image,
            final String mime, Object output) throws IOException{
        if(image == null) throw new NullPointerException("Image can not be null");

        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mime);
        while (writers.hasNext()) {
            final ImageWriter writer = writers.next();
            final ImageWriterSpi spi = writer.getOriginatingProvider();
            if (spi.canEncodeImage(image)) {
                ImageOutputStream stream = null;
                if (!isValidType(spi.getOutputTypes(), output)) {
                    stream = ImageIO.createImageOutputStream(output);
                    output = stream;
                }
                writer.setOutput(output);
                writer.write(image);
                writer.dispose();
                if (stream != null) {
                    stream.close();
                }

                return;
            }
        }

        throw new IOException("Unknowed image type");
    }

    private static MapContext convertCoverage(final GridCoverage2D coverage){
        final MutableStyle style = STYLE_FACTORY.style(STYLE_FACTORY.rasterSymbolizer());
        final MapLayer layer = MapBuilder.createCoverageLayer(coverage, style,"coveragename");

        final MapContext context = MapBuilder.createContext(layer.getBounds().getCoordinateReferenceSystem());
        context.layers().add(layer);

        return context;
    }

    /**
     * Check if the provided object is an instance of one of the given classes.
     */
    private static boolean isValidType(final Class<?>[] validTypes, final Object type) {
        for (final Class<?> t : validTypes) {
            if (t.isInstance(type)) {
                return true;
            }
        }
        return false;
    }
    
    
}
