package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p<></p>
 *
 * <br />&lt;element name="Placemark" type="kml:PlacemarkType" substitutionGroup="kml:AbstractFeatureGroup"/>
 * <br />&lt;complexType name="PlacemarkType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractFeatureType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractGeometryGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:PlacemarkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:PlacemarkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="PlacemarkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="PlacemarkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Placemark extends AbstractFeature {

    /**
     *
     * @return
     */
    public AbstractGeometry getAbstractGeometry();

    /**
     *
     * @return the list of Placemark simple extensions.
     */
    public List<SimpleType> getPlacemarkSimpleExtensions();

    /**
     *
     * @return the list of Placemark object extensions.
     */
    public List<AbstractObject> getPlacemarkObjectExtensions();

}
