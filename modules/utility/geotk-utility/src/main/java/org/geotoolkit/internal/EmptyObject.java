/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.internal;


/**
 * A marker interface for any kind of objects that are empty. It can be an immutable empty
 * collection, or proxy objects created by {@link org.geotoolkit.xml.EmptyObjectHandler}
 * for example. We use a marker interface instead than annotation because we can not use
 * annotations with {@link java.lang.reflect.Proxy}.
 * <p>
 * Note that an "empty" object may not be completely empty. For example an empty ISO 19139
 * element could still have {@code xlink} attributes, but those attributes are not part of
 * ISO 19115. In this context "empty" means that, when marshalled to XML, this empty element
 * should not appear in the XML document.
 * <p>
 * This marker interface is not completely reliable since we can not retrofit Java classes
 * to implement it, and not every Geotk classes implement it neither. This class is mostly
 * used for ISO 19139 marshalling of proxy instances created with {@code EmptyObjectHandler}.
 * Some other Geotk classes implement this interface in an opportunist way, but this is
 * currently not really used outside ISO 19139 marshalling.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see org.geotoolkit.xml.EmptyObjectHandler
 *
 * @since 3.18
 * @module
 */
public interface EmptyObject {
}
