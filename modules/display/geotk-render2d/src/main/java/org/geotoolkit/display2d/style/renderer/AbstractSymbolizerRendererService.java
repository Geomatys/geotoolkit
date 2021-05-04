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
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display2d.style.CachedSymbolizer;
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

    protected Object mimicObject(MapLayer layer) {
        if (layer == null) return null;
        final Resource resource = layer.getData();
        if (resource instanceof FeatureSet) {
            try {
                FeatureType ft = ((FeatureSet) resource).getType();
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
