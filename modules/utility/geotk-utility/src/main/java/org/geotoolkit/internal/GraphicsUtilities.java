/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.logging.Logging;


/**
 * A set of utilities related to Swing. More utilities are provided in the
 * {@link org.geotoolkit.internal.swing.SwingUtilities} static class, which is defined in the
 * {@code geotk-widgets-swing} module. Here, we have a more minimalist class defined in the
 * {@code geotk-utility} module only because it is used by other modules that do not depend
 * on the Swing widgets module, as for example {@code geotk-setup} and some test suites.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.0
 * @module
 */
public final class GraphicsUtilities extends Static {
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
                laf = getNimbusLAF();
            } else {
                // Do not change the user-supplied setting.
                return;
            }
        } else if (OS.current() == OS.MAC_OS) {
            // MacOS come with a default L&F which is different than in standard JDK.
            return;
        } else {
            laf = UIManager.getSystemLookAndFeelClassName();
        }
        if (laf.equals(UIManager.getCrossPlatformLookAndFeelClassName()) || laf.contains(".gtk.")) {
            laf = getNimbusLAF(); // Replace Metal L&F by Nimbus L&F.
            // Replaces also the GTK L&F, because it doesn't seem to produce
            // a good result (especially when combined with Swingx).
        }
        if (laf != null) try {
            UIManager.setLookAndFeel(laf);
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            Logging.recoverableException(caller, method, e);
        }
    }

    /**
     * Returns the Nimbus L&F, or {@code null} if not found.
     */
    private static String getNimbusLAF() {
        for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getName().equalsIgnoreCase("Nimbus")) {
                return info.getClassName();
            }
        }
        return null;
    }
}
