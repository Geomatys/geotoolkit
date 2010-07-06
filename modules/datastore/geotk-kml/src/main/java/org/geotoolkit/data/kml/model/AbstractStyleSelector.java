package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps AbstractStyleSelectorGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractStyleSelectorGroup" type="kml:AbstractStyleSelectorType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractStyleSelectorType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractStyleSelectorSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractStyleSelectorObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractStyleSelectorSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractStyleSelectorObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractStyleSelector extends AbstractObject {

}
