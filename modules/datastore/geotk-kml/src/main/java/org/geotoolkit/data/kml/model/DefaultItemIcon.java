package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultItemIcon extends DefaultAbstractObject implements ItemIcon {

    private List<ItemIconState> states;
    private String href;
    private List<SimpleType> itemIconSimpleExtensions;
    private List<AbstractObject> itemIconObjectExtensions;

    /**
     * 
     */
    public DefaultItemIcon(){
        this.states = EMPTY_LIST;
        this.itemIconSimpleExtensions = EMPTY_LIST;
        this.itemIconObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param states
     * @param href
     * @param itemIconSimpleExtensions
     * @param itemIconObjectExtensions
     */
    public DefaultItemIcon(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions, List<AbstractObject> itemIconObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.states = states;
        this.href = href;
        this.itemIconSimpleExtensions = (itemIconSimpleExtensions == null) ? EMPTY_LIST : itemIconSimpleExtensions;
        this.itemIconObjectExtensions = (itemIconObjectExtensions == null) ? EMPTY_LIST : itemIconObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ItemIconState> getStates() {return this.states;}

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
    public List<SimpleType> getItemIconSimpleExtensions() {return this.itemIconSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getItemIconObjectExtensions() {return this.itemIconObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStates(List<ItemIconState> states) {this.states = states;}

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
    public void setItemIconSimpleExtensions(List<SimpleType> itemIconSimpleExtensions) {
        this.itemIconSimpleExtensions = itemIconSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setItemIconObjectExtensions(List<AbstractObject> itemIconObjectExtensions) {
        this.itemIconObjectExtensions = itemIconObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tBalloonStyleDefault : "+
                "\n\tstates : "+this.states+
                "\n\thref : "+this.href;
        return resultat;
    }
}
