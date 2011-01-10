/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.lucene.filter;

import org.apache.lucene.document.Document;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.filter.accessor.PropertyAccessorFactory;

/**
 * Lucene accessor factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LucenePropertyAccessorFactory implements PropertyAccessorFactory{

    private final PropertyAccessor accessor;

    public LucenePropertyAccessorFactory() {
        accessor = new LucenePropertyAccessor();
    }

    @Override
    public int getPriority() {
        return 10;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyAccessor createPropertyAccessor(final Class type, final String xpath, final Class target, final Hints hints) {

        if (!Document.class.isAssignableFrom(type)) {
            return null; // we only work with lucene document
        }

        //lucene doesnt need xpath, target class or hints
        return accessor;
    }

}
