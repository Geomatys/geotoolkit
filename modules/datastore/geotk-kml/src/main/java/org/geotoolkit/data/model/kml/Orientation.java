package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Orientation element.</p>
 *
 * <br />&lt;element name="Orientation" type="kml:OrientationType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="OrientationType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:heading" minOccurs="0"/>
 * <br />&lt;element ref="kml:tilt" minOccurs="0"/>
 * <br />&lt;element ref="kml:roll" minOccurs="0"/>
 * <br />&lt;element ref="kml:OrientationSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:OrientationObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="OrientationSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="OrientationObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>

 *
 * @author Samuel Andrés
 */
public interface Orientation extends AbstractObject {

    /**
     *
     * @return
     */
    public Angle360 getHeading();

    /**
     *
     * @return
     */
    public Anglepos180 getTilt();

    /**
     *
     * @return
     */
    public Angle180 getRoll();

    /**
     *
     * @return the list of Orientation simple extentions.
     */
    public List<SimpleType> getOrientationSimpleExtensions();

    /**
     *
     * @return the list of Orientation object extensions
     */
    public List<AbstractObject> getOrientationObjectExtensions();
}
