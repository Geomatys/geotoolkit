/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc.fid;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.util.Converters;


/**
 * Primary key of a table.
 *
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 * @module pending
 */
public class PrimaryKey {
    /**
     * The columns making up the primary key.
     */
    private final List<PrimaryKeyColumn> columns;

    /**
     * Table name.
     */
    private final String tableName;

    public PrimaryKey(final String tableName, final List<PrimaryKeyColumn> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public List<PrimaryKeyColumn> getColumns() {
        return columns;
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * Encodes a feature id from a primary key and result set values.
     */
    public static String encodeFID(final PrimaryKey pkey, final ResultSet rs)
            throws SQLException{

        final List<PrimaryKeyColumn> columns = pkey.getColumns();

        // no pk columns
        if (columns.isEmpty()) {
            return SimpleFeatureBuilder.createDefaultFeatureId();
        }

        // just one, no need to build support structures
        if (columns.size() == 1) {
            return escapeDot(rs.getString(columns.get(0).getName()));
        }

        // more than one
        final List<Object> keyValues = new ArrayList<Object>();
        for (PrimaryKeyColumn pc : columns) {
            keyValues.add(rs.getString(pc.getName()));
        }
        return encodeFID(keyValues);
    }

    public static String encodeFID(final List<Object> keyValues) {
        final StringBuilder fid = new StringBuilder();
        for (Object o : keyValues) {
            final String str = escapeDot(o);
            fid.append(str).append('.');
        }
        fid.setLength(fid.length() - 1);
        return fid.toString();
    }

    private static String escapeDot(Object obj){
        String str = String.valueOf(obj);
        return str.replaceAll("\\.", "◼");
    }
    
    private static String unescapeDot(String str){
        return str.replaceAll("◼",".");
    }
    
    /**
     * Decodes a fid into its components based on a primary key.
     *
     * @param strict If set to true the value of the fid will be validated against
     *   the type of the key columns. If a conversion can not be made, an exception will be thrown.
     */
    public static List<Object> decodeFID(final PrimaryKey key, String FID, final boolean strict) {
        //strip off the feature type name
        if (FID.startsWith(key.getTableName() + '.')) {
            FID = FID.substring(key.getTableName().length() + 1);
        }

        try {
            FID = URLDecoder.decode(FID, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never occur, because we asked for UTF-8 which is
            // known to be supported.
            throw new AssertionError(e);
        }

        //check for case of multi column primary key and try to backwards map using
        // "." as a seperator of values
        final List values;
        if (key.getColumns().size() > 1) {
            final String[] split = FID.split("\\.");

            //copy over to avoid array store exception
            //values = new ArrayList(split.length);

            //can not do this or it will be a typed list which will raise errors a bit later.
            //values = Arrays.asList(split);
            values = new ArrayList(split.length);
            for(String o : split){
                values.add(unescapeDot(o));
            }
        } else {
            //single value case
            values = new ArrayList();
            values.add(unescapeDot(FID));
        }
        if (values.size() != key.getColumns().size()) {
            throw new IllegalArgumentException("Illegal fid: " + FID + ". Expected " +
                    key.getColumns().size() + " values but got " + values.size());
        }

        //convert to the type of the key
        //JD: usually this would be done by the dialect directly when the value
        // actually gets set but the FIDMapper interface does not report types
        for (int i = 0; i < values.size(); i++) {
            final Object value = values.get(i);
            if (value != null) {
                final Class type = key.getColumns().get(i).getType();
                final Object converted = Converters.convert(value, type);
                if (converted != null) {
                    values.set(i, converted);
                }
                if (strict && !type.isInstance(converted)) {
                    throw new IllegalArgumentException("Value " + values.get(i) + " illegal for type " + type.getName());
                }
            }
        }

        return values;
    }

}
