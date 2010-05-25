package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LineStyleDefault extends AbstractColorStyleDefault implements LineStyle {

    private double width;
    private List<SimpleType> lineStyleSimpleExtensions;
    private List<AbstractObject> lineStyleObjectExtentions;

    public LineStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions, List<AbstractObject> lineStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions);
        this.width = width;
        this.lineStyleSimpleExtensions = lineStyleSimpleExtensions;
        this.lineStyleObjectExtentions = lineStyleObjectExtensions;
    }

    @Override
    public double getWidth() {return this.width;}

    @Override
    public List<SimpleType> getLineStyleSimpleExtensions() {return this.lineStyleSimpleExtensions;}

    @Override
    public List<AbstractObject> getLineStyleObjectExtensions() {return this.lineStyleObjectExtentions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tLineStyleDefault : "+
                "\n\twidth : "+this.width;
        return resultat;
    }

}
