

package org.geotoolkit.pending.demo.rendering.customsymbolizer;

import com.jhlabs.image.CrystallizeFilter;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;


public class CrystallizeCachedSymbolizer extends CachedSymbolizer<CrystallizeSymbolizer> {

    private CrystallizeFilter operation;

    public CrystallizeCachedSymbolizer(CrystallizeSymbolizer symbol, SymbolizerRendererService service){
        super(symbol, service);
    }

    @Override
    protected void evaluate() {
        //load resources, make them ready to be used plenty of times
        operation = new CrystallizeFilter();
        operation.setEdgeThickness(styleElement.getEdgeThickness());
    }

    @Override
    public float getMargin(Object candidate, RenderingContext2D ctx) {
        return 0;
    }

    @Override
    public boolean isVisible(Object feature) {
        return true;
    }

}
