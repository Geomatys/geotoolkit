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
package org.geotoolkit.gui.swing.go2.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.control.selection.DefaultSelectionHandler;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JSelectionBar extends JToolBar implements MapControlBar{

    private static final ImageIcon ICON_SELECT = IconBundle.getInstance().getIcon("16_select");
    private static final ImageIcon ICON_CONFIG = IconBundle.getInstance().getIcon("16_vertical_next");
    private static final ImageIcon ICON_INTERSECT = IconBundle.getInstance().getIcon("16_select_intersect");
    private static final ImageIcon ICON_WITHIN = IconBundle.getInstance().getIcon("16_select_within");
    private static final ImageIcon ICON_LASSO = IconBundle.getInstance().getIcon("16_select_lasso");
    private static final ImageIcon ICON_SQUARE = IconBundle.getInstance().getIcon("16_select_square");
    private static final ImageIcon ICON_GEOGRAPHIC = IconBundle.getInstance().getIcon("16_zoom_all");
    private static final ImageIcon ICON_VISUAL = IconBundle.getInstance().getIcon("16_visible");

    private final ButtonGroup groupClip = new ButtonGroup();
    private final ButtonGroup groupZone = new ButtonGroup();
    private final ButtonGroup groupVisit = new ButtonGroup();

    private final JButton guiSelect = new JButton(ICON_SELECT);
    private final JButton guiConfig = new JButton(ICON_CONFIG);
    private final JRadioButtonMenuItem guiIntersect = new JRadioButtonMenuItem(MessageBundle.getString("select_intersect"),ICON_INTERSECT);
    private final JRadioButtonMenuItem guiWithin = new JRadioButtonMenuItem(MessageBundle.getString("select_within"),ICON_WITHIN);
    private final JRadioButtonMenuItem guiLasso = new JRadioButtonMenuItem(MessageBundle.getString("select_lasso"),ICON_LASSO);
    private final JRadioButtonMenuItem guiSquare = new JRadioButtonMenuItem(MessageBundle.getString("select_square"),ICON_SQUARE);
    private final JRadioButtonMenuItem guiGeographic = new JRadioButtonMenuItem(MessageBundle.getString("select_geographic"),ICON_GEOGRAPHIC);
    private final JRadioButtonMenuItem guiVisual = new JRadioButtonMenuItem(MessageBundle.getString("select_visual"),ICON_VISUAL);

    private final DefaultSelectionHandler handler = new DefaultSelectionHandler();

    private final ActionListener listener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(map == null) return;

            handler.setMap(map);
            handler.setGeographicArea(guiGeographic.isSelected());
            handler.setSquareArea(guiSquare.isSelected());
            handler.setWithinArea(guiWithin.isSelected());

            map.setHandler(handler);
        }
    };

        
    private Map2D map = null;

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JSelectionBar() {
        this(null);

        guiSelect.setToolTipText(MessageBundle.getString("map_select"));
        guiConfig.setToolTipText(MessageBundle.getString("map_select_config"));
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JSelectionBar(Map2D map) {
        setMap(map);

        final JPopupMenu menu = new JPopupMenu();
        menu.add(guiLasso);
        menu.add(guiSquare);
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(guiIntersect);
        menu.add(guiWithin);
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(guiGeographic);
        menu.add(guiVisual);

        guiConfig.setComponentPopupMenu(menu);
        guiConfig.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON1){
                    menu.show(guiConfig, event.getX(), event.getY());
                }
            }
            @Override
            public void mousePressed(MouseEvent arg0) {}
            @Override
            public void mouseReleased(MouseEvent arg0) {}
            @Override
            public void mouseEntered(MouseEvent arg0) {}
            @Override
            public void mouseExited(MouseEvent arg0) {}
        });

        guiIntersect.setSelected(true);
        groupClip.add(guiIntersect);
        groupClip.add(guiWithin);

        guiSquare.setSelected(true);
        groupZone.add(guiLasso);
        groupZone.add(guiSquare);

        guiVisual.setSelected(true);
        groupVisit.add(guiVisual);
        groupVisit.add(guiGeographic);

        guiSelect.addActionListener(listener);
        guiIntersect.addActionListener(listener);
        guiWithin.addActionListener(listener);
        guiLasso.addActionListener(listener);
        guiSquare.addActionListener(listener);
        guiGeographic.addActionListener(listener);
        guiVisual.addActionListener(listener);

        add(guiSelect);
        add(guiConfig);

    }

    @Override
    public void setMap(Map2D map2d) {
        map = map2d;

        if(map != null){
            guiSelect.setEnabled(true);
            guiSelect.setEnabled(true);
        }else{
            guiSelect.setEnabled(false);
            guiSelect.setEnabled(false);
        }

    }

    @Override
    public Map2D getMap() {
        return map;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
