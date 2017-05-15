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
 * Annotates a method to be invoked when the first argument which is not an
 * {@linkplain Option option} matches the {@linkplain #name name}. This annotation contains
 * non-localized informations like the name. This annotation does not contain the description to
 * be printed when the user ask for {@linkplain CommandLine#help help} because such description
 * is local-dependent. The description must be provided in a {@linkplain java.util.Properties
 * properties} file under the following rules:
 * <p>
 * <ul>
 *   <li>The properties filename is the name of the class that contains the annotated method,
 *       and is located in the same package. It may contain a language suffix as described
 *       in the {@link java.util.ResourceBundle} class.</li>
 *   <li>The key is the value returned by {@link #name}, or the method name if no name is
 *       explicitly given.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
    /**
     * The action name. The default is the same name than the method.
     *
     * @return The action name, or {@code ""} for the default.
     */
    String name() default "";

    /**
     * Examples of usage of this action.
     *
     * @return Examples of usage of this action, or an empty array for none.
     */
    String[] examples() default {};

    /**
     * The minimal number of arguments expected after the action name.
     * The default value is 0, which means that there is no minimum.
     *
     * @return The minimum number of arguments.
     */
    int minimalArgumentCount() default 0;

    /**
     * The maximal number of arguments expected after the action name.
     * The default value is {@link Integer#MAX_VALUE}, which means that
     * there is no maximum.
     *
     * @return The maximum number of arguments.
     */
    int maximalArgumentCount() default Integer.MAX_VALUE;
}
