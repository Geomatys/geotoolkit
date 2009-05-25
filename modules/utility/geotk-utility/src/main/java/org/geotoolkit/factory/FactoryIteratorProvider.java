/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

import java.util.Iterator;


/**
 * Provides iterators over factories of specified categories. Users shall
 * {@linkplain Factories#addFactoryIteratorProvider register} an implementation
 * of this interface when the default lookup mechanism (namely scanning the content of the
 * <code>META-INF/services/</code><var>category</var> file in every JARs found on the classpath)
 * can not work. Such need may appear in the context of {@linkplain ClassLoader class loaders}
 * restricting access to non-package directories as {@code META-INF}. This constraint occurs on
 * the Eclipse platform for instance.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see Factories#addFactoryIteratorProvider
 *
 * @since 2.4
 * @level advanced
 * @module
 */
public interface FactoryIteratorProvider {
    /**
     * Returns an iterator over all factory implementations of the given category.
     * The {@code category} argument is the interface to be implemented, not the actual
     * implementation class. The returned implementation is often a instance of some
     * {@link Factory} subclass, but this is not mandatory - it could be any object.
     *
     * @param  <T> The category for the factories to be returned.
     * @param  category The category for the factories to be returned.
     * @return Factories that implement the specified category.
     */
    <T> Iterator<T> iterator(Class<T> category);
}
