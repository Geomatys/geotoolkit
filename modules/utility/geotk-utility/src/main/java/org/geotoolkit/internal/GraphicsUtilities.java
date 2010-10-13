/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.awt.Graphics2D;
import javax.swing.UIManager;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.logging.Logging;


/**
 * A set of utilities methods for painting in a {@link Graphics2D} handle.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.16
 *
 * @since 2.0
 * @module
 */
@Static
public final class GraphicsUtilities {
    /**
     * Number of spaces to leave between each tab.
     */
    private static final int TAB_WIDTH = 4;

    /**
     * The creation of {@code GraphicsUtilities} class objects is forbidden.
     */
    private GraphicsUtilities() {
    }

    /**
     * Sets the Swing Look and Feel to the default value used in Geotk. This method exists
     * in order to have a central place where this setting can be performed, so we can change
     * the setting in a consistent fashion for the whole library.
     *
     * @param caller The class calling this method. Used only for logging purpose.
     * @param method The method invoking this one.  Used only for logging purpose.
     */
    public static void setLookAndFeel(final Class<?> caller, final String method) {
        String laf = System.getProperty("swing.defaultlaf"); // Documented in UIManager.
        if (laf != null) {
            if (laf.equalsIgnoreCase("Nimbus")) {
                laf = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
            } else {
                // Do not change the user-supplied setting.
                return;
            }
        } else if (OS.MAC_OS.equals(OS.current())) {
            // MacOS come with a default L&F which is different than in standard JDK.
            return;
        } else {
            laf = UIManager.getSystemLookAndFeelClassName();
        }
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            Logging.recoverableException(caller, method, e);
        }
    }
}
