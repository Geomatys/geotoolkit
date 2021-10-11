/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.display2d.canvas;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import org.geotoolkit.display.canvas.control.NeverFailMonitor;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.factory.Hints;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Special canvas used to render maps directly on provided Graphics2D.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class J2DCanvasDirect extends J2DCanvas {

    private Graphics2D g2d = null;

    public J2DCanvasDirect(CoordinateReferenceSystem crs, Hints hints) {
        super(crs, hints);
        monitor = new NeverFailMonitor();
    }

    public Graphics2D getGraphics2D() {
        return g2d;
    }

    public void setGraphics2D(Graphics2D g2d) {
        this.g2d = g2d;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clearCache() {
        super.clearCache();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean repaint(final Shape area) {
        monitor.renderingStarted();
        fireRenderingStateChanged(RENDERING);

        final Graphics2D output = g2d;
        output.addRenderingHints(getHints(true));

        final RenderingContext2D context = prepareContext(output, null);

        //paint background if there is one.
        if (painter != null) {
            painter.paint(context);
        }

        boolean dataPainted = false;
        final GraphicContainer container = getContainer();
        if (container != null) {
            dataPainted |= render(context, container.flatten(true));
        }

        /**
         * End painting, erase dirtyArea
         */
        output.dispose();
        fireRenderingStateChanged(ON_HOLD);
        monitor.renderingFinished();
        return dataPainted;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage getSnapShot() {
        throw new UnsupportedOperationException("JasperCanvas doesnt support getSnapshot");
    }

}
