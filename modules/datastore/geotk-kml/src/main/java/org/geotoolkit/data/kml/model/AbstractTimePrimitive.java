package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps AbstractTimePrimitiveGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractTimePrimitiveGroup" type="kml:AbstractTimePrimitiveType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractTimePrimitiveType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractTimePrimitiveSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractTimePrimitiveObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractTimePrimitiveSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractTimePrimitiveObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *</pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractTimePrimitive extends AbstractObject {
}
