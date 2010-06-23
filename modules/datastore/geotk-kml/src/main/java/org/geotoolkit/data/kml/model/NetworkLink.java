package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps NetworkLink element.</p>
 *
 * <pre>
 * &lt;element name="NetworkLink" type="kml:NetworkLinkType" substitutionGroup="kml:AbstractFeatureGroup"/>
 *
 * &lt;complexType name="NetworkLinkType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractFeatureType">
 *          &lt;sequence>
 *              &lt;element ref="kml:refreshVisibility" minOccurs="0"/>
 *              &lt;element ref="kml:flyToView" minOccurs="0"/>
 *              &lt;choice>
 *                  &lt;annotation>
 *                      &lt;documentation>Url deprecated in 2.2</documentation>
 *                  &lt;/annotation>
 *                  &lt;element ref="kml:Url" minOccurs="0"/>
 *                  &lt;element ref="kml:Link" minOccurs="0"/>
 *              &lt;/choice>
 *              &lt;element ref="kml:NetworkLinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:NetworkLinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="NetworkLinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="NetworkLinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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

    /**
     *
     * @param refreshVisibility
     */
    public void setRefreshVisibility(boolean refreshVisibility);

    /**
     *
     * @param flyToView
     */
    public void setFlyToView(boolean flyToView);

    /**
     *
     * @param link
     */
    public void setLink(Link link);

    /**
     *
     * @param networkLinkSimpleExtensions
     */
    public void setNetworkLinkSimpleExtensions(List<SimpleType> networkLinkSimpleExtensions);

    /**
     *
     * @param networkLinkObjectExtensions
     */
    public void setNetworkLinkObjectExtensions(List<AbstractObject> networkLinkObjectExtensions);

}
