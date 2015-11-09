/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.ext.awt.image.codec.png.PNGImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import static org.geotoolkit.display.canvas.AbstractCanvas.ON_HOLD;
import static org.geotoolkit.display.canvas.AbstractCanvas.RENDERING;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.factory.Hints;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class J2DCanvasSVG extends J2DCanvas{

    static {
        //Batik pseudo-image api is a total mess, normaly auto detection should work
        //but well ... I never managed to see it work, so we do it by hand
        ImageWriterRegistry.getInstance().register(new PNGImageWriter());
    }

    private final SVGGraphics2D g2D;
    private final Document document;

    public J2DCanvasSVG(final CoordinateReferenceSystem crs, final Dimension dim){
        this(crs,dim,null);
    }

    public J2DCanvasSVG(final CoordinateReferenceSystem crs, final Dimension dim, final Hints hints){
        super(crs,hints);
        setSize(dim);

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        g2D = new SVGGraphics2D(document);
    }

    public Dimension getSize(){
        return getDisplayBounds().getBounds().getSize();
    }

    public void setSize(final Dimension dim){
        setDisplayBounds(new Rectangle(dim));
    }

    @Override
    public void setDisplayBounds(final Rectangle2D rect) {
        super.setDisplayBounds(rect);
    }

    @Override
    public void repaint(Shape area) {
        //finish any previous painting
        getMonitor().stopRendering();

        final Dimension dim = getSize();
        g2D.setSVGCanvasSize(dim);

        monitor.renderingStarted();
        fireRenderingStateChanged(RENDERING);


        Rectangle clipBounds = g2D.getClipBounds();
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
        g2D.setClip(clipBounds);
        g2D.addRenderingHints(getHints(true));

        final RenderingContext2D context = prepareContext(context2D, g2D,null);

        //paint background if there is one.
        if(painter != null){
            painter.paint(context2D);
        }

        final GraphicContainer container = getContainer();
        if(container != null){
            render(context, container.flatten(true));
        }

        /**
         * End painting, erase dirtyArea
         */
        g2D.dispose();
        fireRenderingStateChanged(ON_HOLD);
        monitor.renderingFinished();
    }

    @Override
    public Image getSnapShot() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void getDocument(Writer writer) throws UnsupportedEncodingException, SVGGraphics2DIOException{

        boolean useCSS = true;
        g2D.stream(writer, useCSS);
    }

}
