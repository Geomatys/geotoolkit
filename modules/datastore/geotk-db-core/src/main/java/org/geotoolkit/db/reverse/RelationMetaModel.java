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

/**
 * Description of a relation between two tables.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class RelationMetaModel {

    private final String currentColumn;
    private final String foreignSchema;
    private final String foreignTable;
    private final String foreignColumn;
    private final boolean imported;

    public RelationMetaModel(final String currentColumn, final String foreignSchema,
            final String foreignTable, final String foreignColumn, boolean imported) {
        this.currentColumn = currentColumn;
        this.foreignSchema = foreignSchema;
        this.foreignTable = foreignTable;
        this.foreignColumn = foreignColumn;
        this.imported = imported;
    }

    public String getCurrentColumn() {
        return currentColumn;
    }

    public String getForeignColumn() {
        return foreignColumn;
    }

    public String getForeignSchema() {
        return foreignSchema;
    }

    public String getForeignTable() {
        return foreignTable;
    }

    /**
     * Indicate if this key is imported.
     * @return 
     */
    public boolean isImported() {
        return imported;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(currentColumn);
        sb.append((imported) ? " → " : " ← ");
        sb.append(foreignSchema).append('.');
        sb.append(foreignTable).append('.').append(foreignColumn);
        return sb.toString();
    }
}
