/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.display2d.style.renderer;


import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.FeatureType;
import org.opengis.style.Symbolizer;

/**
 * Abstract symbolizer renderer service, will redirect most call on using
 * createRenderer and use it.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractSymbolizerRendererService<S extends Symbolizer, C extends CachedSymbolizer<S>> implements SymbolizerRendererService<S, C>{

    /**
     * Returns the standard glyph size : 30x24
     */
    @Override
    public Rectangle2D glyphPreferredSize(final C symbol, final MapLayer layer) {
        return new Rectangle2D.Double(0, 0, 30, 24);
    }

    @Override
    public boolean portray(final ProjectedObject graphic, final C symbol,
            final RenderingContext2D context) throws PortrayalException {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        return renderer.portray(graphic);
    }

    @Override
    public boolean portray(final Iterator<? extends ProjectedObject> graphics,
            final C symbol, final RenderingContext2D context) throws PortrayalException {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        return renderer.portray(graphics);
    }

    @Override
    public boolean portray(final ProjectedCoverage graphic, final C symbol,
            final RenderingContext2D context) throws PortrayalException {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        return renderer.portray(graphic);
    }

    @Override
    public boolean hit(final ProjectedObject graphic, final C symbol,
            final RenderingContext2D context, final SearchAreaJ2D mask, final VisitFilter filter) {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        return renderer.hit(graphic, mask, filter);
    }

    @Override
    public boolean hit(final ProjectedCoverage graphic, final C symbol, final RenderingContext2D context, final SearchAreaJ2D mask, final VisitFilter filter) {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        return renderer.hit(graphic, mask, filter);
    }

    protected Object mimicObject(MapLayer layer) {
        if(layer instanceof FeatureMapLayer){
            try {
                FeatureType ft = ((FeatureMapLayer)layer).getResource().getType();
                if(ft.isAbstract()) {
                    final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                    ftb.setSuperTypes(ft);
                    ftb.setName(ft.getName());
                    ft = ftb.build();
                }
                return ft.newInstance();
            } catch (DataStoreException ex) {
                return null;
            }
        }else{
            return null;
        }
    }

}
