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

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.navigation.MouseNavigatonListener;
import org.geotoolkit.gui.swing.resource.IconBundle;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public abstract class AbstractEditionDelegate extends MouseNavigatonListener implements MouseInputListener,KeyListener,MouseWheelListener{

    public static final Icon ICON_ADD = IconBundle.getIcon("16_add");
    public static final Icon ICON_MOVE = IconBundle.getIcon("16_move");
    public static final Icon ICON_DELETE = IconBundle.getIcon("16_delete");
    public static final Icon ICON_NODE_MOVE = IconBundle.getIcon("16_move_node");
    public static final Icon ICON_SUBPOINT_MOVE = IconBundle.getIcon("16_move_subpoint");
    public static final Icon ICON_SUBLINE_MOVE = IconBundle.getIcon("16_move_subline");
    public static final Icon ICON_SUBPOLYGON_MOVE = IconBundle.getIcon("16_move_subpolygon");
    public static final Icon ICON_NODE_ADD = IconBundle.getIcon("16_add_node");
    public static final Icon ICON_SUBPOINT_ADD = IconBundle.getIcon("16_add_subpoint");
    public static final Icon ICON_SUBLINE_ADD = IconBundle.getIcon("16_add_subline");
    public static final Icon ICON_SUBPOLYGON_ADD = IconBundle.getIcon("16_add_subpolygon");
    public static final Icon ICON_NODE_DELETE = IconBundle.getIcon("16_remove_node");
    public static final Icon ICON_SUBPOINT_DELETE = IconBundle.getIcon("16_remove_subpoint");
    public static final Icon ICON_SUBLINE_DELETE = IconBundle.getIcon("16_remove_subline");
    public static final Icon ICON_SUBPOLYGON_DELETE = IconBundle.getIcon("16_remove_subpolygon");

    protected final DefaultEditionHandler handler;
    private JMap2D map;

    protected AbstractEditionDelegate(final DefaultEditionHandler handler){
        super(null);
        ensureNonNull("handler", handler);
        this.handler = handler;
        setPanButton(MouseEvent.BUTTON2);
    }

    @Override
    public final void setMap(final JMap2D map) {
        super.setMap(map);
        this.map = map;
    }

    @Override
    public final JMap2D getMap() {
        return map;
    }

    public abstract void initialize();


    @Override
    public void mouseClicked(final MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        super.mouseEntered(e);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        super.mouseExited(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        super.mouseMoved(e);
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyPressed(final KeyEvent e) {
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        super.mouseWheelMoved(e);
    }
    
}
