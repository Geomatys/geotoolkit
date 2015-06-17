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
package org.geotoolkit.internal.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JComponent;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.geotoolkit.resources.Vocabulary;


/**
 * A panel containing a "width" and a "height" field, typically for image size.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class SizeFields extends JComponent implements ChangeListener {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 174936953329285487L;

    /**
     * The field for selecting the width of target tiles.
     */
    private final JSpinner width;

    /**
     * The field for selecting the height of target tiles.
     */
    private final JSpinner height;

    /**
     * {@code true} if the width and height are in the process of being adjusted together.
     * This is used in order to fire only one change event instead than two.
     */
    private transient boolean isAdjusting;

    /**
     * Creates a new panel initialized to the given value.
     *
     * @param locale  The locale to use for creating the panel.
     * @param size    The initial value to display in the fields.
     * @param minSize The minimal size allowed.
     */
    public SizeFields(final Locale locale, final Dimension size, final Dimension minSize) {
        setLayout(new GridBagLayout());
        width  = new JSpinner((new SpinnerNumberModel(size.width,  minSize.width,  null, 1)));
        height = new JSpinner((new SpinnerNumberModel(size.height, minSize.height, null, 1)));
        final Vocabulary resources = Vocabulary.getResources(locale);
        setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.TilesSize)));
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx=0; c.insets.left=9;
        c.gridy=0; add(new JLabel(resources.getLabel(Vocabulary.Keys.Width)),  c);
        c.gridy++; add(new JLabel(resources.getLabel(Vocabulary.Keys.Height)), c);
        c.gridx++; c.weightx=1; c.insets.left=3; c.insets.right=9;
        c.gridy=0; add(width,  c);
        c.gridy++; add(height, c);
        width .addChangeListener(this);
        height.addChangeListener(this);
    }

    /**
     * Returns the size value currently defined by the user.
     *
     * @return The current size value.
     */
    public Dimension getSizeValue() {
        return new Dimension(((Number) width .getValue()).intValue(),
                             ((Number) height.getValue()).intValue());
    }

    /**
     * Sets the size displayed in the fields to the given value.
     *
     * @param size The value to be displayed in the fields.
     */
    public void setSizeValue(final Dimension size) {
        isAdjusting = true;
        try {
            width .setValue(size.width);
            height.setValue(size.height);
        } finally {
            isAdjusting = false;
        }
        fireStateChanged();
    }

    /**
     * Adds a listener to notify of changes.
     *
     * @param listener The listener to add.
     */
    public void addChangeListener(final ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener The listener to remove.
     */
    public void removeChangeListener(final ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    /**
     * Invoked every time a change in width or height occurs. This method is public
     * as an implementation side-effect, but should never be invoked directly.
     *
     * @param event The change event.
     */
    @Override
    public void stateChanged(final ChangeEvent event) {
        // TODO: If we want to link the change of width and height, do it here.
        if (!isAdjusting) {
            fireStateChanged();
        }
    }

    /**
     * Invoked when the dimension changed.
     */
    private void fireStateChanged() {
        ChangeEvent event = null;
        final Object[] listeners = listenerList.getListenerList();
        for (int i=listeners.length; (i-=2)>=0;) {
            if (listeners[i] == ChangeListener.class) {
                if (event == null) {
                    event = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i+1]).stateChanged(event);
            }
        }
    }
}
