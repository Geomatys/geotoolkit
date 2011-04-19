/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.go2.control.edition;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputListener;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.gui.swing.go2.CanvasHandler;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.geotoolkit.util.ArgumentChecks;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class EditionHandler implements CanvasHandler, MouseInputListener,KeyListener,MouseWheelListener{

    private final JMap2D map;
    private final EditionDelegate delegate;
    private final MapDecoration delegateDecoration;

    public EditionHandler(final JMap2D map,final EditionDelegate delegate) {
        ArgumentChecks.ensureNonNull("map", map);
        ArgumentChecks.ensureNonNull("delegate", delegate);
        this.map = map;
        this.delegate = delegate;
        this.delegateDecoration = delegate.getDecoration();
    }

    @Override
    public J2DCanvas getCanvas() {
        return map.getCanvas();
    }

    @Override
    public void install(final Component component) {
        map.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        if(delegateDecoration != null){
            map.addDecoration(0,delegateDecoration);
        }
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
        component.addKeyListener(this);
    }

    @Override
    public void uninstall(final Component component) {
        map.setCursor(Cursor.getDefaultCursor());
        if(delegateDecoration != null){
            map.removeDecoration(delegateDecoration);
        }
        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
        component.removeMouseWheelListener(this);
        component.removeKeyListener(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    //forward events to the delegate ///////////////////////////////////////////
    
    @Override
    public void mouseClicked(final MouseEvent e) {
        //handle grid and geometry snapping
        delegate.mouseClicked(e);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        //handle grid and geometry snapping
        delegate.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        //handle grid and geometry snapping
        delegate.mouseReleased(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        map.getComponent().requestFocus();
        //handle grid and geometry snapping
        delegate.mouseEntered(e);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        //handle grid and geometry snapping
        delegate.mouseExited(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        //handle grid and geometry snapping
        delegate.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        //handle grid and geometry snapping
        delegate.mouseMoved(e);
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        //handle grid and geometry snapping
        delegate.mouseWheelMoved(e);
    }
        
    @Override
    public void keyTyped(final KeyEvent e) {
        delegate.keyTyped(e);
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        delegate.keyPressed(e);
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        delegate.keyReleased(e);
    }

}
