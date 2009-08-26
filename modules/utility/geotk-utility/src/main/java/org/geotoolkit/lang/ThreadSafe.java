/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 * For example unless stated otherwise, thread-safety is implicit for {@linkplain Static static}
 * methods, {@linkplain Enum enumerations} and {@linkplain Immutable immutable} objects.
 * As a general rule:
 *
 * <ul>
 *   <li><p>Classes without {@code @ThreadSafe} annotation may or may not be thread safe.
 *       Thread-safety may be implicit (as documented above), or the multi-thread behavior
 *       may be documented in the class javadoc, or not documented at all (typically because
 *       subject to changes). Unless otherwise known, class without annotation should be
 *       considered <strong>not</strong> thread-safe.</p></li>
 *
 *   <li><p>Classes with {@code @ThreadSafe} annotation are garanteed to be thread safe.
 *       This garantee is expected to apply to all subclasses as well. The later may be
 *       annotated explicitly for clarity, but don't have too.</p></li>
 * </ul>
 *
 * The sole purpose of this annotation is to replace the "<cite>This class is thread-safe</cite>"
 * sentence that would be otherwise copied in class javadoc.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.00
 * @module
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ThreadSafe {
    /**
     * Returns {@code true} if the annotated class supports some level of concurrency.
     * The value {@code false} is sometime specified in order to declare explicitly a
     * non-concurrent behavior, but don't have to: if this value is not specified, then
     * the class should be assumed non-concurrent (i.e. if two threads want to execute
     * the same method, then one thread will have to wait that the other thread finish
     * first).
     *
     * @return {@code true} if different threads can execute the methods concurrently.
     *
     * @since 3.03
     */
    boolean concurrent() default false;
}
