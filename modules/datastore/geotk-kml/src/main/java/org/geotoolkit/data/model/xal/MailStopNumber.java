package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps MailStopNumber element.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="NameNumberSeparator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
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
