/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.resources;

import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.sis.util.resources.IndexedResourceBundle;


/**
 * Locale-dependent resources for widgets messages.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
public final class Widgets extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     *
     * @author Cédric Briançon (Geomatys)
     * @version 3.03
     *
     * @since 3.03
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * Add all
         */
        public static final short AddAll = 1;

        /**
         * Add selected elements
         */
        public static final short AddSelectedElements = 2;

        /**
         * Confirm data addition
         */
        public static final short ConfirmAddData = 3;

        /**
         * Confirm delete
         */
        public static final short ConfirmDelete = 4;

        /**
         * <html>Are you sure you want to delete "<cite>{0}</cite>"?< <strong>This action will remove
         * all references to raster data in that layer.</strong> However, the raster files will not be
         * deleted.</html>
         */
        public static final short ConfirmDeleteLayer_1 = 5;

        /**
         * A {0,choice,0#horizontal|1#vertical} Coordinate Reference System must be specified.
         */
        public static final short CrsRequired_1 = 6;

        /**
         * Domain of entries to list
         */
        public static final short DomainOfEntries = 7;

        /**
         * You can restrict the amount of images to be listed by specifying a smaller geographic area
         * or time range, or a larger resolution. Leave the values unchanged for listing every images
         * available in the layer.
         */
        public static final short ExplainDomainOfEntries = 8;

        /**
         * Incomplete form
         */
        public static final short IncompleteForm = 9;

        /**
         * Elements of layer {0}
         */
        public static final short LayerElements_1 = 10;

        /**
         * New format (editable).
         */
        public static final short NewFormat = 11;

        /**
         * Raster sample values are geophysics.
         */
        public static final short RasterIsGeophysics = 12;

        /**
         * Remove all
         */
        public static final short RemoveAll = 13;

        /**
         * Remove selected elements
         */
        public static final short RemoveSelectedElements = 14;

        /**
         * Rename this format if sample dimensions need to be edited.
         */
        public static final short RenameFormatForEdit = 15;

        /**
         * Select a directory
         */
        public static final short SelectDirectory = 16;

        /**
         * Select a file
         */
        public static final short SelectFile = 17;

        /**
         * Select variables
         */
        public static final short SelectVariables = 18;

        /**
         * <html><i>from</i> {0}<br><i>to</i> {1}</html>
         */
        public static final short TimeRange_2 = 19;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public Widgets(final java.net.URL filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Widgets getResources(Locale locale) throws MissingResourceException {
        return getBundle(Widgets.class, locale);
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short key) throws MissingResourceException {
        return getResources(null).getString(key);
    }
}
