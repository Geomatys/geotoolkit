package org.geotoolkit.data.xal.model;

/**
 * <p>This enumeration maps dependentThoroughfares attribute.</p>
 *
 * <p>Does this thoroughfare have a a dependent thoroughfare? Corner of street X, etc.</p>
 *
 * <pre>
 * &lt;xs:simpleType>
 *  &lt;xs:restriction base="xs:NMTOKEN">
 *      &lt;xs:enumeration value="Yes"/>
 *      &lt;xs:enumeration value="No"/>
 *  &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum DependentThoroughfares {

    YES("Yes"),
    NO("No");

    private String dependentThoroughfares;

    /**
     * 
     * @param dependentThoroughfares
     */
    private DependentThoroughfares(String dependentThoroughfares){
        this.dependentThoroughfares = dependentThoroughfares;
    }

    /**
     *
     * @return
     */
    public String getDependentThoroughfares(){
        return this.dependentThoroughfares;
    }

    /**
     *
     * @param dependentThoroughfares
     * @return The DependentThoroughfares instance corresponding to the dependentThoroughfares parameter.
     */
    public static DependentThoroughfares transform(String dependentThoroughfares){
        return transform(dependentThoroughfares, null);
    }

    /**
     *
     * @param dependentThoroughfares
     * @param defaultValue The default value to return if dependentThoroughfares String parameter
     * do not correspond to one DependentThoroughfares instance.
     * @return The DependentThoroughfares instance corresponding to the dependentThoroughfares parameter.
     */
    public static DependentThoroughfares transform(String dependentThoroughfares, DependentThoroughfares defaultValue){
        for(DependentThoroughfares dtf : DependentThoroughfares.values()){
            if(dtf.getDependentThoroughfares().equals(dependentThoroughfares)){
                return dtf;
            }
        }
        return defaultValue;
    }
}
