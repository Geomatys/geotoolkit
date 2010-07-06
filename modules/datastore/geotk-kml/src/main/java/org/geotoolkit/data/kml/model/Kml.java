package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps a kml element</p>
 *
 * <pre>
 * &lt;element name="kml" type="kml:KmlType"/>
 *
 * &lt;complexType name="KmlType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:NetworkLinkControl" minOccurs="0"/>
 *      &lt;element ref="kml:AbstractFeatureGroup" minOccurs="0"/>
 *      &lt;element ref="kml:KmlSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:KmlObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attribute name="hint" type="string"/>
 * &lt;/complexType>
 *
 * &lt;lement name="KmlSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="KmlObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Kml {

    /**
     *
     * @return
     */
    public String getVersion();

    /**
     *
     * @return The Kml NetworkLinkControl.
     */
    public NetworkLinkControl getNetworkLinkControl();

    /**
     *
     * @return The Kml AbstractFeature.
     */
    public AbstractFeature getAbstractFeature();

    /**
     * 
     * @param version
     */
    public void setVersion(String version) throws KmlException;

    /**
     * 
     * @param networkLinkCOntrol
     */
    public void setNetworkLinkControl(NetworkLinkControl networkLinkCOntrol);

    /**
     * 
     * @param feature
     */
    public void setAbstractFeature(AbstractFeature feature);

    /**
     * 
     * @return
     */
    public Extensions extensions();

}
