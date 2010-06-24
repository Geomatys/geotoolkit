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
public class DefaultPolyStyle extends DefaultAbstractColorStyle implements PolyStyle {

    private boolean fill;
    private boolean outline;
    private List<SimpleType> polyStyleSimpleExtensions;
    private List<AbstractObject> polyStyleObjectExtensions;

    /**
     * 
     */
    public DefaultPolyStyle() {
        this.fill = DEF_FILL;
        this.outline = DEF_OUTLINE;
        this.polyStyleSimpleExtensions = EMPTY_LIST;
        this.polyStyleObjectExtensions = EMPTY_LIST;
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
     * @param fill
     * @param outline
     * @param polyStyleSimpleExtensions
     * @param polyStyleObjectExtensions
     */
    public DefaultPolyStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleType> polyStyleSimpleExtensions, List<AbstractObject> polyStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions);
        this.fill = fill;
        this.outline = outline;
        this.polyStyleSimpleExtensions = (polyStyleSimpleExtensions == null) ? EMPTY_LIST : polyStyleSimpleExtensions;
        this.polyStyleObjectExtensions = (polyStyleObjectExtensions == null) ? EMPTY_LIST : polyStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getFill() {
        return this.fill;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getOutline() {
        return this.outline;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPolyStyleSimpleExtensions() {
        return this.polyStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPolyStyleObjectExtensions() {
        return this.polyStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFill(boolean fill) {
        this.fill = fill;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPolyStyleSimpleExtensions(List<SimpleType> polyStyleSimpleExtensions) {
        this.polyStyleSimpleExtensions = polyStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPolyStyleObjectExtensions(List<AbstractObject> polyStyleObjectExtensions) {
        this.polyStyleObjectExtensions = polyStyleObjectExtensions;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tPolyStyleDefault : "
                + "\n\tfill : " + this.fill
                + "\n\toutline : " + this.outline;
        return resultat;
    }
}
