/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel (Geomatys)
 */
public class SwingGeoComponent extends JComponent{

    private final J2DCanvasComponentAdapter canvas;
    
    public SwingGeoComponent(CoordinateReferenceSystem crs){
        canvas = new J2DCanvasComponentAdapter(crs,this);
    }
    
    public ReferencedCanvas2D getReferencedCanvas() {
        return canvas;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void paintComponent(final Graphics g) {        
        super.paintComponent(g);
        final Graphics copy = g.create();
        canvas.paint((Graphics2D) copy);
        copy.dispose();
    }
    
}
