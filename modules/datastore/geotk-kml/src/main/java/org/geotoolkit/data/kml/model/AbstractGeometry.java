package org.geotoolkit.data.kml.model;

/**
 *
 * <p>This interface maps AbstractGeometryGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractGeometryGroup" type="kml:AbstractGeometryType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractGeometryType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractGeometrySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractGeometryObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractGeometrySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractGeometryObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractGeometry extends AbstractObject{

}