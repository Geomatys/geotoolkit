/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import java.awt.Component;


/**
 * Interface for widgets that can be used as a dialog box.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public interface Dialog {
    /**
     * Shows a dialog box requesting input from the user. The dialog box will be parented to
     * {@code owner}. If {@code owner} is contained into a {@link javax.swing.JDesktopPane},
     * the dialog box will appears as an internal frame.
     *
     * {@section Multi-threading}
     * Geotoolkit implementations allow this method to be invoked from any thread. If the caller
     * thread is not the <cite>Swing</cite> thread, then the execution of this method will be
     * registered in the AWT Event Queue and the caller thread will block until completion.
     *
     * @param  owner The parent component for the dialog box, or {@code null} if there is no parent.
     * @param  title The dialog box title.
     * @return {@code true} if user pressed the "<cite>Ok</cite>" button, or {@code false} otherwise
     *         (e.g. pressing "<cite>Cancel</cite>" or closing the dialog box from the title bar).
     */
    boolean showDialog(Component owner, String title);
}
