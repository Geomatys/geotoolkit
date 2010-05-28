package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps NetworkLink element.</p>
 *
 * <br />&lt;element name="NetworkLink" type="kml:NetworkLinkType" substitutionGroup="kml:AbstractFeatureGroup"/>
 * <br />&lt;complexType name="NetworkLinkType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractFeatureType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:refreshVisibility" minOccurs="0"/>
 * <br />&lt;element ref="kml:flyToView" minOccurs="0"/>
 * <br />&lt;choice>
 * <br />&lt;annotation>
 * <br />&lt;documentation>Url deprecated in 2.2</documentation>
 * <br />&lt;/annotation>
 * <br />&lt;element ref="kml:Url" minOccurs="0"/>
 * <br />&lt;element ref="kml:Link" minOccurs="0"/>
 * <br />&lt;/choice>
 * <br />&lt;element ref="kml:NetworkLinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:NetworkLinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="NetworkLinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="NetworkLinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface NetworkLink extends AbstractFeature {

    /**
     *
     * @return
     */
    public boolean getRefreshVisibility();

    /**
     *
     * @return
     */
    public boolean getFlyToView();

    /**
     *
     * @return
     */
    public Link getLink();

    /**
     *
     * @return the list of NetworkLink simple extensions.
     */
    public List<SimpleType> getNetworkLinkSimpleExtensions();

    /**
     *
     * @return the list of NetworkLink object extensions.
     */
    public List<AbstractObject> getNetworkLinkObjectExtensions();

}
