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

import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;

/**
 * Describe a table foreign key.
 * Representation is transformed in a filter between two tables.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class ForeignKey {

    public static final String RELATION = "relationFilter";
    
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    private final RelationMetaModel relationModel;
    private final PropertyName property;

    public ForeignKey(final RelationMetaModel model) {
        this.relationModel = model;
        this.property = FF.property(relationModel.getForeignColumn());
    }

    public RelationMetaModel getRelationModel() {
        return relationModel;
    }
    
    public Filter toFilter(final Object value){
        return FF.equals(property, FF.literal(value));
    }
    
}
