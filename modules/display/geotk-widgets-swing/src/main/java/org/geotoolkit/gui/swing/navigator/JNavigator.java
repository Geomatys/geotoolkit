/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.gui.swing.navigator;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.*;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.util.collection.NotifiedCheckedList;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JNavigator extends JPanel implements
        MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private final NavigatorModel model = new DoubleNavigatorModel(null);
    private NavigatorRenderer renderer;
    private final JComponent graduation = new JComponent(){};
    private final JPanel bandsPan = new JPanel();
    private final List<JNavigatorBand> bands = new NotifiedCheckedList<JNavigatorBand>(JNavigatorBand.class){

        @Override
        protected void notifyAdd(JNavigatorBand band, int index) {
            band.setModel(getModel());
            band.setNavigator(JNavigator.this);
            band.addMouseListener(JNavigator.this);
            band.addMouseMotionListener(JNavigator.this);
            band.addMouseWheelListener(JNavigator.this);
            band.addKeyListener(JNavigator.this);
            updateDisplay();
        }

        @Override
        protected void notifyAdd(Collection<? extends JNavigatorBand> items, NumberRange<Integer> range) {
            for(JNavigatorBand band : items){
                band.setModel(getModel());
                band.setNavigator(JNavigator.this);
                band.addMouseListener(JNavigator.this);
                band.addMouseMotionListener(JNavigator.this);
                band.addMouseWheelListener(JNavigator.this);
                band.addKeyListener(JNavigator.this);
            }
            updateDisplay();
        }

        @Override
        protected void notifyRemove(JNavigatorBand band, int index) {
            band.removeMouseListener(JNavigator.this);
            band.removeMouseMotionListener(JNavigator.this);
            band.removeMouseWheelListener(JNavigator.this);
            band.removeKeyListener(JNavigator.this);
            updateDisplay();
        }

        @Override
        protected void notifyRemove(Collection<? extends JNavigatorBand> items, NumberRange<Integer> range) {
            for(JNavigatorBand band : items){
                band.removeMouseListener(JNavigator.this);
                band.removeMouseMotionListener(JNavigator.this);
                band.removeMouseWheelListener(JNavigator.this);
                band.removeKeyListener(JNavigator.this);
            }
            updateDisplay();
        }

        @Override
        protected void notifyChange(JNavigatorBand oldItem, JNavigatorBand newItem, int index) {
            if(oldItem != null){
                oldItem.removeMouseListener(JNavigator.this);
                oldItem.removeMouseMotionListener(JNavigator.this);
                oldItem.removeMouseWheelListener(JNavigator.this);
                oldItem.removeKeyListener(JNavigator.this);
            }
            if(newItem != null){
                newItem.setModel(getModel());
                newItem.setNavigator(JNavigator.this);
                newItem.addMouseListener(JNavigator.this);
                newItem.addMouseMotionListener(JNavigator.this);
                newItem.addMouseWheelListener(JNavigator.this);
                newItem.addKeyListener(JNavigator.this);
            }
            updateDisplay();
        }
        
    };

    private int orientation = SwingConstants.SOUTH;

    private final PropertyChangeListener listener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            revalidate();
            repaint();
        }
    };

    public JNavigator() {
        super(new BorderLayout(0,0));
        renderer = new DoubleRenderer();
        bandsPan.setOpaque(false);
        bandsPan.setInheritsPopupMenu(true);
        graduation.setInheritsPopupMenu(true);

        final BoxLayout bl = new BoxLayout(bandsPan, BoxLayout.Y_AXIS);        
        bandsPan.setLayout(bl);
                
        model.addPropertyChangeListener(listener);
        this.graduation.setOpaque(false);
        add(BorderLayout.SOUTH,graduation);
        
        final JScrollPane scroller = new JScrollPane(bandsPan);
        scroller.setOpaque(false);
        scroller.setInheritsPopupMenu(true);
        scroller.setBorder(null);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getViewport().setBorder(null);
        scroller.getViewport().setOpaque(false);      
        scroller.getViewport().setInheritsPopupMenu(true);  
        add(BorderLayout.CENTER,scroller);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        graduation.addMouseListener(this);
        graduation.addMouseMotionListener(this);
        graduation.addMouseWheelListener(this);
        graduation.addKeyListener(this);
        bandsPan.addMouseListener(this);
        bandsPan.addMouseMotionListener(this);
        bandsPan.addMouseWheelListener(this);
        bandsPan.addKeyListener(this);
    }

    @Override
    public void setComponentPopupMenu(final JPopupMenu popup) {
        super.setComponentPopupMenu(popup);
        graduation.setComponentPopupMenu(popup);
    }

    private void updateDisplay(){
        bandsPan.removeAll();
        
        for(JNavigatorBand band : bands){
            bandsPan.add(band);
        }
        
        bandsPan.revalidate();
        bandsPan.repaint();
    }
        
    /**
     * Nodifiable list of bands to display.
     */
    public List<JNavigatorBand> getBands(){
        return bands;
    }

    public NavigatorModel getModel() {
        return model;
    }

    public NavigatorRenderer getModelRenderer() {
        return renderer;
    }

    public void setModelRenderer(final NavigatorRenderer renderer) {
        this.renderer = renderer;

        if(this.renderer != null){
            graduation.setPreferredSize(new Dimension(this.renderer.getGraduationHeight(), this.renderer.getGraduationHeight()));
            revalidate();
            repaint();
        }
    }

    public void setOrientation(final int orientation) {
        
        if(this.orientation != orientation){
            this.orientation = orientation;
            //change the order
            removeAll();
            bandsPan.removeAll();
            if(orientation == NORTH){
                add(BorderLayout.NORTH,graduation);
                bandsPan.setLayout(new GridLayout(0, 1));
            }else if(orientation == SOUTH){
                add(BorderLayout.SOUTH,graduation);
                bandsPan.setLayout(new GridLayout(0, 1));
            }else if(orientation == EAST){
                add(BorderLayout.EAST,graduation);
                bandsPan.setLayout(new GridLayout(1, 0));
            }else if(orientation == WEST){
                add(BorderLayout.WEST,graduation);
                bandsPan.setLayout(new GridLayout(1, 0));
            }else{
                throw new IllegalArgumentException("Orientation doesnt have a valid value :" + orientation);
            }

            add(BorderLayout.CENTER, bandsPan);
            for(JNavigatorBand band : bands){
                bandsPan.add(band);
            }

            bandsPan.revalidate();
            revalidate();
            repaint();
        }

    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    protected void paintComponent(final Graphics grphcs) {
        super.paintComponent(grphcs);
        final Graphics2D g = (Graphics2D) grphcs;
        if(renderer != null){
            renderer.render(this, g, new Rectangle(getWidth(), getHeight()));
        }
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
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if(e.isConsumed()) return;
        flagMove = (e.getButton() == MouseEvent.BUTTON1);

        newMouseX = e.getX();
        newMouseY = e.getY();

        lastMouseX = newMouseX;
        lastMouseY = newMouseY;
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if(e.isConsumed()) return;
        flagMove = false;
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        requestFocus();
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if(e.isConsumed()) return;

        if(!flagMove) return;

        newMouseX = e.getX();
        newMouseY = e.getY();

        final double tr;
        switch(orientation){
            case NORTH : tr = lastMouseX-newMouseX; break;
            case SOUTH : tr = lastMouseX-newMouseX; break;
            case EAST : tr = lastMouseY-newMouseY; break;
            default : tr = lastMouseY-newMouseY; break;
        }
        final double scale = getModel().getScale();
        getModel().translate(-tr/scale);

        lastMouseX = newMouseX;
        lastMouseY = newMouseY;
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setToolTipText(null);
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        final int x = getPosition(e);

        if (e.getWheelRotation() > 0) {
            getModel().scale(1.1d, x);
        } else {
            getModel().scale(0.9d, x);
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        final int code = e.getKeyCode();
        final int x = getPosition(null);
        final double scale = getModel().getScale();

        final int speed = 3;
        switch(orientation){
            case NORTH : 
                switch(code){
                    case VK_RIGHT : getModel().translate(-speed/scale);break;
                    case VK_LEFT :  getModel().translate(speed/scale);break;
                    case VK_UP :    getModel().scale(0.9d, x);break;
                    case VK_DOWN : getModel().scale(1.1d, x);break;
                } break;
            case SOUTH : 
                switch(code){
                    case VK_RIGHT : getModel().translate(-speed/scale);break;
                    case VK_LEFT :  getModel().translate(speed/scale);break;
                    case VK_UP :    getModel().scale(1.1d, x);break;
                    case VK_DOWN : getModel().scale(0.9d, x);break;
                } break;
            case EAST :
                switch(code){
                    case VK_RIGHT : getModel().scale(0.9d, x);break;
                    case VK_LEFT :  getModel().scale(1.1d, x);break;
                    case VK_UP :    getModel().translate(-speed/scale);break;
                    case VK_DOWN : getModel().translate(speed/scale);break;
                } break;
            case WEST :
                switch(code){
                    case VK_RIGHT : getModel().scale(1.1d, x);break;
                    case VK_LEFT :  getModel().scale(0.9d, x);break;
                    case VK_UP :    getModel().translate(-speed/scale);break;
                    case VK_DOWN : getModel().translate(speed/scale);break;
                } break;
        }


    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    /**
     * used to define the scaling center.
     */
    private int getPosition(final MouseEvent e){
        if(e != null){
            switch(orientation){
                case NORTH : return e.getX();
                case SOUTH : return e.getX();
                case EAST : return e.getY();
                default : return e.getY();
            }
        }else{
            switch(orientation){
                case NORTH : return getWidth()/2; 
                case SOUTH : return getWidth()/2;
                case EAST : return getHeight()/2;
                default : return getHeight()/2;
            }
        }
    }

}
