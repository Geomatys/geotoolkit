/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db.reverse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.geotoolkit.data.memory.FeatureStreams;
import org.opengis.filter.Filter;

/**
 * Cached a resultset content.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedResultSet {

    private final List<Map> records = new ArrayList<Map>();

    public CachedResultSet() {
    }

    public CachedResultSet(ResultSet rs, String ... columns) throws SQLException {
        append(rs, columns);
    }

    public void append(ResultSet rs, String ... columns) throws SQLException {
        while(rs.next()){
            final Map record = new HashMap();
            for(String col : columns){
                record.put(col, rs.getObject(col));
            }
            records.add(record);
        }
        rs.close();
    }

    public Iterator<Map> filter(Filter filter){
        return FeatureStreams.filter(records.iterator(), filter);
    }


}
