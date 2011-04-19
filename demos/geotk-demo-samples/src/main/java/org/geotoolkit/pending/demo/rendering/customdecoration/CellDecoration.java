

package org.geotoolkit.pending.demo.rendering.customdecoration;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotoolkit.display2d.canvas.DefaultRenderingContext2D;

import org.geotoolkit.gui.swing.go2.decoration.AbstractMapDecoration;
import org.opengis.geometry.BoundingBox;

public class CellDecoration extends AbstractMapDecoration{

    private double cellSize = 50;

    private final JPanel panel = new JPanel(){

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            final Graphics2D g2d = (Graphics2D) g;

            //we want to render as if we where on the canvas
            final DefaultRenderingContext2D context = new DefaultRenderingContext2D(map.getCanvas());
            map.getCanvas().prepareContext(context, g2d, g.getClip());


            final BoundingBox bbox = context.getCanvasObjectiveBounds2D();

            final double minx = bbox.getMinX();
            final double maxx = bbox.getMaxX();
            final double miny = bbox.getMinY();
            final double maxy = bbox.getMaxY();


            context.switchToObjectiveCRS();
            final java.awt.geom.Line2D.Double line = new java.awt.geom.Line2D.Double();

            g.setColor(Color.BLACK);
            for(double i=minx; i<maxx ;i+=cellSize){
                line.setLine(i, miny, i, maxy);
                g2d.draw(line);
            }
            for(double i=miny; i<maxy ;i+=cellSize){
                line.setLine(minx, i, maxx, i);
                g2d.draw(line);
            }

            context.switchToDisplayCRS();

        }

    };

    public CellDecoration(){

        final JLabel lbl = new JLabel("Cell width ");
        lbl.setOpaque(true);

        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(50, 1, 5000, 10));

        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                cellSize = ((Number)spinner.getValue()).floatValue();
                panel.repaint();
            }
        });

        panel.add(lbl);
        panel.add(spinner);
        panel.setOpaque(false);
    }

    @Override
    public void refresh() {
        cellSize = 50;
        panel.repaint();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

}
