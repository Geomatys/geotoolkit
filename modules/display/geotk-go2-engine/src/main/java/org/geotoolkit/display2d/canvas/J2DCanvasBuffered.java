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
package org.geotoolkit.display2d.canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.container.statefull.StatefullContextJ2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.internal.image.ColorUtilities;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.visitor.ListingColorVisitor;

import org.opengis.display.canvas.RenderingState;
import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Canvas based on a BufferedImage.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class J2DCanvasBuffered extends J2DCanvas{

    private BufferedImage buffer;


    public J2DCanvasBuffered(CoordinateReferenceSystem crs, final Dimension dim){
        this(crs,dim,null);
    }

    public J2DCanvasBuffered(CoordinateReferenceSystem crs, final Dimension dim, final Hints hints){
        super(crs,hints);
        setSize(dim);
    }

    public Dimension getSize(){
        return getDisplayBounds().getBounds().getSize();
    }

    public void setSize(final Dimension dim){
        setDisplayBounds(new Rectangle(dim));
    }

    @Override
    protected void setDisplayBounds(Rectangle2D rect) {
        super.setDisplayBounds(rect);
        buffer = null; //todo should check if the size is really different
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose(){
        super.dispose();
        buffer = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage getSnapShot(){
        return buffer;
    }

    @Override
    public void repaint(Shape displayArea) {
        final Dimension dim = getSize();

        if(buffer == null){
            //create the buffer at the last possible moment
            buffer = createBufferedImage(dim);
        }else{
            //we clear the buffer if it exists
            final Graphics2D g2D = (Graphics2D) buffer.getGraphics();
            g2D.setComposite(GO2Utilities.ALPHA_COMPOSITE_0F);
            g2D.fillRect(0,0,dim.width,dim.height);
        }

        monitor.renderingStarted();
        fireRenderingStateChanged(RenderingState.RENDERING);

        final Graphics2D output = (Graphics2D) buffer.getGraphics();

        Rectangle clipBounds = output.getClipBounds();
        /*
         * Sets a flag for avoiding some "refresh()" events while we are actually painting.
         * For example some implementation of the GraphicPrimitive2D.paint(...) method may
         * detects changes since the last rendering and invokes some kind of invalidate(...)
         * methods before the graphic rendering begin. Invoking those methods may cause in some
         * indirect way a call to GraphicPrimitive2D.refresh(), which will trig an other widget
         * repaint. This second repaint is usually not needed, since Graphics usually managed
         * to update their informations before they start their rendering. Consequently,
         * disabling repaint events while we are painting help to reduces duplicated rendering.
         */
        if (clipBounds == null) {
            clipBounds = new Rectangle(dim);
        }
        output.setClip(clipBounds);
        output.addRenderingHints(hints);

        final DefaultRenderingContext2D context = prepareContext(context2D, output,null);

        //paint background if there is one.
        if(painter != null){
            painter.paint(context2D);
        }

        final AbstractContainer2D container = getContainer();
        if(container != null){
            render(context, container.getSortedGraphics());
        }

        /**
         * End painting, erase dirtyArea
         */
        output.dispose();
        fireRenderingStateChanged(RenderingState.ON_HOLD);
        monitor.renderingFinished();
    }

    /**
     * This will try to create the most efficient bufferedImage knowing
     * the different rendering parameters and hints.
     * @return
     */
    private BufferedImage createBufferedImage(Dimension dim){

        //See if a color model has been set, if so use it.
        final ColorModel cm = (ColorModel)getRenderingHint(GO2Hints.KEY_COLOR_MODEL);
        if(cm != null){
            return new BufferedImage(cm,
                    cm.createCompatibleWritableRaster(dim.width, dim.height),
                    cm.isAlphaPremultiplied(), null);
        }

        //Get the Anti-aliasing value;
        final boolean AA;
        final Object val = getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        if(RenderingHints.VALUE_ANTIALIAS_ON == val){
            AA = true;
        }else{
            //force AA off, to replace default value.
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            AA = false;
        }

        //check background painter.
        if(painter == null){

            if(AA){
                //Anti-aliasing enable, unpredictable colors
                return new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            }

            //check graphic object and see if we can predict colors
            final Set<Integer> colors = extractColors(getContainer().getSortedGraphics());

            if(colors != null){
                //translucent background
                colors.add(0);
                //we succesfully predicted the colors, makes an index color model
                return createBufferedImage(dim,colors);
            }else{
                //we can't use a index color model, use an ARGB palette
                return new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            }

        }else{
            if(painter.isOpaque()){
                //background is opaque, we are sure we don't need an alpha
                //see of we can determinate the background color, only if there is no AA.
                if(!AA && painter instanceof SolidColorPainter){
                    //check graphic object and see if we can predict colors
                    final Set<Integer> colors = extractColors(getContainer().getSortedGraphics());

                    if(colors != null){
                        final Color bgColor = ((SolidColorPainter)painter).getColor();
                        colors.add(bgColor.getRGB());
                        //we succesfully predicted the colors, makes an index color model
                        return createBufferedImage(dim,colors);
                    }else{
                        //we can't use a index color model, use an RGB palette
                        return new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
                    }
                }else{
                    //we can't determinate the background colors, use an RGB palette
                    return new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
                }
            }else{
                //we can't determinate the background colors, use an ARGB palette
                return new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            }
        }

    }

    private static BufferedImage createBufferedImage(Dimension dim, Set<Integer> colors){
        
        if(colors.size() <= 1){
            //in case no colors where used after all filters.
            //icm must have at least 2 values
            colors.add(Color.BLACK.getRGB());
            if(colors.size() == 1){
                //we are out of luck, it was black the single color.
                colors.add(0); //add translucent
            }
        }

        final int[] cmap = new int[colors.size()];
        int i = 0;
        for (Integer color : colors) {
            cmap[i++] = color;
        }

        final IndexColorModel icm = ColorUtilities.getIndexColorModel(cmap);
        return new BufferedImage(icm, icm.createCompatibleWritableRaster(dim.width, dim.height), icm.isAlphaPremultiplied(), null);
    }

    /**
     * @param graphics graphics to explore
     * @return Set of colors used by the graphics or null if unpredictable.
     */
    private static SortedSet<Integer> extractColors(List<Graphic> graphics){

        SortedSet<Integer> colors = new TreeSet<Integer>(new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                //place 0 always first
                if(o1 == 0){
                    return -1;
                }if(o2 == 0){
                    return +1;
                }else{
                    return o1.compareTo(o2);
                }
            }

        });

        for(Graphic gra : graphics){
            if(gra instanceof StatelessContextJ2D){
                final StatelessContextJ2D cn = (StatelessContextJ2D) gra;
                colors = extractColors(cn.getContext(), colors);
            }else if(gra instanceof StatefullContextJ2D){
                final StatefullContextJ2D cn = (StatefullContextJ2D) gra;
                colors = extractColors(cn.getContext(), colors);
            }else{
                //can not extract colors
                return null;
            }
        }

        return colors;
    }

    private static SortedSet<Integer> extractColors(MapContext context, SortedSet<Integer> buffer){
        for(MapLayer layer : context.layers()){
            buffer = extractColors(layer, buffer);
            if(buffer == null){
                //unpredictable colors
                return buffer;
            }
        }
        return buffer;
    }

    private static SortedSet<Integer> extractColors(MapLayer layer, SortedSet<Integer> buffer){

        final GraphicBuilder customBuilder = layer.getGraphicBuilder(GraphicJ2D.class);

        if(customBuilder != null){
            //this layer has a custom graphic builder, colors are unpredictable.
            return null;
        }

        final MutableStyle style = layer.getStyle();
        final ListingColorVisitor visitor = new ListingColorVisitor();
        style.accept(visitor, null);
        final Set<Integer> colors = visitor.getColors();

        if(colors == null){
            //unpredictable colors
            return null;
        }else{
            buffer.addAll(colors);
            return buffer;
        }

    }

}
