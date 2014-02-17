/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.lucene.index;

import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.StoredFieldVisitor;

/**
 * A Lucene field selector, allowing to retrieve only the field containg the identifiers of the document.
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class IDFieldSelector extends StoredFieldVisitor {

    private boolean found = false;
    
    private final String fieldName;
    
    public IDFieldSelector(final String fieldName) {
        this.fieldName = fieldName;
        this.found     = false;
    }
    
    /**
     * Accept only the id field of a lucene document.
     *
     * @param field The name of the current field to load.
     * @return FieldSelectorResult.LOAD only if the fieldName is "id".
     */
    @Override
    public Status needsField(final FieldInfo field) {
        if (field.name.equals(fieldName)) {
            found = true;
            return Status.YES;
        } else if (found) {
            return Status.STOP;
        } else {
            return Status.NO;
        }
    }
}
