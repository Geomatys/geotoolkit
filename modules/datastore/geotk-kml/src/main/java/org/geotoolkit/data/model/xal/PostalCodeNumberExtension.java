package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostalCodeNumberExtension type.</p>
 *
 * <p>Examples are: 1234 (USA), 1G (UK), etc.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Type">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="NumberExtensionSeparator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
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
