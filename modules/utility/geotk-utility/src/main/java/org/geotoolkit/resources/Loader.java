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
package org.geotoolkit.resources;

import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.ResourceBundle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;


/**
 * Controls the resource bundle loading. This class looks for {@code .utf} files rather than
 * the Java default {@code .class} or {@code .properties} files.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class Loader extends ResourceBundle.Control {
    /**
     * The filename extension of resource files, without leading dot.
     */
    private static final String EXTENSION = "utf";

    /**
     * The formats supported by this loader.
     */
    private static final List<String> FORMATS = Collections.singletonList("geotoolkit." + EXTENSION);

    /**
     * The singleton instance of the {@link Loader} class.
     */
    public static final Loader INSTANCE = new Loader();

    /**
     * Creates the unique instance of the Geotoolkit resource bundle loader.
     */
    private Loader() {
    }

    /**
     * Returns the formats supported by this loader.
     * The only supported format is {@code "geotoolkit.utf"}.
     *
     * @param baseName Ignored.
     * @return The supported formats.
     */
    @Override
    public List<String> getFormats(String baseName) {
        return FORMATS;
    }

    /**
     * Returns {@code false} in all case, since our implementation never needs reload.
     */
    @Override
    public boolean needsReload(final String baseName, final Locale locale, final String format,
                               final ClassLoader loader, final ResourceBundle bundle, long loadTime)
    {
        return false;
    }

    /**
     * Instantiates a new resource bundle.
     *
     * @param baseName  The fully qualified name of the base resource bundle.
     * @param locale    The locale for which the resource bundle should be instantiated.
     * @param format    Ignored since this loader supports only one format.
     * @param loader    The class loader to use.
     * @param reload    Ignored since this loader do not supports resource expiration.
     * @return The resource bundle instance, or null if none could be found.
     */
    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
                                    final ClassLoader loader, final boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
    {
        final Class<?> classe;
        try {
            classe = Class.forName(baseName, true, loader);
        } catch (ClassNotFoundException e) {
            return null; // This is the expected behavior as of Control.newBundle contract.
        }
        /*
         * Gets the filename relative to the class we created, since we assumes that UTF files
         * are in the same package. Then check for file existence and instantiate the resource
         * bundle only if the file is found.
         */
        String filename = toResourceName(toBundleName(classe.getSimpleName(), locale), EXTENSION);
        if (classe.getResource(filename) == null) {
            if (!Locale.ENGLISH.equals(locale)) {
                return null;
            }
            // We have no explicit resources for English. We use the default one for that.
            filename = toResourceName(classe.getSimpleName(), EXTENSION);
            if (classe.getResource(filename) == null) {
                return null;
            }
        }
        /*
         * If the file exists, instantiate now the resource bundle. Note that the constructor
         * will not loads the data immediately, which is why we don't pass it the above URL.
         */
        final Constructor<?> c;
        try {
            c = classe.getDeclaredConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw instantiationFailure(e);
        }
        final ResourceBundle bundle;
        c.setAccessible(true);
        try {
            bundle = (ResourceBundle) c.newInstance(filename);
        } catch (InvocationTargetException e) {
            throw instantiationFailure(e);
        }
        return bundle;
    }

    /**
     * Creates an exception for a resource bundle that can not be created.
     */
    private static InstantiationException instantiationFailure(final Exception cause) {
        InstantiationException exception = new InstantiationException(cause.getLocalizedMessage());
        exception.initCause(cause);
        return exception;
    }
}
