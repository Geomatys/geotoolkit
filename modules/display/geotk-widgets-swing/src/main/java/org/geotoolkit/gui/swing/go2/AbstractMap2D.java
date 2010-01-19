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
package org.geotoolkit.gui.swing.go2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.geotoolkit.gui.swing.BufferLayout;
import org.geotoolkit.gui.swing.go2.decoration.DefaultInformationDecoration;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.geotoolkit.gui.swing.go2.decoration.ColorDecoration;
import org.geotoolkit.gui.swing.go2.decoration.InformationDecoration;

/**
 * Default implementation of Map2D
 * 
 * @author Johann Sorel
 * @module pending
 */
public abstract class AbstractMap2D extends JPanel implements Map2D {
    
    private static final MapDecoration[] EMPTY_OVERLAYER_ARRAY = {};

    private final List<MapDecoration> userDecorations = new ArrayList<MapDecoration>();
    
    private final JLayeredPane mapDecorationPane = new JLayeredPane();
    private final JLayeredPane userDecorationPane = new JLayeredPane();
    private final JLayeredPane mainDecorationPane = new JLayeredPane();
    private int nextMapDecorationIndex = 1;
    private InformationDecoration informationDecoration = new DefaultInformationDecoration();
    private MapDecoration backDecoration = new ColorDecoration();
    
    private Component mapComponent =  null;

    /**
     * create a default JDefaultMap2D
     */
    public AbstractMap2D() {
        init();        
    }
    
    private void init(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(150,150));
        mapDecorationPane.setLayout(new BufferLayout());
        userDecorationPane.setLayout(new BufferLayout());
        mainDecorationPane.setLayout(new BufferLayout());

        mainDecorationPane.add(informationDecoration.geComponent(), Integer.valueOf(3));
        mainDecorationPane.add(userDecorationPane, Integer.valueOf(2));
        mainDecorationPane.add(mapDecorationPane, Integer.valueOf(1));

        informationDecoration.setMap2D(this);
        
        add(BorderLayout.CENTER, mainDecorationPane);

        setBackground(Color.WHITE);
        setOpaque(true);
    }
    
    protected void setMapComponent(Component comp){
        
        if(mapComponent != null){
            mapDecorationPane.remove(mapComponent);        
        }
        mapComponent = comp;
        mapDecorationPane.add(mapComponent, Integer.valueOf(0));
        mapDecorationPane.revalidate();
        
    }

    //----------------------Use as extend for subclasses------------------------
    protected void setRendering(boolean render) {
        informationDecoration.setPaintingIconVisible(render);
    }

    //----------------------Over/Sub/information layers-------------------------
    @Override
    public void setInformationDecoration(InformationDecoration info) {
        if (info == null) {
            throw new NullPointerException("info decoration can't be null");
        }

        mainDecorationPane.remove(informationDecoration.geComponent());
        informationDecoration = info;
        mainDecorationPane.add(informationDecoration.geComponent(), Integer.valueOf(3));

        mainDecorationPane.revalidate();
        mainDecorationPane.repaint();
    }

    @Override
    public InformationDecoration getInformationDecoration() {
        return informationDecoration;
    }

    @Override
    public void setBackgroundDecoration(MapDecoration back) {

        if (back == null) {
            throw new NullPointerException("background decoration can't be null");
        }

        mainDecorationPane.remove(backDecoration.geComponent());
        backDecoration = back;
        mainDecorationPane.add(backDecoration.geComponent(), Integer.valueOf(0));

        mainDecorationPane.revalidate();
        mainDecorationPane.repaint();
    }

    @Override
    public MapDecoration getBackgroundDecoration() {
        return backDecoration;
    }

    @Override
    public void addDecoration(MapDecoration deco) {

        if (deco != null && !userDecorations.contains(deco)) {
            deco.setMap2D(this);
            userDecorations.add(deco);
            userDecorationPane.add(deco.geComponent(), Integer.valueOf(userDecorations.indexOf(deco)));
            userDecorationPane.revalidate();
            userDecorationPane.repaint();
        }
    }

    @Override
    public void addDecoration(int index, MapDecoration deco) {

        if (deco != null && !userDecorations.contains(deco)) {
            deco.setMap2D(this);
            userDecorations.add(index, deco);
            userDecorationPane.add(deco.geComponent(), Integer.valueOf(userDecorations.indexOf(deco)));
            userDecorationPane.revalidate();
            userDecorationPane.repaint();
        }
    }

    @Override
    public int getDecorationIndex(MapDecoration deco) {
        return userDecorations.indexOf(deco);
    }

    @Override
    public void removeDecoration(MapDecoration deco) {
        if (deco != null && userDecorations.contains(deco)) {
            deco.setMap2D(null);
            deco.dispose();
            userDecorations.remove(deco);
            userDecorationPane.remove(deco.geComponent());
            userDecorationPane.revalidate();
            userDecorationPane.repaint();
        }
    }

    @Override
    public MapDecoration[] getDecorations() {
        return userDecorations.toArray(EMPTY_OVERLAYER_ARRAY);
    }

    /**
     * add a MapDecoration between the map and the user MapDecoration
     * those MapDecoration can not be removed because they are important
     * for edition/selection/navigation.
     * @param deco : MapDecoration to add
     */
    protected void addMapDecoration(MapDecoration deco) {
        mapDecorationPane.add(deco.geComponent(), Integer.valueOf(nextMapDecorationIndex));
        nextMapDecorationIndex++;
    }

    //-----------------------------MAP2D----------------------------------------

    @Override
    public Component getComponent() {
        return mainDecorationPane;
    }

    @Override
    public JPanel getUIContainer() {
        return this;
    }

}

