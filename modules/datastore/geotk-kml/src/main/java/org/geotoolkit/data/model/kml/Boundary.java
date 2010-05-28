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
 * <br />&lt;element name="outerBoundaryIs" type="kml:BoundaryType"/>
 * <br />&lt;element name="innerBoundaryIs" type="kml:BoundaryType"/>
 * <br />&lt;complexType name="BoundaryType" final="#all">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:LinearRing" minOccurs="0"/>
 * <br />&lt;element ref="kml:BoundarySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:BoundaryObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/complexType>
 * <br />&lt;element name="BoundarySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="BoundaryObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
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
