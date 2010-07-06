package org.geotoolkit.data.kml.model;

import java.awt.Color;

/**
 * <p>This interface maps AbstractColorStyle element.</p>
 *
 * <pre>
 * &lt;element name="AbstractColorStyleGroup" type="kml:AbstractColorStyleType" abstract="true" substitutionGroup="kml:AbstractSubStyleGroup"/>
 *
 * &lt;complexType name="AbstractColorStyleType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractSubStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:color" minOccurs="0"/>
 *              &lt;element ref="kml:colorMode" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractColorStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractColorStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractColorStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * &lt;element name="AbstractColorStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractColorStyle extends AbstractSubStyle {

    /**
     *
     * @return The AbstractColorStyle color.
     */
    public Color getColor();

    /**
     *
     * @return The AbstractColorStyle color mode.
     */
    public ColorMode getColorMode();

    /**
     *
     * @param color
     */
    public void setColor(Color color);

    /**
     *
     * @param colorMode
     */
    public void setColorMode(ColorMode colorMode);
}