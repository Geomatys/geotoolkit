package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps TimeStamp element.</p>
 *
 * <pre>
 * &lt;element name="TimeStamp" type="kml:TimeStampType" substitutionGroup="kml:AbstractTimePrimitiveGroup"/>
 *
 * &lt;complexType name="TimeStampType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractTimePrimitiveType">
 *          &lt;sequence>
 *              &lt;element ref="kml:when" minOccurs="0"/>
 *              &lt;element ref="kml:TimeStampSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:TimeStampObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="TimeStampSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="TimeStampObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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