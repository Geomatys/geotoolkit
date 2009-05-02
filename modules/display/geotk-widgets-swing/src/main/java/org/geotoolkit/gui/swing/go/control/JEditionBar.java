/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.go.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.go.control.selection.LasoSelectionDecoration;
import org.geotoolkit.gui.swing.go.control.selection.LasoSelectionHandler;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JEditionBar extends JToolBar {

    private static final ImageIcon ICON_SELECT = IconBundle.getInstance().getIcon("16_select");

    private final ButtonGroup groupClip = new ButtonGroup();
    private final ButtonGroup groupZone = new ButtonGroup();

    private final JButton guiSelect = new JButton(ICON_SELECT);
    private final JToggleButton guiIntersect = new JToggleButton("I");
    private final JToggleButton guiWithin = new JToggleButton("W");
    private final JToggleButton guiLasso = new JToggleButton("L");
    private final JToggleButton guiSquare = new JToggleButton("S");

//    private final LasoSelectionDecoration deco = new LasoSelectionDecoration();
//    private final LasoSelectionHandler handler = new LasoSelectionHandler();

    private boolean installed = false;

    private final ActionListener listener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(map == null) return;

            map.setHandler(new LasoSelectionHandler(map));

            if(e.getSource() == guiSelect){
                if(installed){
                    map.setHandler(new LasoSelectionHandler(map));
//                    map.removeDecoration( deco);
                    installed = false;
                }else{
//                    map.addDecoration(10, deco);
                    installed = true;
                }
            }else{
                
            }
        }
    };

        
    private GoMap2D map = null;

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JEditionBar() {
        this(null);

    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JEditionBar(GoMap2D map) {
        setMap(map);

        guiIntersect.setSelected(true);
        groupClip.add(guiIntersect);
        groupClip.add(guiWithin);

        guiLasso.setSelected(true);
        groupZone.add(guiLasso);
        groupZone.add(guiSquare);

        guiSelect.addActionListener(listener);
        guiIntersect.addActionListener(listener);
        guiWithin.addActionListener(listener);
        guiLasso.addActionListener(listener);
        guiSquare.addActionListener(listener);

        add(guiSelect);
        add(guiIntersect);
        add(guiWithin);
        add(guiLasso);
        add(guiSquare);

    }

    
    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    public void setMap(GoMap2D map2d) {
        map = map2d;        
    }
}
