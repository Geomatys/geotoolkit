package org.geotoolkit.data.model.xal;

/**
 * <p>This enumeration maps oddEven element.</p>
 *
 * <pre>
 * &lt;xs:simpleType>
 *  &lt;s:restriction base="xs:NMTOKEN">
 *      &lt;xs:enumeration value="Odd"/>
 *      &lt;xs:enumeration value="Even"/>
 *  &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
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
    public String getOddEven(){
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
            if(rt.getOddEven().equals(oddEven)){
                resultat = rt;
                break;
            }
        }
        return resultat;
    }
}
