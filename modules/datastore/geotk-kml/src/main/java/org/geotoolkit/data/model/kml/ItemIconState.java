package org.geotoolkit.data.model.kml;

/**
 *
 * <p>Thi enumeration maps itemIconStateEnumType type.</p>
 *
 * <br />&lt;simpleType name="itemIconStateEnumType">
 * <br />&lt;restriction base="string">
 * <br />&lt;enumeration value="open"/>
 * <br />&lt;enumeration value="closed"/>
 * <br />&lt;enumeration value="error"/>
 * <br />&lt;enumeration value="fetching0"/>
 * <br />&lt;enumeration value="fetching1"/>
 * <br />&lt;enumeration value="fetching2"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
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

    /**
     *
     * @param itemIconState
     */
    private ItemIconState(String itemIconState){
        this.itemIconState = itemIconState;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getItemIconState(){
        return this.itemIconState;
    }

    /**
     *
     * @param itemIconState
     * @return The ItemIconState instance corresponding to the itemIconState parameter.
     */
    public static ItemIconState transform(String itemIconState){
        return transform(itemIconState, null);
    }

        /**
     *
     * @param altitudeMode
     * @param defaultValue The default value to return if itemIconState String parameter
     * do not correspond to one ItemIconState instance.
     * @return The ItemIconState instance corresponding to the itemIconState parameter.
     */
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
