package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Model element.</p>
 *
 * <pre>
 * &lt;element name="Model" type="kml:ModelType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="ModelType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:Location" minOccurs="0"/>
 *              &lt;element ref="kml:Orientation" minOccurs="0"/>
 *              &lt;element ref="kml:Scale" minOccurs="0"/>
 *              &lt;element ref="kml:Link" minOccurs="0"/>
 *              &lt;element ref="kml:ResourceMap" minOccurs="0"/>
 *              &lt;element ref="kml:ModelSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ModelObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ModelSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ModelObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Model extends AbstractGeometry {

    /**
     *
     * @return
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @return
     */
    public Location getLocation();

    /**
     *
     * @return
     */
    public Orientation getOrientation();

    /**
     *
     * @return
     */
    public Scale getScale();

    /**
     *
     * @return
     */
    public Link getLink();

    /**
     *
     * @return
     */
    public ResourceMap getRessourceMap();

    /**
     *
     * @return the list of Model simple extensions.
     */
    public List<SimpleType> getModelSimpleExtensions();

    /**
     *
     * @return the list of Model object extensions.
     */
    public List<AbstractObject> getModelObjectExtensions();

}
