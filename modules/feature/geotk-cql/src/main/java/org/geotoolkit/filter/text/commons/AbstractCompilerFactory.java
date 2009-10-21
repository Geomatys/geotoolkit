/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.commons;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.opengis.filter.FilterFactory;


/**
 * Provides the common behavior to make a compiler implementation
 * <p>
 * Warning: This component is not published. It is part of module implementation.
 * Client module should not use this feature.
 * </p>
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
public abstract class AbstractCompilerFactory {
    /**
     * Initializes and create the new compiler
     *
     * @param predicate
     * @param filterFactory
     * @return CQLCompiler
     * @throws CQLException
     */
    public ICompiler makeCompiler(final String predicate, final FilterFactory filterFactory) throws CQLException {
        final FilterFactory ff = (filterFactory != null) ? filterFactory : FactoryFinder.getFilterFactory(null);
        return createCompiler(predicate, ff);
    }

    protected abstract ICompiler createCompiler(final String predicate,final FilterFactory filterFactory);
}
