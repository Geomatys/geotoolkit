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

import java.util.Locale;
import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.geotoolkit.internal.sql.Dialect;
import org.geotoolkit.io.X364;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Loggings;


/**
 * The action run by {@link CommandLine#version()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 2.5
 * @module
 */
final class VersionAction {
    /**
     * Do not allow instantiation of this class.
     */
    private VersionAction() {
    }

    /**
     * Invoked when the user asked the {@code "version"} action.
     */
    static void version(final PrintWriter out, final boolean colors, final Locale locale) {
        final String bold, faint, normal;
        if (colors) {
            bold   = X364.BOLD.sequence();
            faint  = X364.FAINT.sequence();
            normal = X364.NORMAL.sequence();
        } else {
            bold = faint = normal = "";
        }
        final Vocabulary resources = Vocabulary.getResources(locale);
        out.print("Geotoolkit.org ");
        out.println(Utilities.VERSION);
        out.print(resources.getString(Vocabulary.Keys.JAVA_VERSION_1, System.getProperty("java.version")));
        out.print(faint);
        out.print(" (");
        out.print(resources.getString(Vocabulary.Keys.JAVA_VENDOR_1, System.getProperty("java.vendor")));
        out.print(')');
        out.println(normal);
        out.print(resources.getString(Vocabulary.Keys.OS_NAME_1, System.getProperty("os.name")));
        out.print(faint);
        out.print(" (");
        out.print(resources.getString(Vocabulary.Keys.OS_VERSION_2, new String[] {
            System.getProperty("os.version"), System.getProperty("os.arch")
        }));
        out.print(')');
        out.println(normal);
        out.print(resources.getString(Vocabulary.Keys.MEMORY_HEAP_SIZE_1,
                Math.round(Runtime.getRuntime().maxMemory() / (1024 * 1024.0))));
        out.print(" (");
        out.print(resources.getString(Vocabulary.Keys.MAXIMUM));
        out.println(')');
        /*
         * Test for the presence of extensions for which the class may not be on the classpath:
         * JavaDB, JAI, Image I/O extensions for JAI.
         */
        out.println();
        out.print(bold);
        out.print("Extensions:");
        out.println(normal);
        out.flush(); // For allowing user to see what we have done so far while he is waiting.
        for (int i=0; i<3; i++) {
            String header = null;
            Object result = null; // String on success, Throwable on error.
            try {
                switch (i) {
                    default: {
                        throw new AssertionError(i);
                    }
                    case 0: {
                        header = "Embedded Database";
                        Class.forName(Dialect.DERBY.driverClass);
                        final Driver d = DriverManager.getDriver(Dialect.DERBY.protocol);
                        result = Loggings.getResources(locale).getString(
                                Loggings.Keys.JDBC_DRIVER_VERSION_3, "Derby",
                                d.getMajorVersion(), d.getMinorVersion());
                        break;
                    }
                    case 1: {
                        header = "Java Advanced Imaging";
                        result = String.valueOf(Class.forName("javax.media.jai.JAI")
                                .getMethod("getBuildVersion").invoke(null, (Object[]) null));
                        break;
                    }
                    case 2: {
                        header = "Image I/O extensions";
                        final Package p = Package.getPackage("com.sun.media.jai.operator");
                        if (p != null) {
                            result = resources.getString(Vocabulary.Keys.VERSION_1,
                                    p.getImplementationVersion());
                        }
                        break;
                    }
                }
            } catch (ReflectiveOperationException | SQLException e) {
                result = e;
            }
            out.print(header);
            out.print(':');
            out.print(CharSequences.spaces(22 - header.length()));
            if (result instanceof String) {
                out.print(faint);
                out.print((String) result);
                out.println(normal);
            } else {
                out.print(resources.getString(Vocabulary.Keys.NOT_INSTALLED));
                if (result != null) {
                    out.print(faint);
                    out.print(" (");
                    out.print(Classes.getShortClassName(result));
                    out.print(')');
                    out.print(normal);
                }
                out.println();
            }
        }
    }
}
