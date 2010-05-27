package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractOverlayGroup element.</p>
 *
 * <br />&lt;element name="AbstractOverlayGroup" type="kml:AbstractOverlayType" abstract="true" substitutionGroup="kml:AbstractFeatureGroup"/>
 * <br />&lt;complexType name="AbstractOverlayType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractFeatureType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:color" minOccurs="0"/>
 * <br />&lt;element ref="kml:drawOrder" minOccurs="0"/>
 * <br />&lt;element ref="kml:Icon" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AbstractOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface AbstractOverlay extends AbstractFeature{

    /**
     *
     * @return the color.
     */
    public Color getColor();

    /**
     *
     * @return the drawOrder.
     */
    public int getDrawOrder();

    /**
     *
     * @return the icon link.
     */
    public Link getIcon();

    /**
     *
     * @return the list of AbstractOverlay simple extensions.
     */
    public List<SimpleType> getAbstractOverlaySimpleExtensions();

    /**
     *
     * @return the list of AbstractOverlay object extensions.
     */
    public List<AbstractObject> getAbstractOverlayObjectExtensions();
}
