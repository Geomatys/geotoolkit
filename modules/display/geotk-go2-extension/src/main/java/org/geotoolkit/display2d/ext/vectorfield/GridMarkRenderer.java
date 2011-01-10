/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.ext.vectorfield;


import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;


/**
 * Renderer for vector field arrows.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GridMarkRenderer extends AbstractCoverageSymbolizerRenderer<CachedVectorFieldSymbolizer>{

    public GridMarkRenderer(final CachedVectorFieldSymbolizer symbol, final RenderingContext2D context){
        super(symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage graphic) throws PortrayalException {

        final GeneralEnvelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        bounds.setCoordinateReferenceSystem(renderingContext.getObjectiveCRS());
        final double[] resolution = renderingContext.getResolution();
        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(bounds);
        param.setResolution(resolution);

        GridCoverage2D coverage;
        try {
            coverage = graphic.getCoverage(param);
        } catch (CoverageStoreException ex) {
            throw new PortrayalException(ex);
        }

        final J2DCanvas canvas = renderingContext.getCanvas();

        if(coverage != null){
            final RenderedGridMarks marks = new RenderedGridMarks(canvas,coverage);
            synchronized(marks.getTreeLock()){
                marks.paint(renderingContext);
            }
        }

    }

}
