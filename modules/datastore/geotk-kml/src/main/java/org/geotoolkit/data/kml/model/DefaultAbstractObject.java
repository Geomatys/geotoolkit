package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractObject implements AbstractObject {

    protected List<SimpleType> objectSimpleExtensions;
    protected IdAttributes idAttributes;

    protected DefaultAbstractObject(){
        this.objectSimpleExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     */
    protected DefaultAbstractObject(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes){

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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setObjectSimpleExtensions(List<SimpleType> objectSimpleExtensions){
        this.objectSimpleExtensions = objectSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */ 
    @Override
    public void setIdAttributes(IdAttributes idAttributes){
        this.idAttributes = idAttributes;
    }

    @Override
    public String toString(){
        String resultat = "Abstract Object : ";
        return resultat;
    }

}
