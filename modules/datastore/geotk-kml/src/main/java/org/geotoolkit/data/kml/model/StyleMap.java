package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps StyleMap element.</p>
 *
 * <pre>
 * &lt;element name="StyleMap" type="kml:StyleMapType" substitutionGroup="kml:AbstractStyleSelectorGroup"/>
 *
 * &lt;complexType name="StyleMapType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractStyleSelectorType">
 *          &lt;sequence>
 *              &lt;element ref="kml:Pair" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:StyleMapSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:StyleMapObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="StyleMapSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="StyleMapObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface StyleMap extends AbstractStyleSelector {

    /**
     *
     * @return
     */
    public List<Pair> getPairs();

    /**
     *
     * @return the list of StyleMap simple extensions.
     */
    public List<SimpleType> getStyleMapSimpleExtensions();

    /**
     *
     * @return the list of StyleMap object extensions.
     */
    public List<AbstractObject> getStyleMapObjectExtensions();

}
