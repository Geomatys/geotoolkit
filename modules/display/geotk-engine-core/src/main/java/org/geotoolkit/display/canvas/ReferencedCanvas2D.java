/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display.canvas;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.opengis.display.canvas.Canvas;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

/**
 * A canvas implementation with default support for two-dimensional CRS management. This
 * default implementation uses <cite>Java2D</cite> geometry objects like {@link Shape} and
 * {@link AffineTransform}, which are somewhat lightweight objects. There is no dependency
 * toward AWT toolkit in this class (which means that this class can be used as a basis for
 * SWT renderer as well), and this class does not assume a rectangular widget.
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public interface ReferencedCanvas2D extends Canvas{

    public static final String OBJECTIVE_CRS_PROPERTY = "ObjectiveCRS";

    public static final String OBJECTIVE_TO_DISPLAY_PROPERTY = "ObjectiveToDisplay";

    @Override
    CanvasController2D getController();

    void setObjectiveCRS(CoordinateReferenceSystem objective) throws TransformException;

    CoordinateReferenceSystem getObjectiveCRS();

    /**
     * @return The 2D composant part of the objective CRS.
     */
    CoordinateReferenceSystem getObjectiveCRS2D();

    DerivedCRS getDisplayCRS();

    AffineTransform2D getObjectiveToDisplay();

    Rectangle2D getDisplayBounds();
    
}
