package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps ImagePyramid element.</p>
 *
 * <pre>
 * &lt;element name="ImagePyramid" type="kml:ImagePyramidType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ImagePyramidType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:tileSize" minOccurs="0"/>
 *              &lt;element ref="kml:maxWidth" minOccurs="0"/>
 *              &lt;element ref="kml:maxHeight" minOccurs="0"/>
 *              &lt;element ref="kml:gridOrigin" minOccurs="0"/>
 *              &lt;element ref="kml:ImagePyramidSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ImagePyramidObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ImagePyramidSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ImagePyramidObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
