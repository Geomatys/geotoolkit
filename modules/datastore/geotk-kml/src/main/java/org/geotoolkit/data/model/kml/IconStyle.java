package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps iconStyle element.</p>
 *
 * <br />&lt;element name="IconStyle" type="kml:IconStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 * <br />&lt;complexType name="IconStyleType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractColorStyleType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:scale" minOccurs="0"/>
 * <br />&lt;element ref="kml:heading" minOccurs="0"/>
 * <br />&lt;element name="Icon" type="kml:BasicLinkType" minOccurs="0"/>
 * <br />&lt;element ref="kml:hotSpot" minOccurs="0"/>
 * <br />&lt;element ref="kml:IconStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:IconStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="IconStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="IconStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface IconStyle extends AbstractColorStyle {

    /**
     *
     * @return
     */
    public double getScale();

    /**
     *
     * @return
     */
    public Angle360 getHeading();
    
    /**
     * 
     * @return
     */
    public BasicLink getIcon();

    /**
     * 
     * @return
     */
    public Vec2 getHotSpot();

    /**
     *
     * @return
     */
    public List<SimpleType> getIconStyleSimpleExtensions();

    /**
     * 
     * @return
     */
    public List<AbstractObject> getIconStyleObjectExtensions();
}
