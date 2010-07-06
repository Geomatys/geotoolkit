package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLineStyle extends DefaultAbstractColorStyle implements LineStyle {

    private double width;

    /**
     * 
     */
    public DefaultLineStyle() {
        this.width = DEF_WIDTH;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractSubStyleSimpleExtensions
     * @param abstractSubStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param width
     * @param lineStyleSimpleExtensions
     * @param lineStyleObjectExtensions
     */
    public DefaultLineStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions,
            List<AbstractObject> lineStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions,
                colorStyleObjectExtensions);
        this.width = width;
        if (lineStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LINE_STYLE).addAll(lineStyleSimpleExtensions);
        }
        if (lineStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LINE_STYLE).addAll(lineStyleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getWidth() {
        return this.width;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tLineStyleDefault : "
                + "\n\twidth : " + this.width;
        return resultat;
    }
}
