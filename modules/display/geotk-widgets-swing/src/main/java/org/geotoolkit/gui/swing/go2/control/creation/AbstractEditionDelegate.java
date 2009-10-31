/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.control.creation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.Icon;
import javax.swing.event.MouseInputListener;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.control.navigation.MouseNavigatonListener;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public abstract class AbstractEditionDelegate extends MouseNavigatonListener implements MouseInputListener,KeyListener,MouseWheelListener{

    public static final Icon ICON_ADD = IconBundle.getInstance().getIcon("16_add");
    public static final Icon ICON_MOVE = IconBundle.getInstance().getIcon("16_move");
    public static final Icon ICON_DELETE = IconBundle.getInstance().getIcon("16_delete");
    public static final Icon ICON_NODE_MOVE = IconBundle.getInstance().getIcon("16_move_node");
    public static final Icon ICON_SUBPOINT_MOVE = IconBundle.getInstance().getIcon("16_move_subpoint");
    public static final Icon ICON_SUBLINE_MOVE = IconBundle.getInstance().getIcon("16_move_subline");
    public static final Icon ICON_SUBPOLYGON_MOVE = IconBundle.getInstance().getIcon("16_move_subpolygon");
    public static final Icon ICON_NODE_ADD = IconBundle.getInstance().getIcon("16_add_node");
    public static final Icon ICON_SUBPOINT_ADD = IconBundle.getInstance().getIcon("16_add_subpoint");
    public static final Icon ICON_SUBLINE_ADD = IconBundle.getInstance().getIcon("16_add_subline");
    public static final Icon ICON_SUBPOLYGON_ADD = IconBundle.getInstance().getIcon("16_add_subpolygon");
    public static final Icon ICON_NODE_DELETE = IconBundle.getInstance().getIcon("16_remove_node");
    public static final Icon ICON_SUBPOINT_DELETE = IconBundle.getInstance().getIcon("16_remove_subpoint");
    public static final Icon ICON_SUBLINE_DELETE = IconBundle.getInstance().getIcon("16_remove_subline");
    public static final Icon ICON_SUBPOLYGON_DELETE = IconBundle.getInstance().getIcon("16_remove_subpolygon");

    protected final DefaultEditionHandler handler;
    private Map2D map;

    protected AbstractEditionDelegate(DefaultEditionHandler handler){
        super(null);
        if(handler == null){
            throw new NullPointerException("Handler should not be null");
        }
        this.handler = handler;
        setPanButton(MouseEvent.BUTTON2);
    }

    @Override
    public final void setMap(Map2D map) {
        super.setMap(map);
        this.map = map;
    }

    @Override
    public final Map2D getMap() {
        return map;
    }

    public abstract void initialize();


    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
    }
    
}
