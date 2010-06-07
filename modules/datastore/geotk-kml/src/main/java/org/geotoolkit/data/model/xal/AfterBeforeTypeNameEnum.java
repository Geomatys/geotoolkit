package org.geotoolkit.data.model.xal;

/**
 *
 * <p>23-25 Archer St, where number appears before name.</p>
 *
 * <br />&lt;xs:simpleType>
 * <br />&lt;s:restriction base="xs:NMTOKEN">
 * <br />&lt;xs:enumeration value="BeforeName"/>
 * <br />&lt;xs:enumeration value="AfterName"/>
 * <br />&lt;xs:enumeration value="BeforeType"/>
 * <br />&lt;xs:enumeration value="AfterType"/>
 * <br />&lt;/xs:restriction>
 * <br />&lt;/xs:simpleType>
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
    public String getNumberOccurrence(){
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
            if(io.getNumberOccurrence().equals(afterBeforTypeName)) return io;
        }
        return defaultValue;
    }
}
