package org.geotoolkit.data.model.kml;

/**
 *
 * <p>This enumeration maps itemIconStateEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="itemIconStateEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="open"/>
 *      &lt;enumeration value="closed"/>
 *      &lt;enumeration value="error"/>
 *      &lt;enumeration value="fetching0"/>
 *      &lt;enumeration value="fetching1"/>
 *      &lt;enumeration value="fetching2"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
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

    private final String itemIconState;

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
        for(ItemIconState cm : ItemIconState.values()){
            if(cm.getItemIconState().equals(itemIconState)) return cm;
        }
        return defaultValue;
    }
}
