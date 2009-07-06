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
package org.geotoolkit.display2d.ext.vectorfield;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;

import org.geotoolkit.util.logging.Logging;
import org.opengis.display.canvas.Canvas;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Graphic builder for Coverages to be displayed with
 * arrows or cercles.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class GridMarkGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    private static final Logger LOGGER = Logging.getLogger(GridMarkGraphicBuilder.class);

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas cvs) {

        if( !(cvs instanceof ReferencedCanvas2D) ){
            throw new IllegalArgumentException("Illegal canvas, must be a ReferencedCanvas2D");
        }

        final ReferencedCanvas2D canvas = (ReferencedCanvas2D) cvs;

        if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer coverageLayer = (CoverageMapLayer) layer;
            
            
            //TODO fix to use the coveragereader
//            try {
//                feature = layer.getFeatureSource().getFeatures().features().next();
//            } catch (IOException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            }
//            
//            GridCoverage2D coverage = (GridCoverage2D) feature.getProperty("grid").getValue();
            GridCoverage2D coverage = null;
            try {
                //get the default gridcoverage
                coverage = coverageLayer.getCoverageReader().read(null);
            } catch (FactoryException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (TransformException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            
            if(coverage != null){
                final RenderedGridMarks marks = new RenderedGridMarks(canvas,coverage);
                final Collection<GraphicJ2D> graphics = new ArrayList<GraphicJ2D>();
                graphics.add(marks);
                return graphics;
            }else{
                return Collections.emptyList();
            }

        }else{
            return Collections.emptyList();
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }
    
}
