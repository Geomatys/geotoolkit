package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * <p>This interface maps Data element.</p>
 *
 * <br />&lt;element name="Data" type="kml:DataType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="DataType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:displayName" minOccurs="0"/>
 * <br />&lt;element ref="kml:value"/>
 * <br />&lt;element ref="kml:DataExtension" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;attribute name="name" type="string"/>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="DataExtension" abstract="true"/>
 *
 * @author Samuel Andrés
 */
public interface Data extends AbstractObject {

    /**
     *
     * @return the display name.
     */
    public String getDisplayName();

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
}
