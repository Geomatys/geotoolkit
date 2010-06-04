package org.geotoolkit.data.model.kml;

/**
 * <p>This enumeration maps listItemTypeEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="listItemTypeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="radioFolder"/>
 *      &lt;enumeration value="check"/>
 *      &lt;enumeration value="checkHideChildren"/>
 *      &lt;enumeration value="checkOffOnly"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum ListItem {

    RADIO_FOLDER("radioFolder"),
    CHECK("check"),
    CHECK_HIDE_CHILDREN("checkHideChildren"),
    CHECK_OFF_ONLY("checkOffOnly");

    private String item;

    /**
     * 
     * @param item
     */
    private ListItem(String item){
        this.item = item;
    }

    /**
     *
     * @return
     */
    public String getItem(){
        return this.item;
    }

    /**
     *
     * @param item
     * @return The ListItem instance corresponding to the item parameter.
     */
    public static ListItem transform(String item){
        return transform(item, null);
    }

    /**
     *
     * @param item
     * @param defaultValue The default value to return if item String parameter
     * do not correspond to one ListItem instance.
     * @return The ListItem instance corresponding to the item parameter.
     */
    public static ListItem transform(String item, ListItem defaultValue){
        ListItem resultat = defaultValue;
        for(ListItem cm : ListItem.values()){
            if(cm.getItem().equals(item)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }
}
