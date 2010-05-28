package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps NetworkLinkControl element.</p>
 *
 * <br />&lt;element name="NetworkLinkControl" type="kml:NetworkLinkControlType"/>
 * <br />&lt;complexType name="NetworkLinkControlType" final="#all">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:minRefreshPeriod" minOccurs="0"/>
 * <br />&lt;element ref="kml:maxSessionLength" minOccurs="0"/>
 * <br />&lt;element ref="kml:cookie" minOccurs="0"/>
 * <br />&lt;element ref="kml:message" minOccurs="0"/>
 * <br />&lt;element ref="kml:linkName" minOccurs="0"/>
 * <br />&lt;element ref="kml:linkDescription" minOccurs="0"/>
 * <br />&lt;element ref="kml:linkSnippet" minOccurs="0"/>
 * <br />&lt;element ref="kml:expires" minOccurs="0"/>
 * <br />&lt;element ref="kml:Update" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractViewGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:NetworkLinkControlSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:NetworkLinkControlObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/complexType>
 * <br />&lt;element name="NetworkLinkControlSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="NetworkLinkControlObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface NetworkLinkControl {

    /**
     *
     * @return
     */
    public double getMinRefreshPeriod();

    /**
     *
     * @return
     */
    public double getMaxSessionLength();

    /**
     *
     * @return
     */
    public String getCookie();

    /**
     *
     * @return
     */
    public String getMessage();

    /**
     *
     * @return
     */
    public String getLinkName();

    /**
     *
     * @return
     */
    public String getLinkDescription();

    /**
     *
     * @return
     */
    public Snippet getLinkSnippet();

    /**
     *
     * @return
     */
    public String getExpire();

    /**
     *
     * @return
     */
    public Update getUpdate();

    /**
     *
     * @return
     */
    public AbstractView getView();

    /**
     *
     * @return the list of NetworkLinkControl simple extensions.
     */
    public List<SimpleType> getNetworkLinkControlSimpleExtensions();

    /**
     *
     * @return the list of NetworkLinkControl object extensions.
     */
    public List<AbstractObject> getNetworkLinkControlObjectExtensions();

}