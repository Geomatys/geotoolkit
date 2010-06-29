package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Region element.</p>
 *
 * <pre>
 * &lt;element name="Region" type="kml:RegionType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="RegionType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:LatLonAltBox" minOccurs="0"/>
 *              &lt;element ref="kml:Lod" minOccurs="0"/>
 *              &lt;element ref="kml:RegionSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:RegionObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="RegionSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="RegionObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Region extends AbstractObject{

    /**
     *
     * @return
     */
    public LatLonAltBox getLatLonAltBox();

    /**
     *
     * @return
     */
    public Lod getLod();

    /**
     *
     * @return the list of Region simple extensions.
     */
    public List<SimpleType> getRegionSimpleExtensions();

    /**
     *
     * @return the list of Region object extensions.
     */
    public List<AbstractObject> getRegionObjectExtensions();

    /**
     *
     * @param latLonAltBox
     */
    public void setLatLonAltBox(LatLonAltBox latLonAltBox);

    /**
     *
     * @param lod
     */
    public void setLod(Lod lod);

    /**
     *
     * @param regionSimpleExtensions
     */
    public void setRegionSimpleExtensions(List<SimpleType> regionSimpleExtensions);

    /**
     * 
     * @param regionObjectExtensions
     */
    public void setRegionObjectExtensions(List<AbstractObject> regionObjectExtensions);

}
