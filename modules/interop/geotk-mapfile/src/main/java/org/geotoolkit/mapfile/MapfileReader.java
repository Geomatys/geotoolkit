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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.UUID;

import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.util.Converters;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import static org.geotoolkit.mapfile.MapFileTypes.*;

/**
 * Read the given mapfile and return a feature which type is MapFileTypes.MAP.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class MapfileReader {
    
    private File file = null;
    
    public MapfileReader() {
    }
    
    public void setInput(final File obj){
        this.file = obj;
    }
    
    /**
     * Read the given file and return a feature which type is MapFileTypes.MAP.
     * @return Feature
     * @throws IOException 
     */
    public Feature read() throws IOException{
        LineNumberReader reader = null;
        try{
            reader = new LineNumberReader(new FileReader(file));
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
        
        if(typeName.equals("INCLUDE")){
            //TODO open the related file and append all string to it
        }
                
        final FeatureType ft = getType(typeName);
                
        final Property result;
        if(value == null && ft != null){
            //it's a feature type       
            
            //check if we are in a parent, in this case use the descriptor
            PropertyDescriptor desc = null;
            if(parentType != null){
                desc = parentType.getDescriptor(typeName);
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
            final PropertyDescriptor desc = parentType.getDescriptor(typeName);
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
    private static Object convertType(String value, final PropertyDescriptor desc){
        value = value.trim();
        if(value.startsWith("\"")){
            value = value.substring(1, value.length()-2);
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
        }
        
        return Converters.convert(value, clazz);
    }
    
}
