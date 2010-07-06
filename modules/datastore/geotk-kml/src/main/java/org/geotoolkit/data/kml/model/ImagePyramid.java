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
     * @param titleSize
     */
    public void setTitleSize(int titleSize);

    /**
     *
     * @param maxWidth
     */
    public void setMaxWidth(int maxWidth);

    /**
     *
     * @param maxHeight
     */
    public void setMaxHeight(int maxHeight);

    /**
     *
     * @param gridOrigin
     */
    public void setGridOrigin(GridOrigin gridOrigin);

}
