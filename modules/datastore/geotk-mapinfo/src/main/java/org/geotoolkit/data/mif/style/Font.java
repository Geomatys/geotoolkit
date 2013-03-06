package org.geotoolkit.data.mif.style;

import org.geotoolkit.feature.DefaultName;
import org.opengis.feature.type.Name;
import org.opengis.filter.expression.Expression;
import org.opengis.style.*;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class Font implements MIFSymbolizer {

    public static final Name NAME = new DefaultName("BRUSH");

    private String fontName = null;
    private int fontStyle = 0;
    private int fontColorCode = 0;
    private int backColorCode = -1;

    public Font(String ftName, int ftStyle, int ftColor) {
        fontName = ftName;
        fontStyle = ftStyle;
        fontColorCode = ftColor;
    }

    public Font(String ftName, int ftStyle, int ftColor, int backColor) {
        fontName = ftName;
        fontStyle = ftStyle;
        fontColorCode = ftColor;
        backColorCode = backColor;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public int getFontColorCode() {
        return fontColorCode;
    }

    public int getBackColorCode() {
        return backColorCode;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setFontColorCode(int fontColorCode) {
        this.fontColorCode = fontColorCode;
    }

    public void setBackColorCode(int backColorCode) {
        this.backColorCode = backColorCode;
    }

    @Override
    public String toMIFText() {
        return NAME.getLocalPart()+"("+fontName+","+fontStyle+","+fontColorCode+((backColorCode>=0)? ","+backColorCode : "")+")";
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return null;
    }

    @Override
    public String getGeometryPropertyName() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public Description getDescription() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public Object accept(StyleVisitor styleVisitor, Object o) {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }
}
