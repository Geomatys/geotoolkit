package org.geotoolkit.data.kml.model;

/**
 *
 * <p>This interface maps Alias element.</p>
 *
 * <pre>
 * &lt;element name="Alias" type="kml:AliasType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AliasType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:targetHref" minOccurs="0"/>
 *              &lt;element ref="kml:sourceHref" minOccurs="0"/>
 *              &lt;element ref="kml:AliasSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AliasObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AliasSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AliasObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Alias extends AbstractObject {

    /**
     *
     * @return the target href
     */
    public String getTargetHref();

    /**
     *
     * @return the source href
     */
    public String getSourceHref();

    /**
     *
     * @param targetHref
     */
    public void setTargetHref(String targetHref);

    /**
     *
     * @param sourceHref
     */
    public void setSourceHref(String sourceHref);

}
