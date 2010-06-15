package org.geotoolkit.data.model.xal;

/**
 * <p>This enumeration maps AfterBeforeEnum element.</p>
 *
 * <p>No.12-14 where "No." is before actual street number.</p>
 *
 * <pre>
 *  &lt;xs:simpleType>
 *  &lt;s:restriction base="xs:NMTOKEN">
 *  &lt;xs:enumeration value="Before"/>
 *  &lt;xs:enumeration value="After"/>
 *  &lt;/xs:restriction>
 *  &lt;/xs:simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum AfterBeforeEnum {

    BEFORE("Before"),
    AFTER("After");

    private final  String beforeAfter;

    /**
     * 
     * @param beforeAfter
     */
    private AfterBeforeEnum(String beforeAfter){
        this.beforeAfter = beforeAfter;
    }

    /**
     *
     * @return
     */
    public String getAfterBeforeEnum(){
        return this.beforeAfter;
    }

    /**
     *
     * @param beforeAfter
     * @return The AfterBeforeEnum instance corresponding to the beforeAfter parameter.
     */
    public static AfterBeforeEnum transform(String beforeAfter){
        return transform(beforeAfter, null);
    }

    /**
     *
     * @param beforeAfter
     * @param defaultValue The default value to return if beforeAfter String parameter
     * do not correspond to one AfterBeforeEnum instance.
     * @return The AfterBeforeEnum instance corresponding to the beforeAfter parameter.
     */
    public static AfterBeforeEnum transform(String beforeAfter, AfterBeforeEnum defaultValue){
        for(AfterBeforeEnum ba : AfterBeforeEnum.values()){
            if(ba.getAfterBeforeEnum().equals(beforeAfter)) return ba;
        }
        return defaultValue;
    }
}
