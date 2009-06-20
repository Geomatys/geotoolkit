/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.map.map2d.decoration.MapDecoration;

/**
 * Infomation decoration
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class InformationDecoration extends JComponent implements MapDecoration{

    private final JPanel infoPane = new JPanel(new BorderLayout());
    private final JEditorPane textPane = new JEditorPane();
    private final JButton closeButton;
    private final JButton nextButton;
    private final JButton previousButton;
    
    private Map2D map = null;
    
    private String[] texts = new String[0];
    private int selected = 0;
    private Point2D point = null;
    
    public InformationDecoration(){
        setLayout(null);

        //configure buttons ----------------------------------------------------

        closeButton = new JButton(new AbstractAction("", IconBundle.getInstance().getIcon("16_delete")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawText(null, null);
            }
        });
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);

        nextButton = new JButton(new AbstractAction("",IconBundle.getInstance().getIcon("16_vertical_next")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedInfo(selected+1);
            }
        });
        nextButton.setDisabledIcon(IconBundle.EMPTY_ICON_16);
        nextButton.setMargin(new Insets(0, 0, 0, 0));
        nextButton.setBorderPainted(false);
        nextButton.setContentAreaFilled(false);

        previousButton = new JButton(new AbstractAction("",IconBundle.getInstance().getIcon("16_vertical_previous")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedInfo(selected-1);
            }
        });
        previousButton.setDisabledIcon(IconBundle.EMPTY_ICON_16);
        previousButton.setMargin(new Insets(0, 0, 0, 0));
        previousButton.setBorderPainted(false);
        previousButton.setContentAreaFilled(false);

        final JPanel top = new JPanel(new GridLayout(3,1,0,0));
        top.setOpaque(false);
        top.add(closeButton);
        top.add(previousButton);
        top.add(nextButton);

        final JPanel border = new JPanel(new BorderLayout(1,1));
        border.setOpaque(true);
        border.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        border.setBackground(Color.WHITE);
        border.setOpaque(true);
        border.add(BorderLayout.NORTH,top);
        
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setMargin(new Insets(0, 2, 2, 4));
        final JScrollPane scroll = new JScrollPane(textPane);
        scroll.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.setBackground(new Color(0f,0f,0f,0f));
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBorder(null);
        
        infoPane.setOpaque(true);
        infoPane.setBackground(new Color(1f,1f,1f,0.9f));
        infoPane.add(BorderLayout.WEST,border);
        infoPane.add(BorderLayout.CENTER,scroll);
        setOpaque(false);
    }

    private void setSelectedInfo(final int index){
        selected = index;

        String text = texts[index];

        textPane.setText(text);
        infoPane.setSize(300, 250);
//        infoPane.setSize(infoPane.getPreferredSize());

        int x = (int)point.getX();
        int y = (int)point.getY();

        if(x+infoPane.getWidth() > getWidth()){
            x = getWidth()-infoPane.getWidth();
        }
        if(y+infoPane.getHeight() > getHeight()){
            y = getHeight()-infoPane.getHeight();
        }

        if(index != 0){
            previousButton.setEnabled(true);
            previousButton.setToolTipText(selected +"/"+texts.length);
        }else{
            previousButton.setEnabled(false);
            previousButton.setToolTipText(null);
        }

        if(index < (texts.length-1)){
            nextButton.setEnabled(true);
            nextButton.setToolTipText(selected+2 +"/"+texts.length);
        }else{
            nextButton.setEnabled(false);
            nextButton.setToolTipText(null);
        }
        
        infoPane.setLocation(x,y);
        add(infoPane);
        revalidate();
        repaint();

    }

    public void drawText(String[] texts, Point2D where){
        this.texts = texts;
        this.point = where;
                
        if(texts != null || where != null){
            setSelectedInfo(0);
        }else{
            texts = new String[0];
            remove(infoPane);
            revalidate();
            repaint();
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void refresh() {
        repaint();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JComponent geComponent() {
        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMap2D(Map2D map) {
        this.map = map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map2D getMap2D() {
        return map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
