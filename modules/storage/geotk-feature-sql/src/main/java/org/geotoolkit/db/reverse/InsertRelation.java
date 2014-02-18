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

import org.opengis.feature.ComplexAttribute;

/**
 * When inserting a complex feature in base. it must be divided in smaller elements.
 * Those flat insertions and relations are represented by this class.
 * 
 * @author Johann Sorel
 */
public final class InsertRelation {
    
    public ComplexAttribute parent;
    public ComplexAttribute child;
    public RelationMetaModel relation;
    
}
