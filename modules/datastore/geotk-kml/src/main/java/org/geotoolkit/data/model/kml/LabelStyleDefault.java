package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LabelStyleDefault extends AbstractColorStyleDefault implements LabelStyle {

    private final double scale;
    private final List<SimpleType> labelStyleSimpleExtensions;
    private final List<AbstractObject> labelStyleObjectExtensions;

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
    public LabelStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions){
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
    public double getScale() {return this.scale;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLabelStyleSimpleExtensions() {return this.labelStyleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLabelStyleObjectExtensions() {return this.labelStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tLabelStyleDefault : "+
                "\n\tscale : "+this.scale;
        return resultat;
    }

}
