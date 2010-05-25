package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public enum ItemIconState {

    OPEN("open"),
    CLOSED("closed"),
    ERROR("error"),
    FETCHING_0("fetching0"),
    FETCHING_1("fetching1"),
    TETCHING_2("fetching2");

    private String itemIconState;

    private ItemIconState(String itemIconState){
        this.itemIconState = itemIconState;
    }

    public String getItemIconState(){
        return this.itemIconState;
    }

    public static ItemIconState transform(String itemIconState){
        return transform(itemIconState, null);
    }

    public static ItemIconState transform(String itemIconState, ItemIconState defaultValue){
        ItemIconState resultat = defaultValue;
        for(ItemIconState cm : ItemIconState.values()){
            if(cm.getItemIconState().equals(itemIconState)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }
}
