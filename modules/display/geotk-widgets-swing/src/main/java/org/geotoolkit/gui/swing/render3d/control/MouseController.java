/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.render3d.control;

import java.awt.event.*;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public class MouseController implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final TrackBallCamera camera;
    private MouseEvent mouseClickStart;

    public MouseController(TrackBallCamera camera){
        this.camera = camera;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1){
            mouseClickStart = e;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseClickStart = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (mouseClickStart == null){
            return;
        }

        if (mouseClickStart.getButton() == MouseEvent.BUTTON1){

            double scale = camera.getViewScale(camera.getLength());

            int moveX = mouseClickStart.getPoint().x - e.getPoint().x;
            int moveY = mouseClickStart.getPoint().y - e.getPoint().y;

            camera.moveRight((float)moveX*(float)scale);
            camera.moveBack((float)moveY*(float)scale);
            camera.updateCameraElevation();

            mouseClickStart.translatePoint(e.getPoint().x-mouseClickStart.getPoint().x, e.getPoint().y-mouseClickStart.getPoint().y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        float move = camera.getLength()*0.1f;

        if (Math.signum((double)e.getWheelRotation()) < 0.0){
            camera.zoomMore(move);
        } else if (Math.signum((double)e.getWheelRotation()) > 0.0){
            camera.zoomMore(-move);
        }
    }

}
