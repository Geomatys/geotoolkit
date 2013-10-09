/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.gui.swing.referencing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;


/**
 * Renderer for an authority code in a {@link javax.swing.JComboBox}.
 *
 * {@note Don't extends <code>JComponent</code> - we need <code>JPanel</code> otherwise the
 * value given to <code>ComboBoxModel.setSelectedItem(Object)</code> does not appear in the
 * combo box. My guess is that a UI is required for allowing the event to be propagated.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @see javax.swing.plaf.basic.BasicComboBoxRenderer
 *
 * @since 3.12
 * @module
 */
@SuppressWarnings("serial")
final class AuthorityCodeRenderer extends JPanel implements ListCellRenderer<AuthorityCode> {
    /**
     * The original renderer.
     */
    private final ListCellRenderer<Object> renderer;

    /**
     * The component which display the name.
     */
    private JLabel name;

    /**
     * The component used for rendering the code.
     */
    private final JLabel code;

    /**
     * The authority code selected for rendering.
     */
    private AuthorityCode selected;

    /**
     * Creates a new renderer wrapping the given original renderer.
     * The original renderer <strong>must</strong> be an instance of {@link JLabel},
     * otherwise don't use this {@code AuthorityCodeRenderer} class.
     *
     * @param original The original renderer.
     */
    AuthorityCodeRenderer(final ListCellRenderer<Object> renderer, final String prototype) {
        super(new BorderLayout());
        this.renderer = renderer;
        code = new JLabel(prototype, JLabel.TRAILING);
        code.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 15));
        code.setPreferredSize(code.getPreferredSize()); // Freeze the size.
        code.setForeground(Color.GRAY);
        add(name = (JLabel) renderer, BorderLayout.CENTER);
        add(code, BorderLayout.LINE_END);
    }

    /**
     * Return a component that has been configured to display the specified value.
     * Note that it is recommended to fix the list cell size to a fixed value in
     * order to avoid invoking this method for computing the preferred size.
     */
    @Override
    public Component getListCellRendererComponent(final JList<? extends AuthorityCode> list,
            final AuthorityCode value, final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        selected = value;
        final String id = (selected != null) ? selected.code : null;
        final Component c = renderer.getListCellRendererComponent(list, id, index, isSelected, cellHasFocus);
        if (c != name) { // Does not occur in typical Swing implementations, but checked anyway for safety.
            remove(name);
            add(name = (JLabel) c, BorderLayout.CENTER);
        }
        code.setBackground(c.getBackground());
        code.setText(id);
        return this;
    }

    /**
     * Before to paint the component, set the real text that we want to render.
     * We do that in order to invoke {@link AuthorityCode#toString()} as late as possible.
     */
    @Override
    public void paint(final Graphics g) {
        if (selected != null) {
            name.setText(selected.toString());
            if (selected.failure()) {
                name.setForeground(Color.RED);
            }
        }
        super.paint(g);
    }
}
