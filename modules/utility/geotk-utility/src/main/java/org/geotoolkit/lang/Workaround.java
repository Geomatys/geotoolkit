/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotates classes, methods or code which exist only as a workaround for a bug or limitation
 * in a library. This annotation is used as a marker for source code only, in order to remind
 * us which part of the code to revisit when new versions of the libraries are available.
 *
 * {@note When only a portion of a method contains a workaround and the annotation can not be
 * applied to that specific part, than it is applied to the whole method. Developers need to
 * refer to code comments in order to locate the specific part.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD,
         ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface Workaround {
    /**
     * A string identifying the library. Typical values are {@code "JDK"}, {@code "JAI"},
     * {@code "NetCDF"}, {@code "Units"}, {@code "Swingx"} and {@code "Geotk"}.
     *
     * @return An identifier of the library.
     */
    String library();

    /**
     * The last library version on which the bug has been verified.
     * The bug may have existed before, and may still exist later.
     *
     * @return The library version on which the bug has been observed.
     */
    String version();
}
