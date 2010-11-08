

package org.geotoolkit.pending.demo.rendering.customsymbolizer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.MapLayer;

public class CrystallizeSymbolizerRendererService extends AbstractSymbolizerRendererService<CrystallizeSymbolizer, CrystallizeCachedSymbolizer> {

    @Override
    public Class<CrystallizeSymbolizer> getSymbolizerClass() {
        return CrystallizeSymbolizer.class;
    }

    @Override
    public Class<CrystallizeCachedSymbolizer> getCachedSymbolizerClass() {
        return CrystallizeCachedSymbolizer.class;
    }

    @Override
    public CrystallizeCachedSymbolizer createCachedSymbolizer(CrystallizeSymbolizer symbol) {
        return new CrystallizeCachedSymbolizer(symbol, this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CrystallizeCachedSymbolizer symbol, RenderingContext2D context) {
        return new CrystallizeSymbolizerRenderer(symbol, context);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CrystallizeCachedSymbolizer symbol, MapLayer layer) {
        //no glyph
    }

}
