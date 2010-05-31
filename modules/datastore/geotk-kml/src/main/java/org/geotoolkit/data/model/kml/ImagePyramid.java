package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps ImagePyramid element.</p>
 *
 * <br />&lt;element name="ImagePyramid" type="kml:ImagePyramidType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="ImagePyramidType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:tileSize" minOccurs="0"/>
 * <br />&lt;element ref="kml:maxWidth" minOccurs="0"/>
 * <br />&lt;element ref="kml:maxHeight" minOccurs="0"/>
 * <br />&lt;element ref="kml:gridOrigin" minOccurs="0"/>
 * <br />&lt;element ref="kml:ImagePyramidSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:ImagePyramidObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="ImagePyramidSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="ImagePyramidObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface ImagePyramid extends AbstractObject {

    /**
     *
     * @return
     */
    public int getTitleSize();

    /**
     *
     * @return
     */
    public int getMaxWidth();

    /**
     *
     * @return
     */
    public int getMaxHeight();

    /**
     *
     * @return
     */
    public GridOrigin getGridOrigin();

    /**
     * 
     * @return the list of ImagePyramid simple extensions.
     */
    public List<SimpleType> getImagePyramidSimpleExtensions();

    /**
     *
     * @return the list of ImagePyramid object extensions.
     */
    public List<AbstractObject> getImagePyramidObjectExtensions();
}
