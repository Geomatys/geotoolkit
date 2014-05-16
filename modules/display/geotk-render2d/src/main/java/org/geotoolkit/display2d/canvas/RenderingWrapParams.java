/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2014, Geomatys
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

package org.geotoolkit.display2d.canvas;

import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.geometry.DirectPosition;

/**
 * World wrap repetition informations.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class RenderingWrapParams {
    
    /**
     * Number of repetition on the decreasing direction.
     */
    public int wrapDecNb;
    /**
     * Number of repetition on the increasing direction.
     */
    public int wrapIncNb;
    
    /**
     * The wrap points in objective CRS.
     * for example in CRS:84 , those points would be :
     * - Point(-180,0)
     * - Point(+180,0)
     */
    public DirectPosition[] wrapPoints = null;
    /**
     * Perpendicular line on the lowest wrap point.
     */
    public com.vividsolutions.jts.geom.LineString wrapDecLine = null;
    /**
     * Perpendicular line on the highest wrap point.
     */
    public com.vividsolutions.jts.geom.LineString wrapIncLine = null;
    /**
     * Polygon containing the valid area, between wrap lines.
     */
    public com.vividsolutions.jts.geom.Polygon wrapArea = null;
    /**
     * Envelope in Objective CRS of the visible area, NOT clipped to crs wraparound.
     * This is a simple affinetransform of the canvas rectangle to objective with the
     * Display to Objective transform.
     */
    public com.vividsolutions.jts.geom.Envelope objectiveJTSEnvelope = null;
    
    /**
     * Normal objective to display transform.
     */
    public AffineTransform2D wrapObjToDisp = null;
    /**
     * Normal Objective transform.
     */
    public AffineTransform2D wrapObj = null;
    
    /**
     * Objective to display transforms on the decreasing side of the wrap area.
     * Size : wrapDecNb + 1 
     * The +1 is to additional transform in case a geometry 
     * overlaps a meridian which cause an additional repetition
     */
    public AffineTransform2D[] wrapDecObjToDisp = null;
    /**
     * Objective to display transforms on the increasing side of the wrap area.
     * Size : wrapIncNb + 1 
     * The +1 is to additional transform in case a geometry 
     * overlaps a meridian which cause an additional repetition
     */
    public AffineTransform2D[] wrapIncObjToDisp = null;
    /**
     * Objective transforms on the decreasing side of the wrap area.
     * Size : wrapDecNb + 1 
     * The +1 is to additional transform in case a geometry 
     * overlaps a meridian which cause an additional repetition
     */
    public AffineTransform2D[] wrapDecObj = null;
    /**
     * Objective transforms on the increasing side of the wrap area.
     * Size : wrapIncNb + 1 
     * The +1 is to additional transform in case a geometry 
     * overlaps a meridian which cause an additional repetition
     */
    public AffineTransform2D[] wrapIncObj = null;
    
}
