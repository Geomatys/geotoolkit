package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps BoundaryType type used by :</p>
 * <ul>
 * <li>outerBoundaryIs element;</li>
 * <li>innerBoundaryIs element.</li>
 * </ul>
 *
 * <pre>
 * &lt;element name="outerBoundaryIs" type="kml:BoundaryType"/>
 * &lt;element name="innerBoundaryIs" type="kml:BoundaryType"/>
 *
 * &lt;complexType name="BoundaryType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:LinearRing" minOccurs="0"/>
 *      &lt;element ref="kml:BoundarySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:BoundaryObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 *
 * &lt;element name="BoundarySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="BoundaryObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Boundary {

    /**
     *
     * @return the linear ring geometry.
     */
    public LinearRing getLinearRing();

    /**
     * 
     * @param linearRing
     */
    public void setLinearRing(LinearRing linearRing);

    /**
     * 
     * @return
     */
    public Extensions extensions();
}
