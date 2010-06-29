package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Orientation element.</p>
 *
 * <pre>
 * &lt;element name="Orientation" type="kml:OrientationType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="OrientationType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element ref="kml:tilt" minOccurs="0"/>
 *              &lt;element ref="kml:roll" minOccurs="0"/>
 *              &lt;element ref="kml:OrientationSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:OrientationObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="OrientationSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="OrientationObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Orientation extends AbstractObject {

    /**
     *
     * @return
     */
    public double getHeading();

    /**
     *
     * @return
     */
    public double getTilt();

    /**
     *
     * @return
     */
    public double getRoll();

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

    /**
     *
     * @param heading
     */
    public void setHeading(double heading);

    /**
     *
     * @param tilt
     */
    public void setTilt(double tilt);

    /**
     *
     * @param roll
     */
    public void setRoll(double roll);

    /**
     *
     * @param orientationSimpleExtensions
     */
    public void setOrientationSimpleExtensions(List<SimpleType> orientationSimpleExtensions);

    /**
     * 
     * @param orientationObjectExtensions
     */
    public void setOrientationObjectExtensions(List<AbstractObject> orientationObjectExtensions);
}
