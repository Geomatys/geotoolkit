package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractObjectDefault implements AbstractObject {

    protected List<SimpleType> objectSimpleExtensions;
    protected IdAttributes idAttributes;

    protected AbstractObjectDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes){

        this.objectSimpleExtensions = objectSimpleExtensions;
        this.idAttributes = idAttributes;
    }

    @Override
    public List<SimpleType> getObjectSimpleExtensions() {
        return this.objectSimpleExtensions;
    }

    @Override
    public IdAttributes getIdAttributes() {
        return this.idAttributes;
    }

    @Override
    public String toString(){
        String resultat = "Abstract Object : ";
        return resultat;
    }

}
