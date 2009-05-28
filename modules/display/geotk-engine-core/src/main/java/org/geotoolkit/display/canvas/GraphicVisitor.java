/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
import org.opengis.display.primitive.Graphic;

/**
 * Used to explore the graphics send when using the
 * getGraphicIn method from ReferencedCanvas.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface GraphicVisitor {

    /**
     * Called when the visiting process starts
     */
    void startVisit();

    /**
     * Called when the visiting process ended
     */
    void endVisit();

    /**
     * Called when a valide graphic has been found.
     *
     * @param graphic : graphic that validate the visiting parameters
     */
    void visit(Graphic graphic, Shape area);

    /**
     * Should return true when the visitor want to stop the search process.
     * For exemple the visitor may stop on the first graphic he uncounter.
     */
    boolean isStopRequested();

}
