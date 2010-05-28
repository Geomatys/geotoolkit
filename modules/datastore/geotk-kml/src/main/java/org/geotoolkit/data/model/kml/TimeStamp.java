package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p></p>
 *
 * <br />&lt;element name="TimeStamp" type="kml:TimeStampType" substitutionGroup="kml:AbstractTimePrimitiveGroup"/>
 * <br />&lt;complexType name="TimeStampType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractTimePrimitiveType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:when" minOccurs="0"/>
 * <br />&lt;element ref="kml:TimeStampSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:TimeStampObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="TimeStampSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="TimeStampObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface TimeStamp extends AbstractTimePrimitive{

    /**
     *
     * @return
     */
    public String getWhen();

    /**
     *
     * @return the list of TimeStamp simple extensions.
     */
    public List<SimpleType> getTimeStampSimpleExtensions();

    /**
     *
     * @return the list of TimeStamp object extensions.
     */
    public List<AbstractObject> getTimeStampObjectExtensions();
}