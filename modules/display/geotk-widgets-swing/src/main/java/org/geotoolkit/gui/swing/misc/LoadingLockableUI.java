/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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
package org.geotoolkit.gui.swing.misc;

import com.jhlabs.image.AverageFilter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;
import javax.swing.Timer;

import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.swingx.painter.BusyPainter;

/**
 * Simple lock ui with effects. eye candys.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class LoadingLockableUI extends LockableUI implements ActionListener {

    private final BusyPainter busyPainter = createDefaultBusyPainter();
    private final Timer timer = new Timer(150, this);
    private int frameNumber;

    public LoadingLockableUI() {
        setLockedEffects(new BufferedImageOpEffect(new AverageFilter()));
    }

    @Override
    public void paint(Graphics g, JComponent comp) {
        super.paint(g, comp);
        if (isLocked()) {
            busyPainter.paint((Graphics2D)g, comp, comp.getWidth(), comp.getHeight());
        }
    }

    @Override
    public void setLocked(boolean isLocked) {
        super.setLocked(isLocked);
        if (isLocked) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frameNumber = (frameNumber + 1) % 8;
        busyPainter.setFrame(frameNumber);
        setDirty(true);
    }

    public static BusyPainter createDefaultBusyPainter(){
        final BusyPainter busyPainter = new BusyPainter();
        busyPainter.setPaintCentered(true);
        busyPainter.setPointShape(new Ellipse2D.Double(0, 0, 16, 16));
        busyPainter.setTrajectory(new Ellipse2D.Double(0, 0, 80, 80));
        return busyPainter;
    }

}
