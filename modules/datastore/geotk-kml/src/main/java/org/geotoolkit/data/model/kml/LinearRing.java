package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps LinearRing element.</p>
 *
 * <br />&lt;element name="LinearRing" type="kml:LinearRingType" substitutionGroup="kml:AbstractGeometryGroup"/>
 * <br />&lt;complexType name="LinearRingType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractGeometryType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:extrude" minOccurs="0"/>
 * <br />&lt;element ref="kml:tessellate" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:coordinates" minOccurs="0"/>
 * <br />&lt;element ref="kml:LinearRingSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LinearRingObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LinearRingSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LinearRingObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface LinearRing extends AbstractGeometry {

    /**
     *
     * @return
     */
    public boolean getExtrude();

    /**
     *
     * @return
     */
    public boolean getTessellate();

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
     * @return the list of LinearRing simple extensions.
     */
    public List<SimpleType> getLinearRingSimpleExtensions();

    /**
     *
     * @return the list of LinearRing object extensions.
     */
    public List<AbstractObject> getLinearRingObjectExtensions();

}
