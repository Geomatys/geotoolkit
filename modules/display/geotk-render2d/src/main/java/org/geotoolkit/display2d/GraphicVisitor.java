/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d;

import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.renderer.Presentation;

/**
 * Used to explore the graphics send when using the
 * getGraphicIn method from ReferencedCanvas.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
     * Called when a valid graphic has been found.
     *
     * @param graphic : graphic that validate the visiting parameters
     */
    void visit(Presentation graphic, RenderingContext context, SearchArea area);

    /**
     * Should return true when the visitor want to stop the search process.
     * For example the visitor may stop on the first graphic he encounter.
     */
    boolean isStopRequested();

}
