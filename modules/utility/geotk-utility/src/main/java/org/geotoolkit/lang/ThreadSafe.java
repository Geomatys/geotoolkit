/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.lang;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


/**
 * A marker annotation for implementation classes that are thread safe. This annotation is
 * for documentation purpose only, and is applied only on classes that are worthly to note.
 * For example unless stated otherwise, thread-safety is implicit for most static methods,
 * enumerations and immutable objects. As a general rule:
 *
 * <ul>
 *   <li><p>Classes without {@code @ThreadSafe} annotation may or may not be thread safe.
 *       Thread-safety may be implicit (static methods, enumerations, immutable classes,
 *       <cite>etc.</cite>, or the multi-thread behavior may be documented in the class
 *       javadoc, or not documented at all (typically because subject to changes).</p></li>
 *
 *   <li><p>Classes with {@code @ThreadSafe} annotation are garanteed to be thread safe.
 *       This garantee is expected to apply to all subclasses as well. The later may be
 *       annotated explicitly for clarity, but don't have too. For example we don't
 *       annotate the ~100 metadata implementation classes.</p></li>
 * </ul>
 *
 * The sole purpose of this annotation is to replace the "<cite>This class is thread-safe</cite>"
 * sentence that would be otherwise copied in class javadoc.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ThreadSafe {
}
