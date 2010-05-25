package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractColorStyleDefault extends AbstractSubStyleDefault implements AbstractColorStyle {

    protected Color color;
    protected ColorMode colorMode;
    protected List<SimpleType> colorStyleSimpleExtensions;
    protected List<AbstractObject> colorStyleObjectExtensions;

    protected AbstractColorStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes, subStyleSimpleExtensions, subStyleObjectExtensions);
        this.color = color;
        this.colorMode = colorMode;
        this.colorStyleSimpleExtensions = colorStyleSimpleExtensions;
        this.colorStyleObjectExtensions = colorStyleObjectExtensions;
    }

    @Override
    public Color getColor() {return this.color;}

    @Override
    public ColorMode getColorMode() {return this.colorMode;}

    @Override
    public List<SimpleType> getColorStyleSimpleExtensions() {return this.colorStyleSimpleExtensions;}

    @Override
    public List<AbstractObject> getColorStyleObjectExtensions() {return this.colorStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tAbstractColorStyleDefault : "+
                "\n\tcolor : "+this.color+
                "\n\tcolorMode : "+this.colorMode;
        return resultat;
    }

}
