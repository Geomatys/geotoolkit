/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.swing;

import java.awt.Component;
import javax.swing.JToolBar;


/**
 * A toolbar which can enable or disable every buttons. The enabled states is controlled by
 * calls to {@link #setButtonsEnabled(boolean)}:
 * <p>
 * <ol>
 *   <li>When invoked with a value of {@code false}, the current enabled or disabled states
 *       of toolbar buttons are remembered before the buttons are turned into their disabled
 *       states.</li>
 *   <li>When invoked with a value of {@code false}, the enabled or disabled button states
 *       are restored to the values remembered in the previous step.</li>
 * </ol>
 * <p>
 * Note that we don't use {@link #setEnabled(boolean)} method for that, because the later
 * control only the enabled of disabled state of the toolbar - in this case it enable or
 * disable the floating and rollver capabilities. It does not enable or disable the buttons
 * contained in the toolbar.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
@SuppressWarnings("serial")
public final class ToolBar extends JToolBar {
    /**
     * The enabled or disabled states of all buttons in this toolbar, or {@code null}.
     * This is non-null only when {@code setButtonsEnabled(false)} has been invoked.
     */
    private boolean[] states;

    /**
     * Creates a new tool bar with a specified name and orientation.
     *
     * @param title The title of the tool bar.
     * @param orientation Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public ToolBar(final String title, final int orientation) {
        super(title, orientation);
    }

    /**
     * Returns {@code true} if the buttons have been disabled with a call to
     * {@code setButtonsEnabled(false)}.
     *
     * @return {@code true} if the buttons have been disabled, or {@code false} otherwise.
     */
    public boolean getButtonsEnabled() {
        return states == null;
    }

    /**
     * Enables or disables the button in this toolbar.
     *
     * @param enabled {@code true} for enabling the buttons, or {@code false} for disabling it.
     */
    public void setButtonsEnabled(final boolean enabled) {
        if (!enabled) {
            if (states == null) {
                final Component[] components = getComponents();
                states = new boolean[components.length];
                for (int i=0; i<components.length; i++) {
                    final Component c = components[i];
                    states[i] = c.isEnabled();
                    c.setEnabled(false);
                }
            }
        } else if (states != null) {
            final Component[] components = getComponents();
            final int n = Math.min(components.length, states.length);
            for (int i=0; i<n; i++) {
                components[i].setEnabled(states[i]);
            }
            states = null;
        }
    }
}
