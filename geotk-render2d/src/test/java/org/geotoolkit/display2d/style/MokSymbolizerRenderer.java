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

package org.geotoolkit.display2d.style;

import java.util.stream.Stream;
import org.apache.sis.map.Presentation;
import org.apache.sis.map.MapLayer;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.feature.Feature;

/**
 * Test that symbolizer renderer are properly called and only once.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MokSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedMokSymbolizer>{

    public static volatile int called = 0;

    public MokSymbolizerRenderer(final SymbolizerRendererService service,final CachedMokSymbolizer cached, final RenderingContext2D ctx){
        super(service,cached,ctx);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Feature feature) {
        called++;
        return Stream.empty();
    }

}
