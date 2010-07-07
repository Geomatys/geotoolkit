package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 *
 * <p>This interface maps Data element.</p>
 *
 * <pre>
 * &lt;element name="Data" type="kml:DataType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="DataType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:displayName" minOccurs="0"/>
 *              &lt;element ref="kml:value"/>
 *              &lt;element ref="kml:DataExtension" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *          &lt;attribute name="name" type="string"/>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="DataExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Data extends AbstractObject {

    /**
     *
     * @return the name.
     */
    public String getName();

    /**
     *
     * @return the display name.
     */
    public Object getDisplayName();

    /**
     *
     * @return the value.
     */
    public String getValue();

    /**
     *
     * @return the list of data extensions.
     */
    public List<Object> getDataExtensions();

    /**
     *
     * @param name
     */
    public void setName(String name);

    /**
     *
     * @param displayName
     */
    public void setDisplayName(String displayName);

    /**
     *
     * @param value
     */
    public void setValue(String value);

    /**
     *
     * @param dataExtensions
     */
    public void setDataExtensions(List<Object> dataExtensions);
}
