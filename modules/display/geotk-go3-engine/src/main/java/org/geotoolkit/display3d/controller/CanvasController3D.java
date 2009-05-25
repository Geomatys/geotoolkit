/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.display3d.controller;

import org.opengis.display.canvas.CanvasController;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public interface CanvasController3D extends CanvasController{

    double[] getCameraPosition();

    void setCameraPosition(double x, double y, double z);

    void setCameraSpeed(double speed);

    CoordinateReferenceSystem getObjectiveCRS();

    void addLocationSensitiveGraphic(LocationSensitiveGraphic graphic, double distance);

}
