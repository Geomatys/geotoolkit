package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

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

}
