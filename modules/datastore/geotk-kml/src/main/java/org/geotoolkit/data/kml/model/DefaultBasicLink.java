package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultBasicLink implements BasicLink {

    private final Extensions extensions = new Extensions();
    private IdAttributes idAttributes;
    private String href;

    /**
     * 
     */
    public DefaultBasicLink() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     */
    public DefaultBasicLink(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, String href,
            List<SimpleType> basicLinkSimpleExtensions,
            List<AbstractObject> basicLinkObjectExtensions) {
        this.idAttributes = idAttributes;
        this.href = href;
        if (objectSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (basicLinkSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.BASIC_LINK).addAll(basicLinkSimpleExtensions);
        }
        if (basicLinkObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.BASIC_LINK).addAll(basicLinkObjectExtensions);
        }
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
    public String getHref() {
        return this.href;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIdAttributes(IdAttributes idAttributes) {
        this.idAttributes = idAttributes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        String resultat = "BasicLinkDefault : "
                + "\n\thref : " + this.href;
        return resultat;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Extensions extensions() {
        return this.extensions;
    }
}
