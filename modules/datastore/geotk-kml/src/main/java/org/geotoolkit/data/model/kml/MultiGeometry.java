package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps MultiGeometry element.</p>
 *
 * <br />&lt;element name="MultiGeometry" type="kml:MultiGeometryType" substitutionGroup="kml:AbstractGeometryGroup"/>
 * <br />&lt;complexType name="MultiGeometryType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractGeometryType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractGeometryGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:MultiGeometrySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:MultiGeometryObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="MultiGeometrySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="MultiGeometryObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface MultiGeometry extends AbstractGeometry {

    /**
     *
     * @return
     */
    public List<AbstractGeometry> getGeometries();

    /**
     *
     * @return the list of MultiGeometry simple extensions.
     */
    public List<SimpleType> getMultiGeometrySimpleExtensions();

    /**
     *
     * @return the list of MultiGeometry object extensions.
     */
    public List<AbstractObject> getMultiGeometryObjectExtensions();

}
