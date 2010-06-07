package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Point element.</p>
 *
 * <pre>
 * &lt;element name="Point" type="kml:PointType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="PointType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:extrude" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:coordinates" minOccurs="0"/>
 *              &lt;element ref="kml:PointSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PointObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PointSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PointObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Point extends AbstractGeometry{

    /**
     *
     * @return
     */
    public boolean getExtrude();

    /**
     *
     * @return
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @return
     */
    public Coordinates getCoordinates();

    /**
     *
     * @return the list of Point simple extensions.
     */
    public List<SimpleType> getPointSimpleExtensions();

    /**
     * 
     * @return the list of Point object extensions.
     */
    public List<AbstractObject> getPointObjectExtensions();
}
