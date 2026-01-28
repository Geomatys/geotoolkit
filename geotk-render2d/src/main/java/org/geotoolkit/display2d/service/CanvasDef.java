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
import java.util.Arrays;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.referencing.internal.shared.AxisDirections;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.apache.sis.coverage.grid.PixelInCell;
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

    /**
     * Get defined grid geometry of create it from current informations.
     *
     * @return GridGeometry
     * @throws IncompleteGridGeometryException if informations insufficiant to create the grid
     */
    public GridGeometry getOrCreateGridGeometry() throws IncompleteGridGeometryException {
        if (gridGeometry != null) return gridGeometry;

        final Envelope envelope = getEnvelope();
        if (envelope == null) throw new IncompleteGridGeometryException("Canvas envelope is undefined.");
        final Dimension canvasDim = getDimension();
        if (canvasDim == null) throw new IncompleteGridGeometryException("Canvas dimension is undefined.");

        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        final int dimension = envelope.getDimension();

        final int x, y;
        final int east = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), AxisDirection.EAST);
        if (east < 0) {
            x = 0;
            y = 1;
        } else {
            final int north = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), AxisDirection.NORTH);
            x = Math.min(east, north);
            y = Math.max(east, north);
        }

        final long[] lowGrid = new long[dimension];
        final long[] highGrid = new long[lowGrid.length];
        Arrays.fill(highGrid, 1);
        highGrid[x] = canvasDim.width;
        highGrid[y] = canvasDim.height;
        final GridExtent displayExtent = new GridExtent(null, lowGrid, highGrid, false);
        final MatrixSIS gridToCrs = Matrices.createIdentity(dimension + 1);
        // init translations for each dimension
        for (int i = 0; i < dimension; i++) {
            gridToCrs.setElement(i, dimension, envelope.getMinimum(i));
        }
        // Initialize display scales, inverting y to represent image space where origin is upper-left.
        gridToCrs.setElement(x, x, envelope.getSpan(x) / canvasDim.width);
        gridToCrs.setElement(y, y, -envelope.getSpan(y) / canvasDim.height);
        // As y is inverted, its translation must also be reversed
        gridToCrs.setElement(y, dimension, envelope.getMaximum(y));
        GridGeometry displayGeom = new GridGeometry(
                displayExtent, PixelInCell.CELL_CORNER,
                MathTransforms.linear(gridToCrs),
                crs
        );

        if (!stretchImage) {
            displayGeom = AbstractCanvas2D.preserverRatio(displayGeom);
        }

        return displayGeom;
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
