package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Model element.</p>
 *
 * <br />&lt;element name="Model" type="kml:ModelType" substitutionGroup="kml:AbstractGeometryGroup"/>
 * <br />&lt;complexType name="ModelType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractGeometryType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:Location" minOccurs="0"/>
 * <br />&lt;element ref="kml:Orientation" minOccurs="0"/>
 * <br />&lt;element ref="kml:Scale" minOccurs="0"/>
 * <br />&lt;element ref="kml:Link" minOccurs="0"/>
 * <br />&lt;element ref="kml:ResourceMap" minOccurs="0"/>
 * <br />&lt;element ref="kml:ModelSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:ModelObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="ModelSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="ModelObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
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
