package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractColorStyle element.</p>
 *
 * <br />&lt;element name="AbstractColorStyleGroup" type="kml:AbstractColorStyleType" abstract="true" substitutionGroup="kml:AbstractSubStyleGroup"/>
 * <br />&lt;complexType name="AbstractColorStyleType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractSubStyleType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:color" minOccurs="0"/>
 * <br />&lt;element ref="kml:colorMode" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractColorStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractColorStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractColorStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;element name="AbstractColorStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
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
     * @return The AbstractColorStyle simple extensions.
     */
    public List<SimpleType> getColorStyleSimpleExtensions();

    /**
     *
     * @return The AbstractColorStyle object extensions.
     */
    public List<AbstractObject> getColorStyleObjectExtensions();
}