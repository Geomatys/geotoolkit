/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
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
package org.geotoolkit.util;

import org.apache.sis.util.Static;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

/**
 * Set of utilities methods for Swing components.
 * TODO maybe move somewhere else.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class SwingUtilities extends Static {

    public static Window windowForComponent(Component component){
        if(component == null){
            return null;
        }else if(component instanceof Window){
            return (Window) component;
        }else{
            return javax.swing.SwingUtilities.windowForComponent(component);
        }
    }

    /**
     * Force divider location for a JSplitPan in percent.
     *
     * @param splitter
     * @param proportion
     * @return
     */
    public static JSplitPane setDividerLocation(final JSplitPane splitter,
                                                final double proportion) {
        if (splitter.isShowing()) {
            if(splitter.getWidth() > 0 && splitter.getHeight() > 0) {
                splitter.setDividerLocation(proportion);
            }
            else {
                splitter.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        splitter.removeComponentListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                });
            }
        }
        else {
            splitter.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 &&
                            splitter.isShowing()) {
                        splitter.removeHierarchyListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                }
            });
        }
        return splitter;
    }

    /**
     * Force divider location for a JSplitPan with int position.
     *
     * @param splitter
     * @param position
     * @return
     */
    public static JSplitPane setDividerLocation(final JSplitPane splitter,
                                                final int position) {
        if (splitter.isShowing()) {
            if(splitter.getWidth() > 0 && splitter.getHeight() > 0) {
                splitter.setDividerLocation(position);
            }
            else {
                splitter.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        splitter.removeComponentListener(this);
                        setDividerLocation(splitter, position);
                    }
                });
            }
        }
        else {
            splitter.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 &&
                            splitter.isShowing()) {
                        splitter.removeHierarchyListener(this);
                        setDividerLocation(splitter, position);
                    }
                }
            });
        }
        return splitter;
    }

    /**
     * Remove all children from a JCompontent and remove all
     * PropertyChangeListeners for all children recursively.
     *
     * @param container
     */
    public static void safeRemoveAll(JComponent container) {
        //remove all children PropertyChangeListeners
        if (container.getComponentCount() > 0) {
            for (Component child : container.getComponents()) {
                removeAllPropertyChangeListeners((JComponent)child);
            }
        }
        //remove all children
        container.removeAll();
    }

    /**
     * Recursively remove all PropertyChangeListeners from a component
     * @param container
     */
    public static void removeAllPropertyChangeListeners(JComponent component) {
        if (component.getComponentCount() > 0) {
            for (Component child : component.getComponents()) {
                if (child instanceof JComponent) {
                    removeAllPropertyChangeListeners((JComponent)child);
                }
            }
        }

        PropertyChangeListener[] tmpList = component.getPropertyChangeListeners();
        for (PropertyChangeListener listener : tmpList) {
            component.removePropertyChangeListener(listener);
        }
    }

    /**
     * Create a darker color using a factor.
     *
     * @param color
     * @param factor
     * @return darker color
     */
    public static Color darker(final Color color, float factor) {
        return new Color(Math.max((int)(color.getRed()  *factor), 0),
                Math.max((int)(color.getGreen()*factor), 0),
                Math.max((int)(color.getBlue() *factor), 0));
    }

    /**
     * Create a lighter color using a factor.
     *
     * @param color
     * @param factor
     * @return lighter color
     */
    public static Color lighter(final Color color, float factor) {
        return new Color(Math.max((int)(color.getRed()  /factor), 0),
                Math.max((int)(color.getGreen()/factor), 0),
                Math.max((int)(color.getBlue() /factor), 0));
    }

    /**
     * Apply bold font to a Jlabel.
     * @param label
     */
    public static void bold(final JLabel label) {
        final Font font = label.getFont();
        label.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
    }
}
