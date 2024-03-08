/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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
package org.geotoolkit.display2d.painter;

import java.util.Set;
import java.util.HashSet;
import org.opengis.filter.ValueReference;


/**
 * Collects all properties used in style elements.
 *
 * <p>
 * NOTE: this class is a first draft subject to modifications.
 * </p>
 *
 * @author  Johann Sorel (Geomatys)
 * @version 1.2
 * @since   1.2
 */
final class PropertyNameCollector extends SymbologyVisitor {
    /**
     * All value references found.
     *
     * @see ValueReference#getXPath()
     */
    final Set<String> references;

    /**
     * Creates a new collector.
     */
    PropertyNameCollector() {
        references = new HashSet<>();
    }

    /**
     * Invoked for each value reference found.
     */
    @Override
    protected void visitProperty(final ValueReference<?,?> expression) {
        if (expression != null) {
            references.add(expression.getXPath());
        }
    }
}
