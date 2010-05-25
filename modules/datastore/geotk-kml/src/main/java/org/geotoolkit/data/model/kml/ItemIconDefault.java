package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class ItemIconDefault extends AbstractObjectDefault implements ItemIcon {

    private List<ItemIconState> states;
    private String href;
    private List<SimpleType> itemIconSimpleExtensions;
    private List<AbstractObject> itemIconObjectExtensions;

    public ItemIconDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions, List<AbstractObject> itemIconObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.states = states;
        this.href = href;
        this.itemIconSimpleExtensions = itemIconSimpleExtensions;
        this.itemIconObjectExtensions = itemIconObjectExtensions;
    }

    @Override
    public List<ItemIconState> getStates() {return this.states;}

    @Override
    public String getHref() {return this.href;}

    @Override
    public List<SimpleType> getItemIconSimpleExtensions() {return this.itemIconSimpleExtensions;}

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
