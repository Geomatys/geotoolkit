/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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

package org.geotoolkit.processing.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.Filter;
import org.opengis.metadata.Metadata;

/**
 * Convenient methods to transform possible constant value from and to String.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ConstantUtilities {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.processing.chain");

    private ConstantUtilities() {}


    public static String valueToString(Object value){

        if(value == null) return null;

        if(value instanceof Boolean){
            return Boolean.toString((Boolean)value);

        }else if(value instanceof Integer){
            return Integer.toString((Integer)value);

        }else if(value instanceof Double){
            return Double.toString((Double)value);

        }else if(value instanceof String){
            return (String)value;

        }else if(value instanceof boolean[]){
            final StringBuilder sb = new StringBuilder();
            final boolean[] candidates = (boolean[])value;
            for(int i=0;i<candidates.length;i++){
                final String part = valueToString(candidates[i]);
                sb.append(part.length()).append(':').append(part);
            }
            return sb.toString();

        }else if(value instanceof int[]){
            final StringBuilder sb = new StringBuilder();
            final int[] candidates = (int[])value;
            for(int i=0;i<candidates.length;i++){
                final String part = valueToString(candidates[i]);
                sb.append(part.length()).append(':').append(part);
            }
            return sb.toString();

        }else if(value instanceof double[]){
            final StringBuilder sb = new StringBuilder();
            final double[] candidates = (double[])value;
            for(int i=0;i<candidates.length;i++){
                final String part = valueToString(candidates[i]);
                sb.append(part.length()).append(':').append(part);
            }
            return sb.toString();

        }else if(value instanceof String[]){
            final StringBuilder sb = new StringBuilder();
            final String[] candidates = (String[])value;
            for(int i=0;i<candidates.length;i++){
                final String part = valueToString(candidates[i]);
                sb.append(part.length()).append(':').append(part);
            }
            return sb.toString();

        }else if(value instanceof Map){
            final StringBuilder sb = new StringBuilder();
            final Map<?,?> map = (Map) value;
            for(final Entry entry : map.entrySet()){
                final String key = valueToString(entry.getKey());
                final String val = valueToString(entry.getValue());
                sb.append(key.length()).append(':').append(key);
                sb.append(val.length()).append(':').append(val);
            }
            return sb.toString();

        }else if(value instanceof Metadata){
            //TODO

        }else if(value instanceof Filter){
            final Filter filter = (Filter) value;
            return CQL.write(filter);
        }

        throw new IllegalArgumentException("Object class not supported : " +value);

    }

    public static <T> T stringToValue(String value, Class<T> clazz){

        if(value == null) return null;

        if(Boolean.class.isAssignableFrom(clazz)){
            return (T) Boolean.valueOf(value);

        }else if(Integer.class.isAssignableFrom(clazz)){
            return (T) Integer.valueOf(value);

        }else if(Double.class.isAssignableFrom(clazz)){
            return (T) Double.valueOf(value);

        }else if(String.class.isAssignableFrom(clazz)){
            return (T) value;

        }else if(boolean[].class.isAssignableFrom(clazz)){
            final List<String> parts = split(value);
            final boolean[] candidates = new boolean[parts.size()];
            for(int i=0; i<candidates.length; i++) {
                candidates[i] = (Boolean)stringToValue(parts.get(i), Boolean.class);
            }
            return (T) candidates;

        }else if(int[].class.isAssignableFrom(clazz)){
            final List<String> parts = split(value);
            final int[] candidates = new int[parts.size()];
            for(int i=0; i<candidates.length; i++) {
                candidates[i] = (Integer)stringToValue(parts.get(i), Integer.class);
            }
            return (T) candidates;

        }else if(double[].class.isAssignableFrom(clazz)){
            final List<String> parts = split(value);
            final double[] candidates = new double[parts.size()];
            for(int i=0; i<candidates.length; i++) {
                candidates[i] = (Double)stringToValue(parts.get(i), Double.class);
            }
            return (T) candidates;

        }else if(String[].class.isAssignableFrom(clazz)){
            final List<String> parts = split(value);
            final String[] candidates = new String[parts.size()];
            for(int i=0; i<candidates.length; i++) {
                candidates[i] = (String)stringToValue(parts.get(i), String.class);
            }
            return (T) candidates;

        }else if(Map.class.isAssignableFrom(clazz)){
            final List<String> parts = split(value);
            final Map map = new HashMap();
            for(int i=0; i<parts.size(); i+=2) {
                map.put(parts.get(i), parts.get(i+1));
            }
            return (T) map;

        }else if(Filter.class.isAssignableFrom(clazz)){
            try {
                return (T) CQL.parseFilter(value);
            } catch (CQLException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(), ex);
            }
            return null;
        }

        throw new IllegalArgumentException("Object class not supported : " +value);

    }

    private static List<String> split(String candidate){

        final List<String> parts = new ArrayList<String>();
        int index = candidate.indexOf(':');
        while(index>0){
            final String strLenght = candidate.substring(0, index);
            final int size = Integer.parseInt(strLenght);
            final String part = candidate.substring(index+1, index+size+1);
            parts.add(part);

            candidate = candidate.substring(index+size+1);
            index = candidate.indexOf(':');
        }

        return parts;
    }

}
