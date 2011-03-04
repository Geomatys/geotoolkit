/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.CollectionMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.statefull.StatefullContextParams;
import org.geotoolkit.display2d.primitive.DefaultProjectedObject;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.util.collection.CloseableIterator;

import org.opengis.display.primitive.Graphic;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;


/**
 * Single object to represent a collection map layer
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 * @module pending
 */
public class StatelessCollectionLayerJ2D extends StatelessMapLayerJ2D<CollectionMapLayer>{

    private final StatefullContextParams params;

    public StatelessCollectionLayerJ2D(final J2DCanvas canvas, final CollectionMapLayer layer){
        super(canvas, layer);
        params = new StatefullContextParams(canvas,layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void paintLayer(final RenderingContext2D context) {
        params.update(context);

        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //let the parent class handle it
            super.paintLayer(context);
            return;
        }

        final CanvasMonitor monitor = context.getMonitor();
        final Collection<?> collection = item.getCollection();
        final Id selected = item.getSelectionFilter();
        final Style selectedStyle = item.getStyle();
        final Style baseStyle = item.getStyle();

        final Iterator<?> ite = collection.iterator();
        try{
            while(ite.hasNext()){
                final Object candidate = ite.next();

                if(monitor.stopRequested()){
                    return;
                }

                final Style style;
                if(selected != null && selected.evaluate(candidate)){
                    //paint with selected style
                    style = selectedStyle;
                }else{
                    //paint with normal style
                    style = baseStyle;
                }

                try {
                    paintObject(candidate, style, context);
                } catch (PortrayalException ex) {
                    monitor.exceptionOccured(ex, Level.WARNING);
                    return;
                }
            }
        }finally{
            if(ite instanceof CloseableIterator){
                ((CloseableIterator)ite).close();
            }
        }

    }

    private void paintObject(final Object object, final Style style, 
            final RenderingContext2D context) throws PortrayalException{
        for(final FeatureTypeStyle fts : style.featureTypeStyles()){
            for(final Rule rule : fts.rules()){
                final Filter filter = rule.getFilter();
                if(filter == null || filter.evaluate(object)){
                    for(final Symbolizer symbolizer : rule.symbolizers()){
                        paintObject(object, symbolizer, context);
                    }
                }
            }
        }
    }

    private void paintObject(final Object object, final Symbolizer symbolizer, 
            final RenderingContext2D context) throws PortrayalException{
        final CachedSymbolizer cached = GO2Utilities.getCached(symbolizer);
        final SymbolizerRenderer renderer = cached.getRenderer().createRenderer(cached, context);
        final ProjectedObject projected = new DefaultProjectedObject(params, object);
        renderer.portray(projected);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, 
            final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {
        return graphics;
    }

}
