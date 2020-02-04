/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.awt.Graphics2D;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Definition of a canvas, dimension, background and image stretching.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CanvasDef {

    private Dimension dimension;
    private Color background;
    private boolean stretchImage = true;
    private Envelope envelope = new JTSEnvelope2D(CommonCRS.WGS84.normalizedGeographic());
    private double azimuth;
    private CanvasMonitor monitor;
    private GridGeometry gridGeometry;
    private Graphics2D graphics;

    public CanvasDef() {
    }

    public CanvasDef(final Dimension dim, final Envelope env) {
        this.dimension = dim;
        this.envelope = env;
    }

    public CanvasDef(GridGeometry gridGeometry) {
        this.gridGeometry = gridGeometry;
    }

    public void setDimension(final Dimension dimension) {
        this.dimension = dimension;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setBackground(final Color background) {
        this.background = background;
    }

    public Color getBackground() {
        return background;
    }

    public void setStretchImage(final boolean stretchImage) {
        this.stretchImage = stretchImage;
    }

    public boolean isStretchImage() {
        return stretchImage;
    }

    public void setEnvelope(final Envelope envelope) {
        this.envelope = envelope;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setAzimuth(final double azimuth) {
        this.azimuth = azimuth;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setMonitor(final CanvasMonitor monitor) {
        this.monitor = monitor;
    }

    public CanvasMonitor getMonitor() {
        return monitor;
    }

    public GridGeometry getGridGeometry() {
        return gridGeometry;
    }

    public void setGridGeometry(GridGeometry gridGeometry) {
        this.gridGeometry = gridGeometry;
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics2D graphics) {
        this.graphics = graphics;
    }

    /**
     * Test and update the Envelope to ensure that the East-West axis is placed horizontaly.
     * This has no effect if the axis direction can not determinate.
     *
     * @return ViewDef : this same object
     */
    public CanvasDef setLongitudeFirst() throws TransformException, FactoryException{
        final Envelope env = getEnvelope();
        if (env != null) {
            setEnvelope( ReferencingUtilities.setLongitudeFirst(env) );
        }
        return this;
    }

    @Override
    public String toString() {
        return "CanvasDef[dimension="+ dimension +", background="+ background +", stretchImage="+ stretchImage +", envelope=" + envelope + ", azimuth=" + azimuth +"]";
    }
}
