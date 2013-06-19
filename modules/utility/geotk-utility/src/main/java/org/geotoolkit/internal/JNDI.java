/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.StringTokenizer;

import org.geotoolkit.lang.Static;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.logging.Logging;


/**
 * Utility methods related to JNDI.
 *
 * @author Jody Garnett (Refractions)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public final class JNDI extends Static {
    /**
     * The initial context. Will be created only when first needed.
     */
    private static InitialContext context;

    /**
     * Do not allow instantiation of this class.
     */
    private JNDI() {
    }

    /**
     * Returns the default initial context.
     *
     * @param  hints An optional set of hints, or {@code null} if none.
     * @return The initial context (never {@code null}).
     * @throws NamingException if the initial context can't be created.
     *
     * @since 2.4
     */
    public static synchronized InitialContext getInitialContext(final Hints hints)
            throws NamingException
    {
        if (context == null) {
            context = new InitialContext();
        }
        return context;
    }

    /**
     * Converts a Geotk name to the syntax used by the {@linkplain #getInitialContext
     * Geotk JNDI context}. Names may be constructed in a variety of ways depending on
     * the implementation of {@link InitialContext}. Geotk uses {@code "jdbc/EPSG"}
     * internally, but some implementations use the form {@code "jdbc:EPSG"}. Calling
     * this method before use will set the name right.
     *
     * @param  name Name of the form {@code "jdbc/EPSG"}, or {@code null}.
     * @return Name fixed up with {@link Context#composeName(String,String)},
     *         or {@code null} if the given name was null.
     *
     * @since 2.4
     */
    public static String fixName(final String name) {
        return fixName(null, name, null);
    }

    /**
     * Converts a Geotk name to the syntax used by the specified JNDI context.
     * This method is similar to {@link #fixName(String)}, but uses the specified
     * context instead of the Geotk one.
     *
     * @param  context The context to use, or {@code null} if none.
     * @param  name Name of the form {@code "jdbc/EPSG"}, or {@code null}.
     * @return Name fixed up with {@link Context#composeName(String,String)},
     *         or {@code null} if the given name was null.
     *
     * @since 2.4
     */
    public static String fixName(final Context context, final String name) {
        return (context != null) ? fixName(context, name, null) : name;
    }

    /**
     * Implementation of {@code fixName} method. If the context is {@code null}, then
     * the {@linkplain #getInitialContext Geotk initial context} will be fetch only
     * when first needed.
     */
    private static String fixName(Context context, final String name, final Hints hints) {
        String fixed = null;
        if (name != null) {
            final StringTokenizer tokens = new StringTokenizer(name, ":/");
            while (tokens.hasMoreTokens()) {
                final String part = tokens.nextToken();
                if (fixed == null) {
                    fixed = part;
                } else try {
                    if (context == null) {
                        context = getInitialContext(hints);
                    }
                    fixed = context.composeName(fixed, part);
                } catch (NamingException e) {
                    Logging.unexpectedException(JNDI.class, "fixName", e);
                    return name;
                }
            }
        }
        return fixed;
    }
}
