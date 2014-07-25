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
import javafx.scene.text.Font;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXUtilities {

    public static final Font FONTAWESOME;
    
    static {
        FONTAWESOME = Font.loadFont(FXUtilities.class.getResource("/org/geotoolkit/gui/swing/resource/fonticon/fontawesome-webfont.ttf").toExternalForm(), 16);
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
    
    
    
}
