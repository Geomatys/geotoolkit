The classes defined in this directory should move to the org.geotoolkit.internal.jaxb.text package,
with a namespace=Namespaces.GMD attribute added to all XML annotations. Unfortunately, as of JAXB 2.1
this cause the apparition of a spurious "gml:" prefix in attribute, for example:

<gmd:LocalisedCharacterString gmd:locale="#locale-eng">Geotoolkit.org, OpenSource Project</gmd:LocalisedCharacterString>

The "gmd:" before "locale" is not necessary.

Revisit with JAXB 2.2 if this problem is still present. If resolved, move the classes to the
text package and the package-info.java file to the geotk-metadata module.
