/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.gui.swing.navigator;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface NavigatorModel<T extends Comparable> {

    public static final String TRANSLATE_PROPERTY = "translate";
    public static final String SCALE_PROPERTY = "scale";
    public static final String ORIENTATION_PROPERTY = "orientation";

    T getValueAt(double d);

    double getPosition(T candidate);

    void setScale(double scale);

    double getScale();

    void setTranslation(double tr);

    double getTranslation();

    void setOrientation(int orientation);

    int getOrientation();

    void addPropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener);

    void removePropertyChangeListener(final PropertyChangeListener listener);

    void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener);

}
