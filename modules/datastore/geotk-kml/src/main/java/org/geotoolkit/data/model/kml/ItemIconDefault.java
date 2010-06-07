package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class ItemIconDefault extends AbstractObjectDefault implements ItemIcon {

    private final List<ItemIconState> states;
    private final String href;
    private final List<SimpleType> itemIconSimpleExtensions;
    private final List<AbstractObject> itemIconObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param states
     * @param href
     * @param itemIconSimpleExtensions
     * @param itemIconObjectExtensions
     */
    public ItemIconDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tBalloonStyleDefault : "+
                "\n\tstates : "+this.states+
                "\n\thref : "+this.href;
        return resultat;
    }
}
