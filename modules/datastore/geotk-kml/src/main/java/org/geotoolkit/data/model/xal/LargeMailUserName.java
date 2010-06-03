package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps LargeMailUserName element.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Type" type="xs:string">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Code" type="xs:string"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface LargeMailUserName {

    /**
     *
     * @return
     */
    public String getContent();

    /**
     * <p>Airport, Hospital, etc.</p>
     *
     * @return
     */
    public String getType();

    /**
     * 
     * @return
     */
    public String getCode();

}
