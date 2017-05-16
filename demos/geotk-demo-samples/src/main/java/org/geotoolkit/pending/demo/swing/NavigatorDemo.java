
package org.geotoolkit.pending.demo.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputListener;
import org.geotoolkit.gui.swing.navigator.DateRenderer;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.temporal.object.TemporalConstants;

/**
 * Demonstration of the Navigation component.
 *
 * Navigator is similar to what could be found in project management libraries
 * to display Gant schemas.
 *
 */
public class NavigatorDemo {

    public static void main(String[] args) {

        final JNavigator guiNavigator = new JNavigator();
        guiNavigator.setModelRenderer(new DateRenderer());

        //add some data bands on the navigator
        for(int i=0;i<30;i++){
            guiNavigator.getBands().add(new TaskBand());
        }

        //move to current date
        guiNavigator.getModel().scale(1f/TemporalConstants.HOUR_MS, 0);
        guiNavigator.getModel().translate(-System.currentTimeMillis());


        //set a popup menu on the gradient area (at the bottom of the componant)
        final JPopupMenu menu = new JPopupMenu("basemenu");
        menu.add(new JMenuItem("base action 1"));
        menu.add(new JMenuItem("base action 2"));
        guiNavigator.setComponentPopupMenu(menu);


        final JFrame frm = new JFrame();
        frm.setSize(800, 600);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationRelativeTo(null);
        frm.setContentPane(guiNavigator);
        frm.setVisible(true);

    }

    /**
     * Band displaying a single red line
     */
    private static final class TaskBand extends JNavigatorBand implements MouseInputListener {


        private final int tolerance = 3;
        private double start;
        private double end;

        public TaskBand() {
            setPreferredSize(new Dimension(50, 30));

            start = System.currentTimeMillis() + Math.random() * TemporalConstants.MONTH_MS;
            end = start + TemporalConstants.MONTH_MS;

            //this band toolip
            setToolTipText(String.valueOf(Math.random()*1000));

            //this band popup actions
            final JPopupMenu menu = new JPopupMenu("");
            menu.add(new JMenuItem("action 1"));
            menu.add(new JMenuItem("action 2"));
            setComponentPopupMenu(menu);

            //listen to move events to drag the bar around
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        private boolean isOver(int position){
            final double pos1 = getModel().getGraphicValueAt(start) - tolerance;
            final double pos2 = getModel().getGraphicValueAt(end) + tolerance;
            return position>=pos1 && position <=pos2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            final Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            final int orientation = getNavigator().getOrientation();
            final boolean horizontal = (orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH);

            final float centered = horizontal ? getHeight() / 2 : getWidth() / 2;

            if (!horizontal) {
                //we apply a transform on eveyrthing we paint
                g2d.translate(getWidth(), 0);
                g2d.rotate(Math.toRadians(90));
            }

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            double start = getModel().getGraphicValueAt(this.start);
            double end = getModel().getGraphicValueAt(this.end);

            final Shape shape = new java.awt.geom.Line2D.Double(start, centered, end, centered);
            g2d.draw(shape);

        }

        ////////////////////////////////////////////////////////////////////////////
        // navigation events ///////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////

        private int lastMouseX = 0;
        private int lastMouseY = 0;
        private int newMouseX = 0;
        private int newMouseY = 0;
        private boolean flagMove = false;

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {

            if(isOver(e.getX())){
                flagMove = (e.getButton() == MouseEvent.BUTTON1);
                newMouseX = e.getX();
                newMouseY = e.getY();
                lastMouseX = newMouseX;
                lastMouseY = newMouseY;
                e.consume();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(flagMove) e.consume();
            flagMove = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(e.isConsumed()) return;

            newMouseX = e.getX();
            newMouseY = e.getY();

            if(flagMove){
                if(isOver(e.getX())){
                    //dragging task
                    final double scale = getModel().getScale();
                    final double tr = lastMouseX-newMouseX;
                    start -= 1/scale*tr;
                    end -= 1/scale*tr;
                    e.consume();
                    repaint();
                }
            }

            lastMouseX = newMouseX;
            lastMouseY = newMouseY;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }
}
