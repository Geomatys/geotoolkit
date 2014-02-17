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
package org.geotoolkit.display3d.scene.quadtree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.vecmath.Point3i;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JQuadView extends JDialog {

    private static final Color STROKE_COLOR = new Color(0, 0, 0, 1f);
    private static final Color FILL_COLOR_NOTILE = new Color(0.8f, 0.2f, 0.2f, 0.2f);
    private static final Color FILL_COLOR_YESTILE = new Color(0.2f, 0.2f, 0.8f, 0.2f);
    private static final Color COLOR_LOADED = new Color(0f, 0f, 1f, 1f);
    private static final Color COLOR_NOTLOADED = new Color(1f, 0f, 0f, 1f);
    private static final int SIZE = 6;

    private final List<QuadTreeNode> nodes = new CopyOnWriteArrayList<>();

    public JQuadView() {
        final JView view = new JView();
        setContentPane(view);
        setModal(false);
        setAlwaysOnTop(true);
        setSize(640, 640);

        final Timer timer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.repaint();
            }
        });
        timer.start();
        setVisible(true);
    }

    public void setNodes(List<QuadTreeNode> nodes){
        this.nodes.clear();
        this.nodes.addAll(nodes);
    }

    private class JView extends JPanel{

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final QuadTreeNode[] quads = nodes.toArray(new QuadTreeNode[0]);

            final double width = getWidth();
            final Graphics2D g2d = (Graphics2D) g;
            for(QuadTreeNode quad : quads){
                final Point3i coord = quad.getPosition();
                final double quadwidth = width / Math.pow(2,coord.z);
                final double x = coord.x*quadwidth;
                final double y = coord.y*quadwidth;

                if(!quad.isData()){
                    g2d.setColor(FILL_COLOR_NOTILE);
                    g2d.fillRect((int)x, (int)y, (int)quadwidth, (int)quadwidth);
                }else{
                    g2d.setColor(FILL_COLOR_YESTILE);
                    g2d.fillRect((int)x, (int)y, (int)quadwidth, (int)quadwidth);

                    final boolean imageLoaded = quad.isDataImageLoaded();
                    g2d.setColor(imageLoaded ? COLOR_LOADED : COLOR_NOTLOADED);
                    g2d.fillRect((int)x, (int)y, SIZE, SIZE);

                    final boolean mntLoaded = quad.isDataMNTLoaded();
                    g2d.setColor(mntLoaded ? COLOR_LOADED : COLOR_NOTLOADED);
                    g2d.fillRect((int)x+SIZE, (int)y, SIZE, SIZE);
                }

                g2d.setColor(STROKE_COLOR);
                g2d.drawRect((int)x, (int)y, (int)quadwidth, (int)quadwidth);

            }
        }
    }

}
