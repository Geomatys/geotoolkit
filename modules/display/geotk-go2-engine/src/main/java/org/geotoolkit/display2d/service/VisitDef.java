/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.service;

import java.awt.Shape;
import org.geotoolkit.display.canvas.GraphicVisitor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class VisitDef {

    private Shape area = null;
    private GraphicVisitor visitor = null;

    public VisitDef(){}

    public VisitDef(Shape area, GraphicVisitor visitor){
        setArea(area);
        setVisitor(visitor);
    }

    public void setArea(Shape area) {
        this.area = area;
    }

    public Shape getArea() {
        return area;
    }

    public void setVisitor(GraphicVisitor visitor) {
        this.visitor = visitor;
    }

    public GraphicVisitor getVisitor() {
        return visitor;
    }

    @Override
    public String toString() {
        return "VisitDef[area="+ area +", visitor="+ visitor +"]";
    }

}
