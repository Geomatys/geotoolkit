package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps AbstractGeometryGroup element.</p>
 *
 * <br />&lt;element name="AbstractGeometryGroup" type="kml:AbstractGeometryType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="AbstractGeometryType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractGeometrySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractGeometryObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractGeometrySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AbstractGeometryObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface AbstractGeometry extends AbstractObject{

    /**
     *
     * @return The AbstractGeometry simple extensions.
     */
    public List<SimpleType> getAbstractGeometrySimpleExtensions();

    /**
     *
     * @return The AbstractGeometry object extensions.
     */
    public List<AbstractObject> getAbstractGeometryObjectExtensions();
}
