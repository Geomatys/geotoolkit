package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLineStyle extends DefaultAbstractColorStyle implements LineStyle {

    private double width;
    private List<SimpleType> lineStyleSimpleExtensions;
    private List<AbstractObject> lineStyleObjectExtentions;

    /**
     * 
     */
    public DefaultLineStyle() {
        this.width = DEF_WIDTH;
        this.lineStyleSimpleExtensions = EMPTY_LIST;
        this.lineStyleSimpleExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param width
     * @param lineStyleSimpleExtensions
     * @param lineStyleObjectExtensions
     */
    public DefaultLineStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions, List<AbstractObject> lineStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions);
        this.width = width;
        this.lineStyleSimpleExtensions = (lineStyleSimpleExtensions == null) ? EMPTY_LIST : lineStyleSimpleExtensions;
        this.lineStyleObjectExtentions = (lineStyleObjectExtensions == null) ? EMPTY_LIST : lineStyleObjectExtensions;
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
    public List<SimpleType> getLineStyleSimpleExtensions() {
        return this.lineStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLineStyleObjectExtensions() {
        return this.lineStyleObjectExtentions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLineStyleSimpleExtensions(List<SimpleType> lineStyleSimpleExtensions) {
        this.lineStyleSimpleExtensions = lineStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLineStyleObjectExtensions(List<AbstractObject> lineStyleObjectExtensions) {
        this.lineStyleObjectExtentions = lineStyleObjectExtensions;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tLineStyleDefault : "
                + "\n\twidth : " + this.width;
        return resultat;
    }
}
