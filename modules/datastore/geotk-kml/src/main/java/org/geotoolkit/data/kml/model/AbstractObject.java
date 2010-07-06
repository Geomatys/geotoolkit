package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps an AbstractObjectGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractObjectGroup" type="kml:AbstractObjectType" abstract="true"/>
 *
 * &lt;complexType name="AbstractObjectType" abstract="true">
 *  &lt;sequence>
 *      &lt;element ref="kml:ObjectSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attributeGroup ref="kml:idAttributes"/>
 * &lt;/complexType>
 *
 * &lt;element name="ObjectSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractObject {

    /**
     *
     * @return The list of simple extensions.
     */
    public Extensions extensions();

    /**
     *
     * @return The identification attributes.
     */
    public IdAttributes getIdAttributes();

    /**
     *
     * @param idAttributes
     */
    public void setIdAttributes(IdAttributes idAttributes);
}
