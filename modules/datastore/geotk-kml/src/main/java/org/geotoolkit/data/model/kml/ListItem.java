package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public enum ListItem {

    RADIO_FOLDER("radioFolder"),
    CHECK("check"),
    CHECK_HIDE_CHILDREN("checkHideChildren"),
    CHECK_OFF_ONLY("checkOffOnly");

    private String item;

    private ListItem(String item){
        this.item = item;
    }

    public String getItem(){
        return this.item;
    }

    public static ListItem transform(String item){
        return transform(item, null);
    }

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
