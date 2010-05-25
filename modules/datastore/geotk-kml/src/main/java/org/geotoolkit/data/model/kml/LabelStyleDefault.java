package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LabelStyleDefault extends AbstractColorStyleDefault implements LabelStyle {

    private double scale;
    private List<SimpleType> labelStyleSimpleExtensions;
    private List<AbstractObject> labelStyleObjectExtensions;

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
        this.labelStyleSimpleExtensions = iconStyleSimpleExtensions;
        this.labelStyleObjectExtensions = iconStyleObjectExtensions;
    }

    @Override
    public double getScale() {return this.scale;}

    @Override
    public List<SimpleType> getLabelStyleSimpleExtensions() {return this.labelStyleSimpleExtensions;}

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
