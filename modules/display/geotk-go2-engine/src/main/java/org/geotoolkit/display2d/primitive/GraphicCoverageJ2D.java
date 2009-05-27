/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.primitive;

import java.io.IOException;
import java.util.List;

import org.geotools.coverage.io.CoverageReadParam;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.CoverageMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class GraphicCoverageJ2D extends GraphicJ2D {


    public GraphicCoverageJ2D(ReferencedCanvas2D canvas, CoordinateReferenceSystem crs){
        super(canvas, crs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        //todo test if it hits the given area
        return graphics;
    }

    public abstract GridCoverage2D getGridCoverage(CoverageReadParam param)
            throws FactoryException,IOException,TransformException;

    public abstract GridCoverage2D getElevationCoverage(CoverageReadParam param)
            throws FactoryException,IOException,TransformException;

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract CoverageMapLayer getUserObject();

    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(RenderingContext2D context) {
        //do nothing, the renderer is in charge of painting this graphic object
    }

}
