package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractStyleSelectorDefault extends AbstractObjectDefault implements AbstractStyleSelector {

    protected List<SimpleType> styleSelectorSimpleExtensions;
    protected List<AbstractObject> styleSelectorObjectExtensions;

    protected AbstractStyleSelectorDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.styleSelectorSimpleExtensions = abstractStyleSelectorSimpleExtensions;
        this.styleSelectorObjectExtensions = abstractStyleSelectorObjectExtensions;
    }

    @Override
    public List<SimpleType> getAbstractStyleSelectorSimpleExtensions() {
        return this.styleSelectorSimpleExtensions;
    }

    @Override
    public List<AbstractObject> getAbstractStyleSelectorObjectExtensions() {
        return this.styleSelectorObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tAbstractStyleSelectorDefault : ";
        return resultat;
    }

}
