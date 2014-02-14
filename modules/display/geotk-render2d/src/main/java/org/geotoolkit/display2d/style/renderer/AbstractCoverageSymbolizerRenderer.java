/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.logging.Level;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.opengis.coverage.Coverage;
import org.opengis.feature.GeometryAttribute;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Symbolizer;

/**
 * Abstract renderer for symbolizer which only apply on coverages datas.
 * This class will take care to implement the coverage hit method.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCoverageSymbolizerRenderer<C extends CachedSymbolizer<? extends Symbolizer>> extends AbstractSymbolizerRenderer<C>{


    public AbstractCoverageSymbolizerRenderer(final SymbolizerRendererService service, final C symbol, final RenderingContext2D context){
        super(service, symbol,context);
    }

    @Override
    public void portray(final ProjectedObject graphic) throws PortrayalException {
        if(graphic instanceof ProjectedFeature){
            final ProjectedFeature pf = (ProjectedFeature) graphic;
            final String geomName = symbol.getSource().getGeometryPropertyName();
            final Object obj;
            if(geomName == null || geomName.isEmpty()){
                final GeometryAttribute att = pf.getCandidate().getDefaultGeometryProperty();
                obj = (att!=null) ? att.getValue() : null;
            }else{
                obj = GO2Utilities.evaluate(GO2Utilities.FILTER_FACTORY.property(geomName), pf.getCandidate(), null, null);
            }
            if(obj instanceof GridCoverage2D){
                final CoverageMapLayer ml = MapBuilder.createCoverageLayer((GridCoverage2D)obj, GO2Utilities.STYLE_FACTORY.style(), "");
                final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(),ml);
                params.update(renderingContext);
                final ProjectedCoverage pc = new ProjectedCoverage(params, ml);
                portray(pc);
            }
        }
    }

    @Override
    public boolean hit(final ProjectedObject graphic, final SearchAreaJ2D mask, final VisitFilter filter) {
        if(graphic instanceof ProjectedFeature){
            final ProjectedFeature pf = (ProjectedFeature) graphic;
            final Object obj = GO2Utilities.evaluate(GO2Utilities.FILTER_FACTORY.property(
                    symbol.getSource().getGeometryPropertyName()), pf.getCandidate(), null, null);
            if(obj instanceof GridCoverage2D){
                final CoverageMapLayer ml = MapBuilder.createCoverageLayer((GridCoverage2D)obj, GO2Utilities.STYLE_FACTORY.style(), "");
                final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(),ml);
                params.update(renderingContext);
                final ProjectedCoverage pc = new ProjectedCoverage(params, ml);
                return hit(pc,mask,filter);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedCoverage projectedCoverage, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();
        final Shape[] shapes;
        try {
            shapes = projectedCoverage.getEnvelopeGeometry().getDisplayShape();
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return false;
        }

        for(Shape shape : shapes){
            final Area area = new Area(mask);
            switch(filter){
                case INTERSECTS :
                    area.intersect(new Area(shape));
                    if(!area.isEmpty()) return true;
                    break;
                case WITHIN :
                    Area start = new Area(area);
                    area.add(new Area(shape));
                    if(start.equals(area)) return true;
                    break;
            }
        }

        return false;
    }

}
