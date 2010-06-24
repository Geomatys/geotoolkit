package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

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
    public String getStyleUrl();

    /**
     *
     * @return
     */
    public AbstractStyleSelector getAbstractStyleSelector();

    /**
     *
     * @return the list of Pair simple extensions.
     */
    public List<SimpleType> getPairSimpleExtensions();

    /**
     *
     * @return the list of Pair object extensions.
     */
    public List<AbstractObject> getPairObjectExtensions();

    /**
     *
     * @param key
     */
    public void setKey(StyleState key);

    /**
     *
     * @param styleUrl
     */
    public void setStyleUrl(String styleUrl);

    /**
     * 
     * @param styleSelector
     */
    public void setAbstractStyleSelector(AbstractStyleSelector styleSelector);

    /**
     *
     * @param pairSimpleExtensions
     */
    public void setPairSimpleExtensions(List<SimpleType> pairSimpleExtensions);

    /**
     *
     * @param pairObjectExtensions
     */
    public void setPairObjectExtensions(List<AbstractObject> pairObjectExtensions);

}
