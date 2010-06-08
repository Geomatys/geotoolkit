package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps TimeSpan element.</p>
 *
 * <pre>
 * &lt;element name="TimeSpan" type="kml:TimeSpanType" substitutionGroup="kml:AbstractTimePrimitiveGroup"/>
 *
 * &lt;complexType name="TimeSpanType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractTimePrimitiveType">
 *          &lt;sequence>
 *              &lt;element ref="kml:begin" minOccurs="0"/>
 *              &lt;element ref="kml:end" minOccurs="0"/>
 *              &lt;element ref="kml:TimeSpanSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:TimeSpanObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="TimeSpanSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="TimeSpanObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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