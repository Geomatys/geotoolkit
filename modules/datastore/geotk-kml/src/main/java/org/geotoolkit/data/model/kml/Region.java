package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Region element.</p>
 *
 * <br />&lt;element name="Region" type="kml:RegionType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="RegionType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:LatLonAltBox" minOccurs="0"/>
 * <br />&lt;element ref="kml:Lod" minOccurs="0"/>
 * <br />&lt;element ref="kml:RegionSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:RegionObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="RegionSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="RegionObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
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

}
