package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p<></p>
 *
 * <pre>
 * &lt;element name="Placemark" type="kml:PlacemarkType" substitutionGroup="kml:AbstractFeatureGroup"/>
 *
 * &lt;complexType name="PlacemarkType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractFeatureType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractGeometryGroup" minOccurs="0"/>
 *              &lt;element ref="kml:PlacemarkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PlacemarkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PlacemarkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PlacemarkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
