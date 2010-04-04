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
public interface NavigatorModel {

    public static final String TRANSFORM_PROPERTY = "transform";

    double getGraphicValueAt(double d);

    double getDimensionValueAt(double candidate);

    void scale(double factor, double position);

    void translate(double tr);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener);

    void removePropertyChangeListener(final PropertyChangeListener listener);

    void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener);

}
