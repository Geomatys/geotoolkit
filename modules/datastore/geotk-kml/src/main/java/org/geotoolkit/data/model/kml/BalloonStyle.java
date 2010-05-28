package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps BalloonStyle elements.</p>
 *
 * <br />&lt;element name="BalloonStyle" type="kml:BalloonStyleType" substitutionGroup="kml:AbstractSubStyleGroup"/>
 * <br />&lt;complexType name="BalloonStyleType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractSubStyleType">
 * <br />&lt;sequence>
 * <br />&lt;choice>
 * <br />&lt;annotation>
 * <br />&lt;documentation>color deprecated in 2.1&lt;/documentation>
 * <br />&lt;/annotation>
 * <br />&lt;element ref="kml:color" minOccurs="0"/>
 * <br />&lt;element ref="kml:bgColor" minOccurs="0"/>
 * <br />&lt;/choice>
 * <br />&lt;element ref="kml:textColor" minOccurs="0"/>
 * <br />&lt;element ref="kml:text" minOccurs="0"/>
 * <br />&lt;element ref="kml:displayMode" minOccurs="0"/>
 * <br />&lt;element ref="kml:BalloonStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:BalloonStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="BalloonStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="BalloonStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>

 * @author Samuel Andrés
 */
public interface BalloonStyle extends AbstractSubStyle {

    /**
     *
     * @return the background color.
     */
    public Color getBgColor();

    /**
     *
     * @return the text color.
     */
    public Color getTextColor();

    /**
     *
     * @return the text content.
     */
    public String getText();

    /**
     *
     * @return the display mode
     */
    public DisplayMode getDisplayMode();

    /**
     *
     * @return the BalloonStyle simple extensions.
     */
    public List<SimpleType> getBalloonStyleSimpleExtensions();

    /**
     *
     * @return the Balloon Style object extensions.
     */
    public List<AbstractObject> getBalloonStyleObjectExtensions();

}
