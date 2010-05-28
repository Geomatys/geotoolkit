package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps TimeSpan element.</p>
 *
 * <br />&lt;element name="TimeSpan" type="kml:TimeSpanType" substitutionGroup="kml:AbstractTimePrimitiveGroup"/>
 * <br />&lt;complexType name="TimeSpanType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractTimePrimitiveType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:begin" minOccurs="0"/>
 * <br />&lt;element ref="kml:end" minOccurs="0"/>
 * <br />&lt;element ref="kml:TimeSpanSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:TimeSpanObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="TimeSpanSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="TimeSpanObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface TimeSpan extends AbstractTimePrimitive{

    /**
     *
     * @return
     */
    public String getBegin();

    /**
     *
     * @return
     */
    public String getEnd();

    /**
     *
     * @return the list of TimeSpan simple extensions.
     */
    public List<SimpleType> getTimeSpanSimpleExtensions();

    /**
     *
     * @return the list of TimeSpan object extensions.
     */
    public List<AbstractObject> getTimeSpanObjectExtensions();
}