package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps Placemark element.</p>
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
     * @param abstractGeometry
     */
    public void setAbstractGeometry(AbstractGeometry abstractGeometry);

}
