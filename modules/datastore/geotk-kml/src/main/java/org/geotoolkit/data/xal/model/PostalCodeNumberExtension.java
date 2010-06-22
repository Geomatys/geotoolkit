package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps PostalCodeNumberExtension type.</p>
 *
 * <p>Examples are: 1234 (USA), 1G (UK), etc.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Type">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="NumberExtensionSeparator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostalCodeNumberExtension extends GenericTypedGrPostal {

    /**
     * <p>The separator between postal code number and the extension. Eg. "-".</p>
     *
     * @return
     */
    public String getNumberExtensionSeparator();
}
