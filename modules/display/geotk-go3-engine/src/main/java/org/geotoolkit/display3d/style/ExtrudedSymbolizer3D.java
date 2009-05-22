

package org.geotoolkit.display3d.style;

import java.util.Collections;
import java.util.Map;
import javax.measure.unit.Unit;

import org.geotoolkit.style.AbstractSymbolizer;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.StyleVisitor;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ExtrudedSymbolizer3D extends AbstractSymbolizer implements Symbolizer3D{

    private static final String NAME = "Extruded3D";

    private final Expression height;

    public ExtrudedSymbolizer3D(Unit uom, String geom, String name, Description desc, Expression height) {
        super(uom, geom, name, desc);
        this.height = height;
    }

    public Expression getHeight() {
        return height;
    }

    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public String getExtensionName() {
        return name;
    }

    @Override
    public Map<String, Expression> getParameters() {
        return Collections.emptyMap();
    }

}
