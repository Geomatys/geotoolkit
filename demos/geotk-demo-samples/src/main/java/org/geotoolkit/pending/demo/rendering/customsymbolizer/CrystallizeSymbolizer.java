
package org.geotoolkit.pending.demo.rendering.customsymbolizer;

import org.apache.sis.measure.Units;
import org.geotoolkit.style.AbstractExtensionSymbolizer;
import org.geotoolkit.style.StyleConstants;

public class CrystallizeSymbolizer extends AbstractExtensionSymbolizer{

    private final float edgeThickness;

    public CrystallizeSymbolizer(float edgeThickness){
        super(Units.POINT, null, "crystal", StyleConstants.DEFAULT_DESCRIPTION);
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
