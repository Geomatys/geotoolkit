

package org.opengis.feature.catalog;

import org.opengis.util.CodeList;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * <p>Java class for FC_RoleType_PropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FC_RoleType_PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.isotc211.org/2005/gfc}FC_RoleType"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.isotc211.org/2005/gco}nilReason"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
//@UML(identifier="FC_RoleType", specification=ISO_19110)
public class RoleType extends CodeList<RoleType> {


    /**
     * indicates an ordinary association
     */
    //@UML(identifier="ordinary", obligation=CONDITIONAL, specification=ISO_19119)
    public static final RoleType ORDINARY = valueOf("ORDINARY");

    /**
     * indicates a UML aggragation (part role)
     */
    //@UML(identifier="aggregation", obligation=CONDITIONAL, specification=ISO_19119)
    public static final RoleType AGGREGATION = valueOf("AGGREGATION");

    /**
     * indicates a UML composition (member role)
     */
    //@UML(identifier="composition", obligation=CONDITIONAL, specification=ISO_19119)
    public static final RoleType COMPOSITION = valueOf("COMPOSITION");


    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private RoleType(final String name) {
        super(name);
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public RoleType[] family() {
        return values(RoleType.class);
    }

    /**
     * Returns the FCRoleType that matches the given string, or returns a
     * new one if none match it.
     */
    public static RoleType valueOf(final String code) {
        return valueOf(RoleType.class, code, RoleType::new).get();
    }

}
