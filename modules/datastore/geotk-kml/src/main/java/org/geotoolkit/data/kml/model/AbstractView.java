package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps AbstractViewGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractViewGroup" type="kml:AbstractViewType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractViewType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractViewSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractViewObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractViewSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractViewObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractView extends AbstractObject{

}
