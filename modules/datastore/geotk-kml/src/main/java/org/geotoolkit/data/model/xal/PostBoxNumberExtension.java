package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostBoxNumberExtension element.</p>
 *
 * <p>Some countries like USA have POBox as 12345-123.</p>
 *
 * <pre>
 * &lt;xs:element name="PostBoxNumberExtension" minOccurs="0">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NumberExtensionSeparator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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