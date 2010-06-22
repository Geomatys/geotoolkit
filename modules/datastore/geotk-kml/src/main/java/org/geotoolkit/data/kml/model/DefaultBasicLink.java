package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultBasicLink implements BasicLink {

    private List<SimpleType> objectSimpleExtensions;
    private IdAttributes idAttributes;
    
    private String href;
    private List<SimpleType> basicLinkSimpleExtensions;
    private List<AbstractObject> basicLinkObjectExtensions;

    /**
     * 
     */
    public DefaultBasicLink(){
         this.basicLinkSimpleExtensions = EMPTY_LIST;
         this.basicLinkObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     */
    public DefaultBasicLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions){
            this.objectSimpleExtensions = objectSimpleExtensions;
            this.idAttributes = idAttributes;
            this.href = href;
            this.basicLinkSimpleExtensions = (basicLinkSimpleExtensions == null) ? EMPTY_LIST : basicLinkSimpleExtensions;
            this.basicLinkObjectExtensions = (basicLinkObjectExtensions == null) ? EMPTY_LIST : basicLinkObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getObjectSimpleExtensions() {return this.objectSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes getIdAttributes() {return this.idAttributes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHref() {return this.href;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getBasicLinkSimpleExtensions() {return this.basicLinkSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getBasicLinkObjectExtensions() {return this.basicLinkObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setObjectSimpleExtensions(List<SimpleType> objectSimpleExtensions) {
        this.objectSimpleExtensions = objectSimpleExtensions;
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
    public void setHref(String href) {this.href = href;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBasicLinkSimpleExtensions(List<SimpleType> basicLinkSimpleExtensions) {
        this.basicLinkSimpleExtensions = basicLinkSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBasicLinkObjectExtensions(List<AbstractObject> basicLinkObjectExtensions) {
        this.basicLinkObjectExtensions = basicLinkObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = "BasicLinkDefault : "+
                "\n\thref : "+this.href;
        return resultat;
    }

}
