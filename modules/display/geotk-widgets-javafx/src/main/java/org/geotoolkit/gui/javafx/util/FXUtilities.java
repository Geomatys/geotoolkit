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

package org.geotoolkit.gui.javafx.util;

import javafx.beans.property.Property;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.geotoolkit.util.collection.NotifiedCheckedList;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXUtilities {

    public static final Font FONTAWESOME;
    
    static {
        FONTAWESOME = Font.loadFont(FXUtilities.class.getResource("/org/geotoolkit/font/fontawesome-webfont.ttf").toExternalForm(), 16);
    }
    
    private FXUtilities() {}
    
    public static <T> Property<T> beanProperty(Object candidate, String propertyName, Class<T> dataType){
        try {
            if(Boolean.class.equals(dataType)){
                return (Property<T>)JavaBeanBooleanPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Integer.class.equals(dataType)){
                return (Property<T>)JavaBeanIntegerPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Long.class.equals(dataType)){
                return (Property<T>)JavaBeanLongPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Float.class.equals(dataType)){
                return (Property<T>)JavaBeanFloatPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Double.class.equals(dataType)){
                return (Property<T>)JavaBeanDoublePropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else{
                return JavaBeanObjectPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex.getMessage(),ex);
        }
    }
        
//    public ObservableValue create(){
//        return new ObservableValueBase() {
//
//            @Override
//            public Object getValue() {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        };
//    }
    
    
    public static java.awt.Color toSwingColor(Color fxColor){
        final int r = (int) (fxColor.getRed() * 255);
        final int g = (int) (fxColor.getGreen() * 255);
        final int b = (int) (fxColor.getBlue() * 255);
        final int rgb = (r << 16) + (g << 8) + b;
        return new java.awt.Color(rgb);
    }
    
    public static Color toFxColor(java.awt.Color swingColor){
        final double r = (double)swingColor.getRed() / 255.0;
        final double g = (double)swingColor.getGreen() / 255.0;
        final double b = (double)swingColor.getBlue() / 255.0;
        final double a = (double)swingColor.getBlue() / 255.0;
        return new Color(r, g, b, a);
    }
        
    /**
     * Expand all nodes from root to given node
     * @param candidate 
     */
    public static void expandRootToItem(TreeItem candidate) {
        if (candidate != null) {
            expandRootToItem(candidate.getParent());
            if (!candidate.isLeaf()) {
                candidate.setExpanded(true);
            }
        }
    }
    
    /**
     * Expand all nodes child node recursively
     * @param candidate 
     */
    public static void expandAll(TreeItem candidate) {
        if (candidate != null) {
            candidate.setExpanded(true);
            for(Object ti : candidate.getChildren()){
                expandAll((TreeItem)ti);
            }
        }
    }
    
    

}
