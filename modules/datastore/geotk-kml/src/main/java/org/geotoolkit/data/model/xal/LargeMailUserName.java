package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps LargeMailUserName element.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Type" type="xs:string">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="Code" type="xs:string"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
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
