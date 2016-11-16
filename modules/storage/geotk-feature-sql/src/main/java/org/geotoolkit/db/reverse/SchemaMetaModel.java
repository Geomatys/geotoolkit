/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 * Description of a database schema.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
        return Trees.toString(name, tables.values());
    }
}
