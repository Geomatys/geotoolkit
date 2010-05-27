package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractFeatureGroup element.</p>
 *
 * <br />&lt;element name="AbstractFeatureGroup" type="kml:AbstractFeatureType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;omplexType name="AbstractFeatureType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:name" minOccurs="0"/>
 * <br />&lt;element ref="kml:visibility" minOccurs="0"/>
 * <br />&lt;element ref="kml:open" minOccurs="0"/>
 * <br />&lt;element ref="atom:author" minOccurs="0"/>
 * <br />&lt;element ref="atom:link" minOccurs="0"/>
 * <br />&lt;element ref="kml:address" minOccurs="0"/>
 * <br />&lt;element ref="xal:AddressDetails" minOccurs="0"/>
 * <br />&lt;element ref="kml:phoneNumber" minOccurs="0"/>
 * <br />&lt;choice>
 * <br />&lt;annotation>
 * <br />&lt;documentation>Snippet deprecated in 2.2&lt;/documentation>
 * <br />&lt;/annotation>
 * <br />&lt;element ref="kml:Snippet" minOccurs="0"/>
 * <br />&lt;element ref="kml:snippet" minOccurs="0"/>
 * <br />&lt;/choice>
 * <br />&lt;element ref="kml:description" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractViewGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractTimePrimitiveGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:styleUrl" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractStyleSelectorGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:Region" minOccurs="0"/>
 * <br />&lt;choice>
 * <br />&lt;annotation>
 * <br />&lt;documentation>Metadata deprecated in 2.2&lt;/documentation>
 * <br />&lt;/annotation>
 * <br />&lt;element ref="kml:Metadata" minOccurs="0"/>
 * <br />&lt;element ref="kml:ExtendedData" minOccurs="0"/>
 * <br />&lt;/choice>
 * <br />&lt;element ref="kml:AbstractFeatureSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractFeatureObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractFeatureObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;element name="AbstractFeatureSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 *
 * @author Samuel Andrés
 */
public interface AbstractFeature extends AbstractObject {

    /**
     *
     * @return The AbstractFeature name.
     */
    public String getName();

    /**
     *
     * @return The AbstractFeature visibility.
     */
    public boolean getVisibility();

    /**
     *
     * @return The AbstractFeature open.
     */
    public boolean getOpen();

    /**
     *
     * @return The AbstractFeature author.
     */
    public AtomPersonConstruct getAuthor();

    /**
     *
     * @return The AbstractFeature link.
     */
    public AtomLink getAtomLink();

    /**
     *
     * @return The AbstractFeature address.
     */
    public String getAddress();

    /**
     *
     * @return The AbstractFeature address details.
     */
    public AddressDetails getAddressDetails();

    /**
     *
     * @return The AbstractFeature phone number.
     */
    public String getPhoneNumber();

    /**
     *
     * @return The AbstractFeature snippet.
     */
    public String getSnippet();

    /**
     *
     * @return The AbstractFeature description.
     */
    public String getDescription();

    /**
     *
     * @return The AbstractFeature view.
     */
    public AbstractView getView();

    /**
     *
     * @return The AbstractFeature time primitive.
     */
    public AbstractTimePrimitive getTimePrimitive();

    /**
     *
     * @return The AbstractFeature style url.
     */
    public String getStyleUrl();

    /**
     *
     * @return The AbstractFeature list of style selectors.
     */
    public List<AbstractStyleSelector> getStyleSelectors();

    /**
     *
     * @return The AbstractFeature region.
     */
    public Region getRegion();

    /**
     *
     * @return The AbstractFeature extended data.
     */
    public ExtendedData getExtendedData();

    /**
     *
     * @return The AbstractFeature list of simple extensions.
     */
    public List<SimpleType> getAbstractFeatureSimpleExtensions();

    /**
     *
     * @return The AbstractFeature list of object extensions.
     */
    public List<AbstractObject> getAbstractFeatureObjectExtensions();
}
