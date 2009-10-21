/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.decoration;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.DefaultRenderingContext2D;
import org.geotoolkit.display2d.primitive.DefaultProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;

import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.operation.TransformException;

/**
 * Abstract Decoration to easely render objective CRS geometries.
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public abstract class AbstractGeometryDecoration extends JPanel implements MapDecoration{

    private static final Logger LOGGER = Logging.getLogger(AbstractGeometryDecoration.class);

    protected final List<Geometry> geometries = new ArrayList<Geometry>();
    private DefaultRenderingContext2D context = null;
    private AffineTransform objToDisp = null;
    protected Map2D map = null;

    protected AbstractGeometryDecoration(){
        setOpaque(false);
    }

    protected double[] toDisplay(Coordinate coord){
        double[] coords = new double[]{coord.x,coord.y};
        objToDisp.transform(coords, 0, coords, 0, 1);
        return coords;
    }

    public void setGeometries(Collection<Geometry> geoms){
        geometries.clear();

        if(geoms != null){
            geometries.addAll(geoms);
        }
        repaint();
    }

    public List<Geometry> getGeometries(){
        return new ArrayList<Geometry>(geometries);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setMap2D(Map2D map) {
        this.map = map;

        if(map != null && map.getCanvas() instanceof J2DCanvas){
            context = new DefaultRenderingContext2D(map.getCanvas());
        }else{
            context = null;
        }

    }

    @Override
    public Map2D getMap2D() {
        return map;
    }

    @Override
    public JComponent geComponent() {
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //enable anti-aliasing
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //check if the map has a Java2D canvas
        if(map == null) return;
        final ReferencedCanvas2D candidate = map.getCanvas();
        if(!(candidate instanceof J2DCanvas)) return;

        final J2DCanvas canvas = (J2DCanvas) candidate;
        canvas.prepareContext(context,(Graphics2D) g.create(), null);
        objToDisp = context.getObjectiveToDisplay();

        if (objToDisp == null) return;

        final Graphics2D g2 = context.getGraphics();

        //prepare datas for geometry painting
        for(final Geometry geo : geometries){
            final DefaultProjectedGeometry projected = new DefaultProjectedGeometry(geo);
            projected.setObjToDisplay(new AffineTransform2D(objToDisp));
            try {
                paintGeometry(g2,context, projected);
            } catch (TransformException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

    }
    
    protected abstract void paintGeometry(Graphics2D g2, RenderingContext2D context, ProjectedGeometry geom) throws TransformException;


}
