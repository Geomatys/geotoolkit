package org.geotoolkit.data.mapinfo.mif.style;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotoolkit.feature.DefaultName;
import org.opengis.feature.type.Name;
import org.opengis.style.Description;
import org.opengis.style.StyleVisitor;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import java.util.regex.Pattern;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class LabelLine implements MIFSymbolizer {

    public static final Name NAME = new DefaultName("LABEL");
    public static final Pattern PATTERN = Pattern.compile(NAME.getLocalPart(), Pattern.CASE_INSENSITIVE);

    private String type = "simple";
    private Coordinate point;

    public LabelLine(String lineType, Coordinate pt) {
        type = lineType;
        point = pt;
    }

    @Override
    public String toMIFText() {
        return NAME.getLocalPart()+' '+type+' '+point.x+' '+point.y;
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public String getGeometryPropertyName() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public String getName() {
        return NAME.getLocalPart();
    }

    @Override
    public Description getDescription() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public Object accept(StyleVisitor styleVisitor, Object o) {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public String toString() {
        return toMIFText();
    }
}
