/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Johann Sorel
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
package org.geotoolkit.gui.swing.go3;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.gui.swing.go3.control.JCoordinateBar;
import org.geotoolkit.gui.swing.go3.control.JNavigationBar;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;

/**
 * Extend the JMap2DFrame with a 3D view in an additional tab.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JMap3DFrame extends JMap2DFrame{
    
    protected JMap3DFrame(final MapContext context, Hints hints){
        super(context,hints);
        
        //add the 3d tab
        final JPanel pan3d = new JPanel(new BorderLayout());
        
        final JNavigationBar guiNavBar = new JNavigationBar();
        final JCoordinateBar guiCoordBar = new JCoordinateBar();
        guiNavBar.setFloatable(false);
        final A3DCanvas canvas = new A3DCanvas(null);
        guiNavBar.setCanvas(canvas);
        guiCoordBar.setCanvas(canvas);
        canvas.getA3DContainer().setContext(context);
        
        pan3d.add(BorderLayout.NORTH,guiNavBar);
        pan3d.add(BorderLayout.CENTER,canvas.getComponent());
        pan3d.add(BorderLayout.SOUTH,guiCoordBar);
        
        panTabs.add("3D", pan3d);
    }
    
    public static void show(MapContext context){
        if(context == null) context = MapBuilder.createContext();
        show(context,null);
    }
    
    public static void show(final MapContext context, final Hints hints){

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JMap3DFrame(context,hints).setVisible(true);
            }
        });
    }
    
}
