package org.geotoolkit.data.mapinfo.mif.style;

import org.geotoolkit.data.mapinfo.mif.geometry.MIFLineBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.feature.type.Name;
import org.opengis.filter.expression.Expression;
import org.opengis.style.*;
import org.opengis.style.Stroke;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import java.util.regex.Pattern;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public class Brush implements MIFSymbolizer, PolygonSymbolizer {

    public static final Name NAME = new DefaultName("BRUSH");

    public static final Pattern BRUSH_PATTERN = Pattern.compile(NAME.getLocalPart()+"(\\s*\\([^\\)]+\\))?", Pattern.CASE_INSENSITIVE);

    public int pattern = 0;
    public int foregroundCC = 0;
    public int backgroundCC = -1;

    private transient Fill fill = null;
    private transient Stroke stroke = null;
    private transient Expression perpendicularOffset = null;
    private transient Displacement displacement = null;
    private String geometryName= MIFLineBuilder.NAME.getLocalPart();

    public Brush(int pattern, int foregroundCC) {
        this.pattern = pattern;
        this.foregroundCC = foregroundCC;
    }

    public Brush(int pattern, int foregroundCC, int backgroundCC) {
        this.pattern = pattern;
        this.foregroundCC = foregroundCC;
        this.backgroundCC = backgroundCC;
    }

    @Override
    public String toMIFText() {
        return NAME.getLocalPart()+"("+pattern+","+foregroundCC+ ((backgroundCC>-1)? ","+backgroundCC : "")+")";
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return null;
    }

    @Override
    public String getGeometryPropertyName() {
        return geometryName;
    }

    @Override
    public String getName() {
        return NAME.getLocalPart();
    }

    @Override
    public Description getDescription() {
        return new DefaultDescription(new SimpleInternationalString(NAME.getLocalPart()),new SimpleInternationalString(toMIFText()));
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public void setForegroundCC(int foregroundCC) {
        this.foregroundCC = foregroundCC;
    }

    public void setBackgroundCC(int backgroundCC) {
        this.backgroundCC = backgroundCC;
    }

    public void setFill(Fill fill) {
        this.fill = fill;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setPerpendicularOffset(Expression perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    public void setDisplacement(Displacement displacement) {
        this.displacement = displacement;
    }

    public void setGeometryName(String geometryName) {
        this.geometryName = geometryName;
    }

    @Override
    public Displacement getDisplacement() {
        return displacement;
    }

    @Override
    public Expression getPerpendicularOffset() {
        return perpendicularOffset;
    }

    @Override
    public Object accept(StyleVisitor styleVisitor, Object o) {
        return styleVisitor.visit(this, o);
    }

    @Override
    public String toString() {
        return toMIFText();
    }
}
