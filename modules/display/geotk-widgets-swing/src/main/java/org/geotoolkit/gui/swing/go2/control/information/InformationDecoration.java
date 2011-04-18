/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.control.information;

import org.geotoolkit.gui.swing.go2.control.information.presenter.InformationPresenter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;

/**
 * Infomation decoration
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class InformationDecoration extends JLayeredPane implements MapDecoration{

    private final JInternalFrame frame = new JInternalFrame();
    private final AbstractAction nextAction;
    private final AbstractAction previousAction;
    private final JLabel label = new JLabel("0/0");

    private JMap2D map = null;
    
    private List<? extends Object> selecteds = new ArrayList<Object>();
    private InformationPresenter presenter = null;
    private RenderingContext2D context = null;
    private SearchAreaJ2D area = null;
    private int selected = 0;


    
    public InformationDecoration(){
        setOpaque(false);

        //configure buttons ----------------------------------------------------

        nextAction = new AbstractAction(" > ") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedInfo(selected+1);
            }
        };

        previousAction = new AbstractAction(" < ") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedInfo(selected-1);
            }
        };

        GridBagConstraints cst = new GridBagConstraints();
        final JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new GridBagLayout());
        toolbar.setFloatable(false);
        cst.gridx = 0;
        toolbar.add(new JButton(previousAction),cst);
        cst.gridx = 1;
        toolbar.add(new JButton(nextAction),cst);
        cst.gridx = 2;
        cst.weightx = 1;
        toolbar.add(label,cst);
        cst.weightx = 0;
        cst.gridx=3;
        toolbar.add(new JButton(new AbstractAction("X") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        }),cst);
        toolbar.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        frame.setUI(new BasicInternalFrameUI(frame){
            @Override
            protected JComponent createNorthPane(JInternalFrame w) {
                return toolbar;
            }
            @Override
            protected JComponent createEastPane(JInternalFrame w) {
                return null;
            }
            @Override
            protected JComponent createWestPane(JInternalFrame w) {
                return null;
            }
            @Override
            protected JComponent createSouthPane(JInternalFrame w) {
                return null;
            }

        });

        frame.setClosable(true);
        frame.setResizable(true);
        frame.setIconifiable(false);
        frame.setFrameIcon(null);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        add(frame);

    }

    private void setSelectedInfo(final int index){
        selected = index;

        final Object candidate = selecteds.get(index);
        frame.setContentPane(presenter.createComponent(candidate,context,area));
        previousAction.setEnabled(index != 0);
        nextAction.setEnabled(index < (selecteds.size()-1));
        label.setText("  "+(selected+1) +"/"+selecteds.size()+"  ");
    }

    public void display(final List<? extends Object> selecteds, 
            final InformationPresenter presenter, final Point2D where, final RenderingContext2D context, final SearchAreaJ2D area){

        this.presenter = presenter;
        this.selecteds = selecteds;
        this.context = context;
        this.area = area;

        if(selecteds == null || where == null){
            frame.setVisible(false);
            return;
        }

        frame.setLocation((int)where.getX(),(int)where.getY());
        setSelectedInfo(0);
        frame.pack();
        final Dimension size = frame.getSize();
        if(size.width > 400) size.width = 400;
        if(size.height > 400) size.height = 400;
        frame.setSize(size);
        frame.setVisible(true);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void refresh() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMap2D(final JMap2D map) {
        this.map = map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JMap2D getMap2D() {
        return map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
