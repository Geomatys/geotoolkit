package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Pair element.</p>
 *
 * <br />&lt;element name="Pair" type="kml:PairType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="PairType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:key" minOccurs="0"/>
 * <br />&lt;element ref="kml:styleUrl" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractStyleSelectorGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:PairSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:PairObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="PairSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="PairObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
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

}
