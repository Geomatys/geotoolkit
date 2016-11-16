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

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;

/**
 * Description of a relation between two tables.
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class RelationMetaModel {
    
    public static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final String relationName;
    private final String currentColumn;
    private final String foreignSchema;
    private final String foreignTable;
    private final String foreignColumn;
    private final boolean imported;
    private final boolean deleteCascade;
    
    //for filter
    private final PropertyName property;

    public RelationMetaModel(final String relationName,final String currentColumn, final String foreignSchema,
            final String foreignTable, final String foreignColumn, 
            boolean imported, boolean deleteCascade) {
        ArgumentChecks.ensureNonNull("relation name", relationName);
        ArgumentChecks.ensureNonNull("current column", currentColumn);
        ArgumentChecks.ensureNonNull("foreign table", foreignTable);
        ArgumentChecks.ensureNonNull("foreign column", foreignColumn);
        this.relationName = relationName;
        this.currentColumn = currentColumn;
        this.foreignSchema = foreignSchema;
        this.foreignTable = foreignTable;
        this.foreignColumn = foreignColumn;
        this.imported = imported;
        this.deleteCascade = deleteCascade;
        this.property = FF.property(foreignColumn);
    }

    public String getRelationName() {
        return relationName;
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
    
    /**
     * @return true if relation implies a delete on cascade.
     */
    public boolean isDeleteCascade(){
        return deleteCascade;
    }

    public Filter toFilter(final Object value){
        return FF.equals(property, FF.literal(value));
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
