package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Lod element.</p>
 *
 * <pre>
 * &lt;element name="Lod" type="kml:LodType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="LodType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:minLodPixels" minOccurs="0"/>
 *              &lt;element ref="kml:maxLodPixels" minOccurs="0"/>
 *              &lt;element ref="kml:minFadeExtent" minOccurs="0"/>
 *              &lt;element ref="kml:maxFadeExtent" minOccurs="0"/>
 *              &lt;element ref="kml:LodSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LodObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LodSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LodObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Lod extends AbstractObject{

    /**
     *
     * @return
     */
    public double getMinLodPixels();

    /**
     *
     * @return
     */
    public double getMaxLodPixels();

    /**
     *
     * @return
     */
    public double getMinFadeExtent();

    /**
     *
     * @return
     */
    public double getMaxFadeExtent();

    /**
     *
     * @return the list of Lod simple extensions.
     */
    public List<SimpleType> getLodSimpleExtensions();

    /**
     *
     * @return the list of Lod object extensions.
     */
    public List<AbstractObject> getLodObjectExtensions();

    /**
     *
     * @param minLodPixels
     */
    public void setMinLodPixels(double minLodPixels);

    /**
     *
     * @param maxLodPixels
     */
    public void setMaxLodPixels(double maxLodPixels);

    /**
     *
     * @param minFadeExtent
     */
    public void setMinFadeExtent(double minFadeExtent);

    /**
     *
     * @param maxFadeExtent
     */
    public void setMaxFadeExtent(double maxFadeExtent);

    /**
     *
     * @param lodSimpleExtensions
     */
    public void setLodSimpleExtensions(List<SimpleType> lodSimpleExtensions);

    /**
     * 
     * @param lodObjectExtensions
     */
    public void setLodObjectExtensions(List<AbstractObject> lodObjectExtensions);

}
