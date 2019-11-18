/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Portrayal data, caches the Java2D canvas for further reuse.
 * This class is not thread safe.
 *
 * @author Johann Sorel (geomatys)
 */
public final class Portrayer {

    private static final MapContext EMPTY_CONTEXT = MapBuilder.createContext();

    private final J2DCanvasBuffered canvas = new J2DCanvasBuffered(CommonCRS.WGS84.normalizedGeographic(), new Dimension(1, 1));
    private final ContextContainer2D container = new ContextContainer2D(canvas, false);

    public Portrayer(){
        canvas.setContainer(container);
        container.setContext(EMPTY_CONTEXT);
    }

    public BufferedImage portray(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef) throws PortrayalException{

        final Envelope contextEnv = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();

        canvas.setSize(canvasDef.getDimension());
        canvas.setRenderingHints(sceneDef.getHints());


        final Color bgColor = canvasDef.getBackground();
        if(bgColor != null){
            canvas.setBackgroundPainter(new SolidColorPainter(bgColor));
        }

        final CanvasMonitor monitor = viewDef.getMonitor();
        if(monitor != null){
            canvas.setMonitor(monitor);
        }

        final MapContext context = sceneDef.getContext();
        container.setContext(context);
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

        canvas.repaint();
        final BufferedImage buffer = canvas.getSnapShot();
        container.setContext(EMPTY_CONTEXT);

        return buffer;
    }

}
