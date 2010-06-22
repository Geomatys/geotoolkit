package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps iconStyle element.</p>
 *
 * <pre>
 * &lt;element name="IconStyle" type="kml:IconStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 *
 * &lt;complexType name="IconStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractColorStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:scale" minOccurs="0"/>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element name="Icon" type="kml:BasicLinkType" minOccurs="0"/>
 *              &lt;element ref="kml:hotSpot" minOccurs="0"/>
 *              &lt;element ref="kml:IconStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:IconStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="IconStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="IconStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
    public double getHeading();
    
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

    /**
     *
     * @param scale
     */
    public void setScale(double scale);

    /**
     *
     * @param heading
     */
    public void setHeading(double heading);

    /**
     *
     * @param icon
     */
    public void setIcon(BasicLink icon);

    /**
     *
     * @param hotSpot
     */
    public void setHotSpot(Vec2 hotSpot);

    /**
     *
     * @param iconStyleSimpleExtensions
     */
    public void setIconStyleSimpleExtensions(List<SimpleType> iconStyleSimpleExtensions);

    /**
     *
     * @param iconStyleObjectExtensions
     */
    public void setIconStyleObjectExtensions(List<AbstractObject> iconStyleObjectExtensions);
}
