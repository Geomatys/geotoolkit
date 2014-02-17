
package org.geotoolkit.pending.demo.rendering.customsymbolizer;

import javax.measure.unit.NonSI;
import org.geotoolkit.style.AbstractExtensionSymbolizer;
import org.geotoolkit.style.StyleConstants;

public class CrystallizeSymbolizer extends AbstractExtensionSymbolizer{

    private final float edgeThickness;

    public CrystallizeSymbolizer(float edgeThickness){
        super(NonSI.PIXEL, null, "crystal", StyleConstants.DEFAULT_DESCRIPTION);
        this.edgeThickness = edgeThickness;
    }

    @Override
    public String getExtensionName() {
        throw new UnsupportedOperationException("crystallize");
    }

    public float getEdgeThickness() {
        return edgeThickness;
    }

}
