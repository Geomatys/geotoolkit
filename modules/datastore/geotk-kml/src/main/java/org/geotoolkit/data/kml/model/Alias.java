package org.geotoolkit.data.kml.model;

import java.net.URI;

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
    public URI getTargetHref();

    /**
     *
     * @return the source href
     */
    public URI getSourceHref();

    /**
     *
     * @param targetHref
     */
    public void setTargetHref(URI targetHref);

    /**
     *
     * @param sourceHref
     */
    public void setSourceHref(URI sourceHref);

}
