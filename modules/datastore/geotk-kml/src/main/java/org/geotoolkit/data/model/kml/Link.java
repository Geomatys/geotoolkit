package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps LinkType type</p>
 * 
 * <br />&lt;complexType name="LinkType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:BasicLinkType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:refreshMode" minOccurs="0"/>
 * <br />&lt;element ref="kml:refreshInterval" minOccurs="0"/>
 * <br />&lt;element ref="kml:viewRefreshMode" minOccurs="0"/>
 * <br />&lt;element ref="kml:viewRefreshTime" minOccurs="0"/>
 * <br />&lt;element ref="kml:viewBoundScale" minOccurs="0"/>
 * <br />&lt;element ref="kml:viewFormat" minOccurs="0"/>
 * <br />&lt;element ref="kml:httpQuery" minOccurs="0"/>
 * <br />&lt;element ref="kml:LinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Link extends AbstractObject {

    /**
     *
     * @return
     */
    public String getHref();

    /**
     *
     * @return
     */
    public List<SimpleType> getBasicLinkSimpleExtensions();

    /**
     *
     * @return
     */
    public List<AbstractObject> getBasicLinkObjectExtensions();

    /**
     *
     * @return
     */
    public RefreshMode getRefreshMode();

    /**
     *
     * @return
     */
    public double getRefreshInterval();

    /**
     *
     * @return
     */
    public ViewRefreshMode getViewRefreshMode();

    /**
     *
     * @return
     */
    public double getViewRefreshTime();

    /**
     *
     * @return
     */
    public double getViewBoundScale();

    /**
     *
     * @return
     */
    public String getViewFormat();

    /**
     *
     * @return
     */
    public String getHttpQuery();

    /**
     *
     * @return the list of Link simple extensions.
     */
    public List<SimpleType> getLinkSimpleExtensions();

    /**
     *
     * @return the list of Link object extensions.
     */
    public List<AbstractObject> getLinkObjectExtensions();
}
