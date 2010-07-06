package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 * <p>This interface maps Polygon element.</p>
 *
 * <pre>
 * &lt;element name="Polygon" type="kml:PolygonType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="PolygonType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:extrude" minOccurs="0"/>
 *              &lt;element ref="kml:tessellate" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:outerBoundaryIs" minOccurs="0"/>
 *              &lt;element ref="kml:innerBoundaryIs" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PolygonSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PolygonObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PolygonSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PolygonObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
    public Boundary getOuterBoundary();

    /**
     *
     * @return
     */
    public List<Boundary> getInnerBoundaries();

    /**
     *
     * @param extrude
     */
    public void setExtrude(boolean extrude);

    /**
     *
     * @param tesselate
     */
    public void setTessellate(boolean tesselate);

    /**
     *
     * @param altitudeMode
     */
    public void setAltitudeMode(AltitudeMode altitudeMode);
}
