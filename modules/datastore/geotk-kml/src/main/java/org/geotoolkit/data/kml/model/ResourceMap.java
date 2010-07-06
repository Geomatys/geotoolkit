package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 * <p>This interface maps ResourceMap element.</p>
 *
 * <pre>
 * &lt;element name="ResourceMap" type="kml:ResourceMapType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ResourceMapType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:Alias" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ResourceMapSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ResourceMapObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ResourceMapsimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ResourceMapObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 * 
 * @author Samuel Andrés
 */
public interface ResourceMap extends AbstractObject {

    /**
     *
     * @return
     */
    public List<Alias> getAliases();

    /**
     *
     * @param aliases
     */
    public void setAliases(List<Alias> aliases);

}
