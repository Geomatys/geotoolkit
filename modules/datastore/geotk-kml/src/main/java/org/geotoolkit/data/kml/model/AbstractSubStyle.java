package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps AbstractSubStyle element.</p>
 *
 * <pre>
 * &lt;element name="AbstractSubStyleGroup" type="kml:AbstractSubStyleType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractSubStyleType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractSubStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractSubStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractSubStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractSubStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 * 
 * @author Samuel Andrés
 */
public interface AbstractSubStyle extends AbstractObject {

}
