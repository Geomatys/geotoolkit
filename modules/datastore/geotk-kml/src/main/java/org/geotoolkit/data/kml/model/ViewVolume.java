package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps viewVolume element.</p>
 *
 * <pre>
 * &lt;element name="ViewVolume" type="kml:ViewVolumeType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ViewVolumeType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:leftFov" minOccurs="0"/>
 *              &lt;element ref="kml:rightFov" minOccurs="0"/>
 *              &lt;element ref="kml:bottomFov" minOccurs="0"/>
 *              &lt;element ref="kml:topFov" minOccurs="0"/>
 *              &lt;element ref="kml:near" minOccurs="0"/>
 *              &lt;element ref="kml:ViewVolumeSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ViewVolumeObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ViewVolumeSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ViewVolumeObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * <h3>OGC Documentation</h3>
 *
 * <p>Defines how much of the current scene in a kml:PhotoOverlay is visible. Specifying the
 * field of view is analogous to specifying the lens opening in a physical camera. A small field
 * of view, like a telephoto lens, focuses on a small part of the scene. A large field of view, like
 * a wide-angle lens, focuses on a large part of the scene.</p>
 *
 * <p>A kml:ViewVolume element shall contain the kml:leftFov, kml:rightFov,
 * kml:bottomFov, kml:topFov, and kml:near child elements outside of an update context,
 * that is when not a descendant of kml:Update.</p>
 *
 * @author Samuel Andrés
 */
public interface ViewVolume extends AbstractObject {

    /**
     *
     * @return
     */
    public double getLeftFov();

    /**
     *
     * @return
     */
    public double getRightFov();

    /**
     *
     * @return
     */
    public double getBottomFov();

    /**
     *
     * @return
     */
    public double getTopFov();

    /**
     *
     * @return
     */
    public double getNear();

    /**
     *
     * @return the list of ViewVolume simple extensions.
     */
    public List<SimpleType> getViewVolumeSimpleExtensions();

    /**
     *
     * @return the list of ViewVolume object extensions.
     */
    public List<AbstractObject> getViewVolumeObjectExtensions();

    /**
     *
     * @param leftFov
     */
    public void setLeftFov(double leftFov);

    /**
     *
     * @param rightFov
     */
    public void setRightFov(double rightFov);

    /**
     *
     * @param bottomFov
     */
    public void setBottomFov(double bottomFov);

    /**
     *
     * @param topFov
     */
    public void setTopFov(double topFov);

    /**
     *
     * @param near
     */
    public void setNear(double near);

    /**
     *
     * @param viewVolumeSimpleExtensions
     */
    public void setViewVolumeSimpleExtensions(List<SimpleType> viewVolumeSimpleExtensions);

    /**
     *
     * @param viewVolumeObjectExtensions
     */
    public void setViewVolumeObjectExtensions(List<AbstractObject> viewVolumeObjectExtensions);

}
