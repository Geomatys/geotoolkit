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

    /**
     * 
     */
    public DefaultItemIcon() {
        this.states = EMPTY_LIST;
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
    public DefaultItemIcon(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions,
            List<AbstractObject> itemIconObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.states = states;
        this.href = href;
        if (itemIconSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.ITEM_ICON).addAll(itemIconSimpleExtensions);
        }
        if (itemIconObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.ITEM_ICON).addAll(itemIconObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ItemIconState> getStates() {
        return this.states;
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
    public void setStates(List<ItemIconState> states) {
        this.states = states;
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
        String resultat = super.toString()
                + "\n\tBalloonStyleDefault : "
                + "\n\tstates : " + this.states
                + "\n\thref : " + this.href;
        return resultat;
    }
}
