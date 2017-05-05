/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.render2d.navigation;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AbstractMouseHandler implements EventHandler {

    @Override
        public void handle(Event event) {
            if(event instanceof MouseEvent){
                final MouseEvent me = (MouseEvent) event;
                if(MouseEvent.MOUSE_CLICKED.equals(me.getEventType())){
                    mouseClicked(me);
                }else if(MouseEvent.MOUSE_PRESSED.equals(me.getEventType())){
                    mousePressed(me);
                }else if(MouseEvent.MOUSE_RELEASED.equals(me.getEventType())){
                    mouseReleased(me);
                }else if(MouseEvent.MOUSE_EXITED.equals(me.getEventType())){
                    mouseExited(me);
                }else if(MouseEvent.MOUSE_DRAGGED.equals(me.getEventType())){
                    mouseDragged(me);
                }else if(MouseEvent.MOUSE_MOVED.equals(me.getEventType())){
                    mouseMoved(me);
                }else if(MouseEvent.MOUSE_ENTERED.equals(me.getEventType())){
                    mouseEntered(me);
                }

            }else if(event instanceof ScrollEvent){
                mouseWheelMoved((ScrollEvent) event);
            }
        }

    public void mouseClicked(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mouseDragged(MouseEvent me) {
    }

    public void mouseMoved(MouseEvent me) {
    }

    public void mouseWheelMoved(ScrollEvent scrollEvent) {
    }

    public void mouseEntered(MouseEvent me) {
    }

}
