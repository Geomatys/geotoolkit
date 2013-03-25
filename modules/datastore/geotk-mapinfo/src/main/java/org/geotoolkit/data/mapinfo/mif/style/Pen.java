package org.geotoolkit.data.mapinfo.mif.style;

import org.geotoolkit.data.mapinfo.mif.geometry.MIFLineBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.feature.type.Name;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public class Pen implements MIFSymbolizer, LineSymbolizer {

    public static final Name NAME = new DefaultName("PEN");

    public static final Pattern PEN_PATTERN = Pattern.compile(NAME.getLocalPart()+"\\s*\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE);

    private int widthCode;
    private int pattern;
    private int colorCode;

    private Stroke stroke = null;
    private Expression perpendicularOffset = null;
    private Graphics2D graphic =null;
    private String geometryName= MIFLineBuilder.NAME.getLocalPart();

    public Pen(int widthCode, int pattern, int colorCode) {
        this.widthCode = widthCode;
        this.pattern = pattern;
        this.colorCode = colorCode;
    }

    public int getWidthCode() {
        return widthCode;
    }

    public int getPattern() {
        return pattern;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setWidthCode(int widthCode) {
        this.widthCode = widthCode;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setPerpendicularOffset(Expression perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    public void setGraphic(Graphics2D graphic) {
        this.graphic = graphic;
    }

    public void setGeometryName(String geometryName) {
        this.geometryName = geometryName;
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public Expression getPerpendicularOffset() {
        return perpendicularOffset;
    }

    @Override
    public String toMIFText() {
        return NAME.getLocalPart()+"("+widthCode+","+pattern+","+colorCode+")";
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
    public Object accept(StyleVisitor styleVisitor, Object o) {
        return styleVisitor.visit(this, o);
    }

    @Override
    public String toString() {
        return toMIFText();
    }
}
