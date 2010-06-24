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
public class DefaultLabelStyle extends DefaultAbstractColorStyle implements LabelStyle {

    private double scale;
    private List<SimpleType> labelStyleSimpleExtensions;
    private List<AbstractObject> labelStyleObjectExtensions;

    public DefaultLabelStyle() {
        this.scale = DEF_SCALE;
        this.labelStyleSimpleExtensions = EMPTY_LIST;
        this.labelStyleObjectExtensions = EMPTY_LIST;
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
     * @param scale
     * @param iconStyleSimpleExtensions
     * @param iconStyleObjectExtensions
     */
    public DefaultLabelStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions);
        this.scale = scale;
        this.labelStyleSimpleExtensions = (iconStyleSimpleExtensions == null) ? EMPTY_LIST : iconStyleSimpleExtensions;
        this.labelStyleObjectExtensions = (iconStyleObjectExtensions == null) ? EMPTY_LIST : iconStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getScale() {
        return this.scale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLabelStyleSimpleExtensions() {
        return this.labelStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLabelStyleObjectExtensions() {
        return this.labelStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLabelStyleSimpleExtensions(List<SimpleType> labelStyleSimpleExtensions) {
        this.labelStyleSimpleExtensions = labelStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLabelStyleObjectExtensions(List<AbstractObject> labelStyleObjectExtensions) {
        this.labelStyleObjectExtensions = labelStyleObjectExtensions;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tLabelStyleDefault : "
                + "\n\tscale : " + this.scale;
        return resultat;
    }
}
