package org.geotoolkit.data.model.xal;

/**
 *
 * <p>Building 12-14 is "Range" and Building 12 is "Single"</p>
 *
 * <br />&lt;xs:simpleType>
 * <br />&lt;s:restriction base="xs:NMTOKEN">
 * <br />&lt;xs:enumeration value="Single"/>
 * <br />&lt;xs:enumeration value="Range"/>
 * <br />&lt;/xs:restriction>
 * <br />&lt;/xs:simpleType>
 *
 * @author Samuel Andrés
 */
public enum SingleRangeEnum {

    SINGLE("Single"),
    RANGE("Range");

    private String singleRange;

    /**
     * 
     * @param singleRange
     */
    private SingleRangeEnum(String singleRange){
        this.singleRange = singleRange;
    }

    /**
     *
     * @return
     */
    public String getSingleRange(){
        return this.singleRange;
    }

    /**
     *
     * @param singleRange
     * @return The SingleRangeEnum instance corresponding to the singleRange parameter.
     */
    public static SingleRangeEnum transform(String singleRange){
        return transform(singleRange, null);
    }

    /**
     *
     * @param singleRange
     * @param defaultValue The default value to return if singleRange String parameter
     * do not correspond to one SingleRangeEnum instance.
     * @return The SingleRangeEnum instance corresponding to the singleRange parameter.
     */
    public static SingleRangeEnum transform(String singleRange, SingleRangeEnum defaultValue){
        SingleRangeEnum resultat = defaultValue;
        for(SingleRangeEnum nt : SingleRangeEnum.values()){
            if(nt.getSingleRange().equals(singleRange)){
                resultat = nt;
                break;
            }
        }
        return resultat;
    }
}
