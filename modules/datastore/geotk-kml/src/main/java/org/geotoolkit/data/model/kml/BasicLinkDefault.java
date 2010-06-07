package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class BasicLinkDefault implements BasicLink {

    private final List<SimpleType> objectSimpleExtensions;
    private final IdAttributes idAttributes;
    
    private final String href;
    private final List<SimpleType> basicLinkSimpleExtensions;
    private final List<AbstractObject> basicLinkObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     */
    public BasicLinkDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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

    @Override
    public String toString(){
        String resultat = "BasicLinkDefault : "+
                "\n\thref : "+this.href;
        return resultat;
    }

}
