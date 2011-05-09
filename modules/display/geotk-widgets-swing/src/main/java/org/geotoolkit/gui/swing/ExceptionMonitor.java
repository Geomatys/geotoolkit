/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import net.jcip.annotations.ThreadSafe;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Exceptions;


/**
 * Displays exception messages in a <cite>Swing</cite> component. The standard
 * {@link java.lang.Exception} class contains methods which write the exception
 * to the error console. This {@code ExceptionMonitor} class adds static methods
 * which make the message, and eventually the exception trace, appear in a widget
 * component.
 *
 * <p>&nbsp;</p>
 * <p align="center"><img src="doc-files/ExceptionMonitor.png"></p>
 * <p>&nbsp;</p>
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @version 3.11
 *
 * @see org.jdesktop.swingx.JXErrorPane
 * @see Exceptions
 *
 * @since 1.0
 * @module
 *
 * @deprecated Methods moved to {@link Exceptions}.
 */
@Deprecated
@ThreadSafe
public final class ExceptionMonitor extends Static {
    /**
     * The creation of {@code ExceptionMonitor} class objects is forbidden.
     */
    private ExceptionMonitor() {
    }

    /**
     * Displays an error message for the specified exception. Note that this method can
     * be called from any thread (not necessarily the <cite>Swing</cite> thread).
     *
     * @param owner Component in which the exception is produced, or {@code null} if unknown.
     * @param exception Exception which has been thrown and is to be reported to the user.
     */
    public static void show(final Component owner, final Throwable exception) {
        show(owner, exception, null);
    }

    /**
     * Displays an error message for the specified exception. Note that this method can
     * be called from any thread (not necessarily the <cite>Swing</cite> thread).
     *
     * @param owner Component in which the exception is produced, or {@code null} if unknown.
     * @param exception Exception which has been thrown and is to be reported to the user.
     * @param message Message to display. If this parameter is null, then
     *        {@link Exception#getLocalizedMessage} will be called to obtain the message.
     */
    public static void show(final Component owner, final Throwable exception, final String message) {
        org.geotoolkit.internal.swing.ExceptionMonitor.show(owner, exception, message);
    }
}
