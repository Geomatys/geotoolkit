package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Polygon element.</p>
 *
 * <br />&lt;element name="Polygon" type="kml:PolygonType" substitutionGroup="kml:AbstractGeometryGroup"/>
 * <br />&lt;complexType name="PolygonType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractGeometryType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:extrude" minOccurs="0"/>
 * <br />&lt;element ref="kml:tessellate" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:outerBoundaryIs" minOccurs="0"/>
 * <br />&lt;element ref="kml:innerBoundaryIs" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:PolygonSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:PolygonObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="PolygonSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="PolygonObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Polygon extends AbstractGeometry {

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
    public Boundary getOuterBoundaryIs();

    /**
     *
     * @return
     */
    public List<Boundary> getInnerBoundariesAre();

    /**
     *
     * @return the list of Polygon simple extensions.
     */
    public List<SimpleType> getPolygonSimpleExtensions();

    /**
     *
     * @return th elist of Polygon object extensions.
     */
    public List<AbstractObject> getPolygonObjectExtensions();
}
