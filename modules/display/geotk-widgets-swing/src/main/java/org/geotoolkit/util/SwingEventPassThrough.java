/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011 Geomatys
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
package org.geotoolkit.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SwingEventPassThrough implements MouseInputListener,MouseWheelListener{

    private final Component source;
    
    public SwingEventPassThrough(final Component source){
        ArgumentChecks.ensureNonNull("source", source);
        this.source = source;
        source.addMouseListener(this);
        source.addMouseMotionListener(this);
        source.addMouseWheelListener(this);
    }
    
    private void forwardMouseEvent(final MouseEvent event){
        final Component destination = source.getParent();
        if (destination != null) {
            destination.dispatchEvent(SwingUtilities.convertMouseEvent((Component)event.getSource(), event, destination));
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        forwardMouseEvent(e);
    }
    
}
