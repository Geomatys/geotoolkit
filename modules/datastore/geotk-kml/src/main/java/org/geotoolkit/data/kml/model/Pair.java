package org.geotoolkit.data.kml.model;

import java.net.URI;

/**
 * <p>This interface maps Pair element.</p>
 *
 * <pre>
 * &lt;element name="Pair" type="kml:PairType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="PairType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *          &lt;element ref="kml:key" minOccurs="0"/>
 *          &lt;element ref="kml:styleUrl" minOccurs="0"/>
 *          &lt;element ref="kml:AbstractStyleSelectorGroup" minOccurs="0"/>
 *          &lt;element ref="kml:PairSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;element ref="kml:PairObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PairSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PairObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Pair extends AbstractObject {

    /**
     *
     * @return
     */
    public StyleState getKey();

    /**
     *
     * @return
     */
    public URI getStyleUrl();

    /**
     *
     * @return
     */
    public AbstractStyleSelector getAbstractStyleSelector();

    /**
     *
     * @param key
     */
    public void setKey(StyleState key);

    /**
     *
     * @param styleUrl
     */
    public void setStyleUrl(URI styleUrl);

    /**
     * 
     * @param styleSelector
     */
    public void setAbstractStyleSelector(AbstractStyleSelector styleSelector);

}
