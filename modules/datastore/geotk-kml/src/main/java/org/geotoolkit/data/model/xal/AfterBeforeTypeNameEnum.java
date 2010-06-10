package org.geotoolkit.data.model.xal;

/**
 *
 * <p>23-25 Archer St, where number appears before name.</p>
 *
 * <pre>
 * &lt;xs:simpleType>
 *  &lt;s:restriction base="xs:NMTOKEN">
 *      &lt;xs:enumeration value="BeforeName"/>
 *      &lt;xs:enumeration value="AfterName"/>
 *      &lt;xs:enumeration value="BeforeType"/>
 *      &lt;xs:enumeration value="AfterType"/>
 *  &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum AfterBeforeTypeNameEnum {

    BEFORE_NAME("BeforeName"),
    AFTER_NAME("AfterName"),
    BEFORE_TYPE("BeforeType"),
    AFTER_TYPE("AfterType");

    private final String afterBeforTypeName;

    /**
     * 
     * @param afterBeforTypeName
     */
    private AfterBeforeTypeNameEnum(String afterBeforTypeName){
        this.afterBeforTypeName = afterBeforTypeName;
    }

    /**
     *
     * @return
     */
    public String getAfterBeforeTypeEnum(){
        return this.afterBeforTypeName;
    }

    /**
     *
     * @param afterBeforTypeName
     * @return The AfterBeforeTypeNameEnum instance corresponding to the afterBeforTypeName parameter.
     */
    public static AfterBeforeTypeNameEnum transform(String afterBeforTypeName){
        return transform(afterBeforTypeName, null);
    }

    /**
     *
     * @param afterBeforTypeName
     * @param defaultValue The default value to return if afterBeforTypeName String parameter
     * do not correspond to one AfterBeforeTypeNameEnum instance.
     * @return The AfterBeforeTypeNameEnum instance corresponding to the afterBeforTypeName parameter.
     */
    public static AfterBeforeTypeNameEnum transform(String afterBeforTypeName, AfterBeforeTypeNameEnum defaultValue){
        for(AfterBeforeTypeNameEnum io : AfterBeforeTypeNameEnum.values()){
            if(io.getAfterBeforeTypeEnum().equals(afterBeforTypeName)) return io;
        }
        return defaultValue;
    }
}
