@XmlSchema(elementFormDefault= XmlNsForm.QUALIFIED,
namespace="http://www.isotc211.org/2005/gfc",
xmlns = {
    @XmlNs(prefix = "gfc", namespaceURI = "http://www.isotc211.org/2005/gfc"),
    @XmlNs(prefix = "gmd", namespaceURI = "http://www.isotc211.org/2005/gmd"),
    @XmlNs(prefix = "gco", namespaceURI = "http://www.isotc211.org/2005/gco"),
    @XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance")
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    // ISO 19115 adapter (metadata module)
//  @XmlJavaTypeAdapter(ScopedNameAdapter.class), // TODO
    @XmlJavaTypeAdapter(GO_GenericName.class),
    @XmlJavaTypeAdapter(CI_ResponsibleParty.class),
    @XmlJavaTypeAdapter(CI_Citation.class),
    // ISO 19110 adapter
    @XmlJavaTypeAdapter(FeatureCatalogueAdapter.class),
    @XmlJavaTypeAdapter(FeatureAssociationAdapter.class),
    @XmlJavaTypeAdapter(AssociationRoleAdapter.class),
    @XmlJavaTypeAdapter(InheritanceRelationAdapter.class),
    @XmlJavaTypeAdapter(FeatureTypeAdapter.class),
    @XmlJavaTypeAdapter(DefinitionSourceAdapter.class),
    @XmlJavaTypeAdapter(PropertyTypeAdapter.class),
    @XmlJavaTypeAdapter(ConstraintAdapter.class),
    @XmlJavaTypeAdapter(DefinitionReferenceAdapter.class),
    @XmlJavaTypeAdapter(ListedValueAdapter.class),
    @XmlJavaTypeAdapter(BoundFeatureAttributeAdapter.class),
    @XmlJavaTypeAdapter(FeatureAttributeAdapter.class),
    @XmlJavaTypeAdapter(FeatureOperationAdapter.class),
    @XmlJavaTypeAdapter(BindingAdapter.class),
    //CodeList handling
    @XmlJavaTypeAdapter(RoleTypeAdapter.class),
    // Primitive type handling
    @XmlJavaTypeAdapter(StringAdapter.class),
    @XmlJavaTypeAdapter(GO_DateTime.class),
    @XmlJavaTypeAdapter(GO_Decimal.class),
    @XmlJavaTypeAdapter(type=double.class, value=GO_Decimal.class),
    @XmlJavaTypeAdapter(GO_Decimal32.class),
    @XmlJavaTypeAdapter(type=float.class, value=GO_Decimal32.class),
    @XmlJavaTypeAdapter(GO_Integer.class),
    @XmlJavaTypeAdapter(type=int.class, value=GO_Integer.class),
    @XmlJavaTypeAdapter(GO_Integer64.class),
    @XmlJavaTypeAdapter(type=long.class, value=GO_Integer64.class),
    @XmlJavaTypeAdapter(GO_Boolean.class),
    @XmlJavaTypeAdapter(type=boolean.class, value=GO_Boolean.class)
})
package org.geotoolkit.feature.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.apache.sis.internal.jaxb.gco.*;
import org.apache.sis.internal.jaxb.metadata.*;
import org.geotoolkit.resources.jaxb.feature.catalog.*;
import org.geotoolkit.resources.jaxb.feature.catalog.code.*;
import org.geotoolkit.resources.jaxb.feature.catalog.GO_Decimal32;
