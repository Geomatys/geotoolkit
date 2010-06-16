package org.geotoolkit.data.model.kml;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPolyStyle extends DefaultAbstractColorStyle implements PolyStyle{

    private final boolean fill;
    private final boolean outline;
    private final List<SimpleType> polyStyleSimpleExtensions;
    private final List<AbstractObject> polyStyleObjectExtensions;

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
            List<SimpleType> polyStyleSimpleExtensions, List<AbstractObject> polyStyleObjectExtensions){
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
    public boolean getFill() {return this.fill;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getOutline() {return this.outline;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPolyStyleSimpleExtensions() {return this.polyStyleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPolyStyleObjectExtensions() {return this.polyStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tPolyStyleDefault : "+
                "\n\tfill : "+this.fill+
                "\n\toutline : "+this.outline;
        return resultat;
    }
}
