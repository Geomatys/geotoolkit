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
package org.geotoolkit.process.mapfile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.CharSequences;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

import org.opengis.filter.expression.PropertyName;
import static org.geotoolkit.process.mapfile.MapfileTypes.*;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;

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

    private static Feature readElement(final Feature parent, final LineNumberReader reader, String line) throws IOException{
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

        if(value == null && ft != null){
            //it's a feature type
            final Feature f = ft.newInstance();

            //read until element END
            line = reader.readLine().trim();
            while(!line.equalsIgnoreCase("END")){
                final Feature prop = readElement(f, reader,line);
                if(prop != null){
                    final String name = prop.getType().getName().toString();
                    final Object oldValues = f.getPropertyValue(name);
                    if (oldValues instanceof Collection) {
                        final List<Feature> values = new ArrayList<>((Collection)oldValues);
                        values.add(prop);
                        f.setPropertyValue(name, values);
                    } else {
                        f.setPropertyValue(name, prop);
                    }
                }
                line = reader.readLine().trim();
            }

            return f;
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
            final AttributeType desc = getDescriptorIgnoreCase(parent.getType(), typeName);
            if(desc!=null){
                parent.setPropertyValue(desc.getName().toString(), convertType(value, desc));
            }
            return null;
        }
    }

    /**
     * Handle mapfile formating :
     * - Boolean [on/off]  [true/false]
     * - Color [r] [g] [b]
     * - Point [x] [y]
     */
    private static Object convertType(String value, final AttributeType desc) throws IOException{
        value = value.trim();

        if(value.endsWith("END")){
            value = value.substring(0,value.length()-4);
        }

        if(value.startsWith("\"") || value.startsWith("'")){
            value = value.substring(1, value.length()-1);
        }

        final Class clazz = desc.getValueClass();

        if(clazz == Boolean.class){
            return (value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true"));
        }else if(clazz == Color.class){
            if(value.startsWith("#")){
                //normal conversion
                return ObjectConverters.convert(value, clazz);
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

            final int nbspace = CharSequences.count(value, " ");

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
        }else if(clazz == float[].class){
            final String[] parts = value.split(" ");
            final float[] dashes = new float[parts.length];
            for(int i=0;i<parts.length;i++){
                dashes[i] = Float.valueOf(parts[i].trim());
            }
            return dashes;
        }

        return ObjectConverters.convert(value, clazz);
    }

    private static AttributeType getDescriptorIgnoreCase(final FeatureType parent, final String name){
        try {
            final PropertyType pt = parent.getProperty(name);
            if(pt instanceof AttributeType) return (AttributeType) pt;
        } catch(PropertyNotFoundException ex) {
            //do nothing
        }
        return null;
    }

}
