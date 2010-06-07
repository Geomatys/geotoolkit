package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractObjectDefault implements AbstractObject {

    protected final List<SimpleType> objectSimpleExtensions;
    protected final IdAttributes idAttributes;

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     */
    protected AbstractObjectDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes){

        this.objectSimpleExtensions = (objectSimpleExtensions == null) ? EMPTY_LIST : objectSimpleExtensions;
        this.idAttributes = idAttributes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getObjectSimpleExtensions() {
        return this.objectSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
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
