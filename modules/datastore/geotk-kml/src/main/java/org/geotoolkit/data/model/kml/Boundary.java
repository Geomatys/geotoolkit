package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

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
     * @return the list of boundary simple extensions.
     */
    public List<SimpleType> getBoundarySimpleExtensions();

    /**
     *
     * @return the list of boundary object extensions.
     */
    public List<AbstractObject> getBoundaryObjectExtensions();

}
