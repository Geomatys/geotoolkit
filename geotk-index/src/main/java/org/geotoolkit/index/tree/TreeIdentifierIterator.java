/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

import java.io.IOException;
import java.util.Iterator;

/**
 * Iter on all tree Identifier from search results.
 *
 * @author Remi Marechal (Geomatys).
 */
public interface TreeIdentifierIterator extends Iterator<Integer> {

    /**
     * Iter on each integer Tree Identifier from search result.
     *
     * @return next integer from search result.
     */
    int nextInt() throws IOException;
}
