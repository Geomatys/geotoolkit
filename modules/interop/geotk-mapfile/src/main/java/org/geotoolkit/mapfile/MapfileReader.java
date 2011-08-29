/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.mapfile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.UUID;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.Strings;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

import org.opengis.filter.expression.PropertyName;
import static org.geotoolkit.mapfile.MapfileTypes.*;

/**
 * Read the given mapfile and return a feature which type is MapFileTypes.MAP.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class MapfileReader {
    
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    private Object in = null;
    
    public MapfileReader() {
    }
    
    /**
     * @param in : source, can be a File, String, URL or URI
     */
    public void setInput(final Object in){
        this.in = in;
    }
    
    /**
     * Read the given file and return a feature which type is MapFileTypes.MAP.
     * @return Feature
     * @throws IOException 
     */
    public Feature read() throws IOException{
        InputStream stream = IOUtilities.open(in);
        
        LineNumberReader reader = null;
        try{
            reader = new LineNumberReader(new InputStreamReader(stream));
            return (Feature) readElement(null,reader,null);
        }finally{
            if(reader != null){
                reader.close();
            }
        }
    }
        
    private static Property readElement(final FeatureType parentType, final LineNumberReader reader, String line) throws IOException{
        if(line ==null){
            line = reader.readLine().trim();
        }
        if(line == null){
            return null;
        }
                
        final String typeName;
        String value;
        final int spaceIndex = line.indexOf(' ');
        if(spaceIndex < 0){
            //value is on next lines
            typeName = line;
            value = null;
        }else{
            //value is on the line
            typeName = line.substring(0, spaceIndex);
            value = line.substring(spaceIndex);
        }
        
        if(typeName.equalsIgnoreCase("INCLUDE")){
            //TODO open the related file and append all string to it
        }
                
        final FeatureType ft = getType(typeName);
                
        final Property result;
        if(value == null && ft != null){
            //it's a feature type       
            
            //check if we are in a parent, in this case use the descriptor
            PropertyDescriptor desc = null;
            if(parentType != null){
                desc = getDescriptorIgnoreCase(parentType, typeName);
            }
            
            final ComplexAttribute f;
            if(desc != null){
                f = (ComplexAttribute) FeatureUtilities.defaultProperty(desc, UUID.randomUUID().toString());
            }else{
                f = FeatureUtilities.defaultFeature(ft, UUID.randomUUID().toString());
            }
            
            result = f;
            
            //read until element END
            line = reader.readLine().trim();
            while(!line.equalsIgnoreCase("END")){
                final Property prop = readElement(ft, reader,line);
                if(prop != null){
                    f.getProperties().add(prop);
                }
                line = reader.readLine().trim();
            }
            
        }else{
            //read the full value if needed
            if(value == null){
                //read all until next END element
                line = reader.readLine().trim();
                while(!line.equalsIgnoreCase("END")){
                    value += line;
                    line = reader.readLine().trim();
                }
            }
            
            //it's a single property from parent type
            final PropertyDescriptor desc = getDescriptorIgnoreCase(parentType, typeName);
            if(desc != null){
                result = FeatureUtilities.defaultProperty(desc);
                result.setValue(convertType(value, desc));
            }else{
                result = null;
            }
        }        
                
        return result;
    }
    
    /**
     * Handle mapfile formating :
     * - Boolean [on/off]  [true/false]
     * - Color [r] [g] [b]
     * - Point [x] [y]
     */
    private static Object convertType(String value, final PropertyDescriptor desc) throws IOException{
        value = value.trim();
        if(value.startsWith("\"") || value.startsWith("'")){
            value = value.substring(1, value.length()-1);
        }
        
        final Class clazz = desc.getType().getBinding();
        
        if(clazz == Boolean.class){
            return (value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true"));            
        }else if(clazz == Color.class){
            if(value.startsWith("#")){
                //normal conversion
                return Converters.convert(value, clazz);
            }            
            final String[] colors = value.split(" ");
            return new Color(Integer.valueOf(colors[0]), Integer.valueOf(colors[1]), Integer.valueOf(colors[2]));
        }else if(Point2D.class.isAssignableFrom(clazz)){
            final String[] parts = value.split(" ");
            return new Point2D.Double(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        }else if(PropertyName.class.isAssignableFrom(clazz)){
            //return it as a property name
            return FF.property(value);
        }else if(Expression.class.isAssignableFrom(clazz)){
            
            if(value.startsWith("[")){
                // like : [ATTRIBUTE] => PropertyName
                value = value.substring(1, value.length()-1);
                return FF.property(value);
            }
            
            if(value.startsWith("#")){
                // like : #CD5F29 => Literal
                return FF.literal(value);
            }
            
            final int nbspace = Strings.count(value, " ");
            
            if(nbspace == 2){
                // color 255 255 255 => Literal
                final String[] colors = value.split(" ");
                
                final Integer r = Integer.valueOf(colors[0]);
                final Integer g = Integer.valueOf(colors[1]);
                final Integer b = Integer.valueOf(colors[2]);
                
                //possible -1 -1 -1 : we translate to white
                final Color c;
                if(r < 0 || g < 0 || b < 0){
                    c = Color.WHITE;
                }else{
                    c = new Color(r,g,b);
                }                
                return SF.literal(c);
            }
            
            //parse it as a number
            try{
                final double d = Double.valueOf(value);
                return FF.literal(d);
            }catch(final NumberFormatException ex){
            }
            
            //return it as a string literal
            return FF.literal(value);
        }
        
        return Converters.convert(value, clazz);
    }
    
    private static PropertyDescriptor getDescriptorIgnoreCase(final ComplexType parent, final String name){
        
        PropertyDescriptor desc = parent.getDescriptor(name);
        if(desc == null){
            //search ignoring case
            for(PropertyDescriptor d : parent.getDescriptors()){
                if(d.getName().getLocalPart().equalsIgnoreCase(name)){
                    desc = d;
                    break;
                }
            }
        }
        
        return desc;
    }
    
}
