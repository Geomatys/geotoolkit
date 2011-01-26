/**
 * An explanation
 * for this package is provided in the {@linkplain org.opengis.service OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the GeotoolKit implementation.
 */
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
    @XmlJavaTypeAdapter(ScopedNameAdapter.class),
    @XmlJavaTypeAdapter(LocalNameAdapter.class),
    @XmlJavaTypeAdapter(GenericNameAdapter.class),
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
    @XmlJavaTypeAdapter(UnlimitedIntegerAdapter.class),
    @XmlJavaTypeAdapter(MultiplicityAdapter.class),
    @XmlJavaTypeAdapter(MultiplicityRangeAdapter.class),
    @XmlJavaTypeAdapter(StringAdapter.class),
    @XmlJavaTypeAdapter(DateAdapter.class),
    @XmlJavaTypeAdapter(DoubleAdapter.class),
    @XmlJavaTypeAdapter(type=double.class, value=DoubleAdapter.class),
    @XmlJavaTypeAdapter(FloatAdapter.class),
    @XmlJavaTypeAdapter(type=float.class, value=FloatAdapter.class),
    @XmlJavaTypeAdapter(IntegerAdapter.class),
    @XmlJavaTypeAdapter(type=int.class, value=IntegerAdapter.class),
    @XmlJavaTypeAdapter(LongAdapter.class),
    @XmlJavaTypeAdapter(type=long.class, value=LongAdapter.class),
    @XmlJavaTypeAdapter(BooleanAdapter.class),
    @XmlJavaTypeAdapter(type=boolean.class, value=BooleanAdapter.class)
})
package org.geotoolkit.feature.catalog;

import org.geotoolkit.resources.jaxb.feature.catalog.MultiplicityRangeAdapter;
import org.geotoolkit.resources.jaxb.feature.catalog.MultiplicityAdapter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.geotoolkit.internal.jaxb.uom.*;
import org.geotoolkit.internal.jaxb.text.*;
import org.geotoolkit.internal.jaxb.metadata.*;
import org.geotoolkit.resources.jaxb.feature.catalog.*;
import org.geotoolkit.resources.jaxb.feature.catalog.code.*;
import org.geotoolkit.internal.jaxb.primitive.*;
import org.geotoolkit.resources.jaxb.feature.catalog.MultiplicityAdapter;
import org.geotoolkit.resources.jaxb.feature.catalog.MultiplicityRangeAdapter;

