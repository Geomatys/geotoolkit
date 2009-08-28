/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
 * A marker annotation for immutable classes. The sole purpose of this annotation is to
 * replace the "<cite>This class is immutable</cite>" sentence that would be otherwise
 * copied in class javadoc.
 * <p>
 * If a base class is marked as immutable, then all sub-classes shall be immutable as well.
 *
 * {@section Thread safety}
 * Unless otherwise specified, every immutable classes are implicitly
 * {@linkplain ThreadSafe thread-safe}. Their thread-safety usually support
 * {@linkplain ThreadSafe#concurrent concurrency}, but a few exceptions exist.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Immutable {
}
