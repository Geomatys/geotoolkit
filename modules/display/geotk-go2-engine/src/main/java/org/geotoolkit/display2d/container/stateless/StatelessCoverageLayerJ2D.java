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
package org.geotoolkit.display2d.container.stateless;


import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotools.coverage.io.CoverageReadParam;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.map.GraphicBuilder;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Single object to represent a complete mapcontext.
 * This is a Stateless graphic object.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatelessCoverageLayerJ2D extends GraphicCoverageJ2D{

    private static final Logger LOGGER = Logger.getLogger(StatelessCoverageLayerJ2D.class.getName());
    
    private final CoverageMapLayer layer;
    private CoverageReadParam lastParam = null;
    private SoftReference<GridCoverage2D> cachedCoverage = null;
    
    public StatelessCoverageLayerJ2D(ReferencedCanvas2D canvas, CoverageMapLayer layer){
        super(canvas, layer.getBounds().getCoordinateReferenceSystem());
        this.layer = layer;
        
        try {
            setEnvelope(layer.getBounds());
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "",ex);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(final RenderingContext2D renderingContext) {
        
        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) return;        
             
        final Name coverageName = layer.getCoverageName();
        final List<CachedRule> rules = GO2Utilities.getValidCachedRules(layer.getStyle(), renderingContext.getScale(), coverageName);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.isEmpty()) {
            return;   //----------------------------------------------------->CONTINUE
        }

        paintRasterLayer(layer, rules, renderingContext);
    }

    private void paintRasterLayer(final CoverageMapLayer layer, final List<CachedRule> rules, 
            final RenderingContext2D renderingContext) {

        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) layer.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer has a special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> graphics = builder.createGraphics(layer, canvas);
            for(GraphicJ2D gra : graphics){
                gra.paint(renderingContext);
            }
            return;
        }
        
        if(!intersects(renderingContext.getCanvasObjectiveBounds())){
            //grid not in the envelope, we have finisehd
            return;
        }
        
        for(final CachedRule rule : rules){
//          final Filter filter = rule.getFilter();
            //test if the rule is valid for this feature
//          if(filter == null  || filter.evaluate(feature)){
                final List<CachedSymbolizer> symbols = rule.symbolizers();

                for(final CachedSymbolizer symbol : symbols){
                    try {
                        GO2Utilities.portray(this, symbol, renderingContext);
                    } catch (PortrayalException ex) {
                        renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
                    }
                }
//            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {

        if(!(context instanceof RenderingContext2D) ) return graphics;
        if(!layer.isSelectable())                     return graphics;
        if(!layer.isVisible())                        return graphics;

        final RenderingContext2D renderingContext = (RenderingContext2D) context;

        final Name coverageName = layer.getCoverageName();
        final List<CachedRule> rules = GO2Utilities.getValidCachedRules(layer.getStyle(), renderingContext.getScale(), coverageName);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.isEmpty()) {
            return graphics;
        }

        if(graphics == null) graphics = new ArrayList<Graphic>();
        graphics = searchAt(layer,rules,renderingContext,mask,filter,graphics);

        return graphics;
    }

    private List<Graphic> searchAt(final CoverageMapLayer layer, final List<CachedRule> rules,
            final RenderingContext2D renderingContext, final SearchArea mask, VisitFilter filter, List<Graphic> graphics) {

        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) layer.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer hasa special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> gras = builder.createGraphics(layer, canvas);
            for(final GraphicJ2D gra : gras){
                graphics = gra.getGraphicAt(renderingContext, mask, filter,graphics);
            }
            return graphics;
        }
       

        for (final CachedRule rule : rules) {
            final List<CachedSymbolizer> symbols = rule.symbolizers();
            for (final CachedSymbolizer symbol : symbols) {
                if(GO2Utilities.hit(this, symbol, renderingContext, mask, filter)){
                    graphics.add(this);
                    break;
                }
            }
        }

        return graphics;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageMapLayer getUserObject() {
        return layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() {
        return super.getEnvelope();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridCoverage2D getGridCoverage(CoverageReadParam param)
        throws FactoryException,IOException,TransformException{
        lastParam = param;
        GridCoverage2D coverage = layer.getCoverageReader().read(param);
        cachedCoverage = new SoftReference<GridCoverage2D>(coverage);
        return coverage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridCoverage2D getElevationCoverage(CoverageReadParam param)
        throws FactoryException,IOException,TransformException{
        final ElevationModel elevationModel = layer.getElevationModel();
        
        if(layer.getElevationModel() != null){
            GridCoverage2D cache = cachedCoverage.get();
            if( cache != null
                && param.equals(lastParam)
                && layer.getCoverageReader().equals(elevationModel.getCoverageReader())){
                //same parameter and data model equals elevation model
                return cache;
            }else{
                return layer.getElevationModel().getCoverageReader().read(param);
            }

        }else{
            return null;
        }
    }

}
