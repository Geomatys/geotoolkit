package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractObject implements AbstractObject {

    private final Extensions extensions = new Extensions();
    protected IdAttributes idAttributes;

    protected DefaultAbstractObject() {
    }

    /**
     * 
     * @param idAttributes
     */
    protected DefaultAbstractObject(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes) {
        if (objectSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        this.idAttributes = idAttributes;
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
    public void setIdAttributes(IdAttributes idAttributes) {
        this.idAttributes = idAttributes;
    }

    @Override
    public String toString() {
        String resultat = "Abstract Object : ";
        return resultat;
    }

    @Override
    public Extensions extensions() {
        return extensions;
    }
}
