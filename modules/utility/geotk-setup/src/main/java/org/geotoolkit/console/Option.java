/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.console;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotates a field to be assigned from values given on the command line. This annotation
 * contains non-localized informations like the name or whatever the option is mandatory.
 * This annotation does not contain the description to be printed when the user ask for
 * {@linkplain CommandLine#help help} because such description is local-dependent. The
 * description must be provided in a {@linkplain java.util.Properties properties} file
 * under the following rules:
 * <p>
 * <ul>
 *   <li>The properties filename is the name of the class that contains the annotated field,
 *       and is located in the same package. It may contain a language suffix as described
 *       in the {@link java.util.ResourceBundle} class.</li>
 *   <li>The key is the value returned by {@link #name}, or the field name if no name is
 *       explicitly given.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Option {
    /**
     * The argument name. The default is the same name than the field.
     *
     * @return The argument name, or {@code ""} for the default.
     */
    String name() default "";

    /**
     * {@code true} if this option is mandatory. Options are optional by default.
     *
     * @return Whatever this option is mandatory or optional.
     */
    boolean mandatory() default false;

    /**
     * Examples of usage of this option.
     *
     * @return Examples of usage of this option, or an empty array for none.
     */
    String[] examples() default {};
}
