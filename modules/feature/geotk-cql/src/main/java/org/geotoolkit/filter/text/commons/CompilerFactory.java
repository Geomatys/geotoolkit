/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.filter.text.cql2.CQLCompiler;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.geotoolkit.filter.text.ecql.ECQLCompiler;
import org.opengis.filter.FilterFactory;


/**
 * Creates the compiler required for the specific language.
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
final class CompilerFactory {

    private CompilerFactory() {
    }

    /**
     * Initializes and create the new compiler
     *
     * @param predicate
     * @param filterFactory
     * @return CQLCompiler
     * @throws CQLException
     */
    public static ICompiler makeCompiler(final Language language, final String predicate, final FilterFactory filterFactory) throws CQLException {

        final FilterFactory ff = (filterFactory != null) ? filterFactory : FactoryFinder.getFilterFactory(null);
        final ICompiler compiler;
        if (language == Language.ECQL) {
            compiler = new ECQLCompiler(predicate, ff);
        } else {
            compiler = new CQLCompiler(predicate, ff);
        }
        return compiler;
    }
}
