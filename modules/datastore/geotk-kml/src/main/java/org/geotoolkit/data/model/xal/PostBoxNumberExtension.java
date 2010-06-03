package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostBoxNumberExtension element.</p>
 *
 * <p>Some countries like USA have POBox as 12345-123.</p>
 *
 * <br />&lt;xs:element name="PostBoxNumberExtension" minOccurs="0">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NumberExtensionSeparator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PostBoxNumberExtension {

    /**
     *
     * @return
     */
    public String getContent();

    /**
     * <p>"-" is the NumberExtensionSeparator in POBOX:12345-123.</p>
     * @return
     */
    public String getNumberExtensionSeparator();
}