/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2009, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.container.statefull;

import java.awt.geom.AffineTransform;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class StatefullContextParams {

    public final FeatureMapLayer layer;
    public final AffineTransform objectiveToDisplay = new AffineTransform();
    public final GeometryCoordinateSequenceTransformer dataToObjectiveTransformer = new GeometryCoordinateSequenceTransformer();
    public final GeometryCoordinateSequenceTransformer dataToDisplayTransformer = new GeometryCoordinateSequenceTransformer();
    public boolean decimate = false;
    public double decimation = 0;

    public MathTransform dataToObjective = null;

    StatefullContextParams(FeatureMapLayer layer){
        this.layer = layer;
    }

}
