package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class PolyStyleDefault extends AbstractColorStyleDefault implements PolyStyle{

    private boolean fill;
    private boolean outline;
    private List<SimpleType> polyStyleSimpleExtensions;
    private List<AbstractObject> polyStyleObjectExtensions;

    public PolyStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
        this.polyStyleSimpleExtensions = polyStyleSimpleExtensions;
        this.polyStyleObjectExtensions = polyStyleObjectExtensions;
    }

    @Override
    public boolean getFill() {return this.fill;}

    @Override
    public boolean getOutline() {return this.outline;}

    @Override
    public List<SimpleType> getPolyStyleSimpleExtensions() {return this.polyStyleSimpleExtensions;}

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
