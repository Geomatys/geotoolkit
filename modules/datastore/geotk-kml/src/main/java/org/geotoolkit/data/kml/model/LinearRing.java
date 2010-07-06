package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps LinearRing element.</p>
 *
 * <pre>
 * &lt;element name="LinearRing" type="kml:LinearRingType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="LinearRingType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:extrude" minOccurs="0"/>
 *              &lt;element ref="kml:tessellate" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:coordinates" minOccurs="0"/>
 *              &lt;element ref="kml:LinearRingSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LinearRingObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LinearRingSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LinearRingObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface LinearRing extends AbstractGeometry {

    /**
     *
     * @return
     */
    Coordinates getCoordinateSequence();
    
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
