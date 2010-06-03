package org.geotoolkit.data.model.xal;

import org.geotoolkit.data.model.kml.*;

/**
 * <p>This enumeration maps oddEven element.</p>
 *
 * <br />&lt;xs:simpleType>
 * <br />&lt;s:restriction base="xs:NMTOKEN">
 * <br />&lt;xs:enumeration value="Odd"/>
 * <br />&lt;xs:enumeration value="Even"/>
 * <br />&lt;/xs:restriction>
 * <br />&lt;/xs:simpleType>
 *
 * @author Samuel Andrés
 */
public enum OddEvenEnum {

    ODD("Odd"),
    EVEN("Even");

    private String oddEven;

    /**
     * 
     * @param oddEven
     */
    private OddEvenEnum(String oddEven){
        this.oddEven = oddEven;
    }

    /**
     *
     * @return
     */
    public String getRangeType(){
        return this.oddEven;
    }

    /**
     *
     * @param oddEven
     * @return The OddEvenEnum instance corresponding to the oddEven parameter.
     */
    public static OddEvenEnum transform(String oddEven){
        return transform(oddEven, null);
    }

    /**
     *
     * @param oddEven
     * @param defaultValue The default value to return if oddEven String parameter
     * do not correspond to one OddEvenEnum instance.
     * @return The OddEvenEnum instance corresponding to the oddEven parameter.
     */
    public static OddEvenEnum transform(String oddEven, OddEvenEnum defaultValue){
        OddEvenEnum resultat = defaultValue;
        for(OddEvenEnum rt : OddEvenEnum.values()){
            if(rt.getRangeType().equals(oddEven)){
                resultat = rt;
                break;
            }
        }
        return resultat;
    }
}
