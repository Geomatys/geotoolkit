package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractSubStyleDefault extends AbstractObjectDefault implements AbstractSubStyle {

    protected List<SimpleType> subStyleSimpleExtensions;
    protected List<AbstractObject> subStyleObjectExtensions;

    protected AbstractSubStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.subStyleSimpleExtensions = subStyleSimpleExtensions;
        this.subStyleObjectExtensions = subStyleObjectExtensions;
    }

    @Override
    public List<SimpleType> getSubStyleSimpleExtensions() {return this.subStyleSimpleExtensions;}

    @Override
    public List<AbstractObject> getSubStyleObjectExtensions() {return this.subStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tAbstractSubStyleDefault : ";
        return resultat;
    }
}
