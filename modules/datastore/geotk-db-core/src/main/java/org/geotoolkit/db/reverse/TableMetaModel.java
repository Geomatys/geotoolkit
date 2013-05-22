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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.geotoolkit.gui.swing.tree.Trees;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.ComplexType;

/**
 * Description of a database table.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TableMetaModel {

    String name;
    String type;
    ComplexType baseType;
    SimpleFeatureType simpleType;
    ComplexType complexType;
    PrimaryKey key;
    //those are 0:1 relations
    final Collection<RelationMetaModel> importedKeys = new ArrayList<RelationMetaModel>();
    //those are 0:N relations
    final Collection<RelationMetaModel> exportedKeys = new ArrayList<RelationMetaModel>();
    //inherited tables
    final Collection<String> parents = new ArrayList<String>();
    
    public TableMetaModel(final String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ComplexType getBaseType() {
        return baseType;
    }

    public SimpleFeatureType getSimpleType() {
        return simpleType;
    }

    public ComplexType getComplexType() {
        return complexType;
    }

    public Collection<RelationMetaModel> getExportedKeys() {
        return Collections.unmodifiableCollection(exportedKeys);
    }

    public Collection<RelationMetaModel> getImportedKeys() {
        return Collections.unmodifiableCollection(importedKeys);
    }

    /**
     * Detect if given type is a subtype. Conditions are :
     * - having a relation toward another type
     * - relation must be cascading
     * @param ft
     * @return true is type is a subtype
     */
    public boolean isSubType(){
        for(RelationMetaModel relation : getImportedKeys()){
            if(relation.isDeleteCascade()){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name);
        if (!importedKeys.isEmpty()) {
            sb.append(Trees.toString("\n Imported Keys", importedKeys));
        }
        if (!exportedKeys.isEmpty()) {
            sb.append(Trees.toString("\n Exported Keys", exportedKeys));
        }
        return sb.toString();
    }
}
