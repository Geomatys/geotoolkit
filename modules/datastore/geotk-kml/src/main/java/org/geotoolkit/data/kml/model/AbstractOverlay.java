package org.geotoolkit.data.kml.model;

import java.awt.Color;

/**
 * <p>This interface maps AbstractOverlayGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractOverlayGroup" type="kml:AbstractOverlayType" abstract="true" substitutionGroup="kml:AbstractFeatureGroup"/>
 *
 * &lt;complexType name="AbstractOverlayType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractFeatureType">
 *          &lt;sequence>
 *              &lt;element ref="kml:color" minOccurs="0"/>
 *              &lt;element ref="kml:drawOrder" minOccurs="0"/>
 *              &lt;element ref="kml:Icon" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
    public Icon getIcon();

    /**
     *
     * @param color
     */
    public void setColor(Color color);

    /**
     *
     * @param drawOrder
     */
    public void setDrawOrder(int drawOrder);

    /**
     *
     * @param link
     */
    public void setIcon(Icon link);

}
