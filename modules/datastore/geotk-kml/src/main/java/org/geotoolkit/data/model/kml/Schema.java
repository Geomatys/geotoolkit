package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p></p>
 *
 * <br />&lt;element name="Schema" type="kml:SchemaType"/>
 * <br />&lt;complexType name="SchemaType" final="#all">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:SimpleField" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:SchemaExtension" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;attribute name="name" type="string"/>
 * <br />&lt;attribute name="id" type="ID"/>
 * <br />&lt;/complexType>
 * <br />&lt;element name="SchemaExtension" abstract="true"/>
 *
 * @author Samuel Andrés
 */
public interface Schema {

    /**
     *
     * @return
     */
    public List<SimpleField> getSimpleFields();
    //public List<SchemaExtension> getSchemaExtensions();

    /**
     *
     * @return
     */
    public String getName();

    /**
     * 
     * @return
     */
    public String getId();
}
