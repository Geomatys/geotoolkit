package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p></p>
 * <br />&lt;element name="PhotoOverlay" type="kml:PhotoOverlayType" substitutionGroup="kml:AbstractOverlayGroup"/>
 * <br />&lt;complexType name="PhotoOverlayType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractOverlayType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:rotation" minOccurs="0"/>
 * <br />&lt;element ref="kml:ViewVolume" minOccurs="0"/>
 * <br />&lt;element ref="kml:ImagePyramid" minOccurs="0"/>
 * <br />&lt;element ref="kml:Point" minOccurs="0"/>
 * <br />&lt;element ref="kml:shape" minOccurs="0"/>
 * <br />&lt;element ref="kml:PhotoOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:PhotoOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="PhotoOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="PhotoOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>

 *
 * @author Samuel Andrés
 */
public interface PhotoOverlay extends AbstractOverlay {

    /**
     *
     * @return
     */
    public Angle180 getRotation();

    /**
     *
     * @return
     */
    public ViewVolume getViewVolume();

    /**
     *
     * @return
     */
    public ImagePyramid getImagePyramid();

    /**
     *
     * @return
     */
    public Point getPoint();

    /**
     *
     * @return
     */
    public Shape getShape();

    /**
     *
     * @return the list of PhotoOverlay simple extensions.
     */
    public List<SimpleType> getPhotoOverlaySimpleExtensions();

    /**
     *
     * @return the list of PhotoOverlay object extensions.
     */
    public List<AbstractObject> getPhotoOverlayObjectExtensions();

}
