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
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public interface Dialog {
    /**
     * Shows a dialog box requesting input from the user. The dialog box will be parented to
     * {@code owner}. If {@code owner} is contained into a {@link javax.swing.JDesktopPane},
     * the dialog box will appears as an internal frame.
     * <p>
     * This method can be invoked from any thread (may or may not be the
     * <cite>Swing</cite> thread).
     *
     * @param  owner The parent component for the dialog box, or {@code null} if there is no parent.
     * @param  title The dialog box title.
     * @return {@code true} if user pressed the "Ok" button, or {@code false} otherwise
     *         (e.g. pressing "Cancel" or closing the dialog box from the title bar).
     */
    boolean showDialog(Component owner, String title);
}
