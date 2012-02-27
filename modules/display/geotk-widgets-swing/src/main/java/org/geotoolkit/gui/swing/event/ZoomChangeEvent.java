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
package org.geotoolkit.gui.swing.event;

import java.util.EventObject;
import java.awt.geom.AffineTransform;


/**
 * An event which indicates that a zoom occurred in a component.
 * This event is fired by {@link org.geotoolkit.gui.swing.ZoomPane}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class ZoomChangeEvent extends EventObject {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5063317286699888858L;

    /**
     * An affine transform indicating the zoom change. If {@code oldZoom} and {@code newZoom}
     * are the affine transforms before and after the change respectively, then the following
     * relation must hold (within the limits of rounding error):
     *
     * {@preformat java
     *     newZoom = oldZoom.concatenate(change)
     * }
     */
    private final AffineTransform change;

    /**
     * Constructs a new event. If {@code oldZoom} and {@code newZoom} are the affine transforms
     * before and after the change respectively, then the following relation must hold (within
     * the limits of rounding error):
     *
     * {@preformat java
     *     newZoom = oldZoom.concatenate(change)
     * }
     *
     * @param source The event source (usually a {@link org.geotoolkit.gui.swing.ZoomPane}).
     * @param change An affine transform indicating the zoom change.
     */
    public ZoomChangeEvent(final Object source, final AffineTransform change) {
        super(source);
        this.change = change;
    }

    /**
     * Returns the affine transform indicating the zoom change.
     * <strong>Note:</strong> for performance reasons, this method does not clone
     * the returned transform. Do not change!
     *
     * @return The zoom change as an affine transform (<strong>not</strong> cloned).
     */
    public AffineTransform getChange() {
        return change;
    }
}
