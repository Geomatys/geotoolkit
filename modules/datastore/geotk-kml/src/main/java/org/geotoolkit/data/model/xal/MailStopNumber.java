package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps MailStopNumber element.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NameNumberSeparator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface MailStopNumber {

    /**
     * 
     * @return
     */
    public String getContent();

    /**
     * <p>"-" in MS-123.</p>
     *
     * @return
     */
    public String getNameNumberSeparator();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();
}
