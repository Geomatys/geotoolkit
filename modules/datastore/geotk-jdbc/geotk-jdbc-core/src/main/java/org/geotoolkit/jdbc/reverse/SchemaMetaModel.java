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
package org.geotoolkit.jdbc.reverse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.util.StringUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SchemaMetaModel {

    final String name;
    final Map<String, TableMetaModel> tables = new HashMap<String, TableMetaModel>();

    public SchemaMetaModel(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Collection<TableMetaModel> getTables() {
        return tables.values();
    }

    public TableMetaModel getTable(final String name){
        return tables.get(name);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name).append('\n');
        sb.append(StringUtilities.toStringTree(tables.values()));
        return sb.toString();
    }
}
