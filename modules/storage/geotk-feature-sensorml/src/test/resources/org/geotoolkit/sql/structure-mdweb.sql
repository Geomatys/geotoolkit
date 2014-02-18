CREATE SCHEMA "Profiles";
CREATE SCHEMA "Resources";
CREATE SCHEMA "Schemas";
CREATE SCHEMA "Search";
CREATE SCHEMA "Storage";
CREATE SCHEMA "Users";

CREATE TABLE "Profiles"."ControlParameters" (
    "orderControl" integer NOT NULL,
    "orderParameter" integer NOT NULL,
    "valueParameter" character varying(50),
    "profileName" character varying(50) NOT NULL,
    "path" character varying(150) NOT NULL
);


CREATE TABLE "Profiles"."FieldControls" (
    "orderControl" integer NOT NULL,
    "nameControl" character varying(30),
    "event" character varying(30),
    "profileName" character varying(50) NOT NULL,
    "path" character varying(150) NOT NULL
);


CREATE TABLE "Profiles"."FieldElements" (
    "profileName" character varying(50) NOT NULL,
    "fieldType" character varying(10),
    "horizontalSize" integer,
    "verticalSize" integer,
    "path" character varying(150) NOT NULL
);


CREATE TABLE "Profiles"."InputLevels" (
    "name" character varying(50) NOT NULL,
    "parent" character varying(50)
);


CREATE TABLE "Profiles"."ProfileElements" (
    "profileName" character varying(50) NOT NULL,
    "path" character varying(150) NOT NULL,
    "obligation" character varying(5),
    "maxOccurrence" integer,
    "defaultValue" character varying(100),
    "inputLevel" character varying(50) NOT NULL
);


CREATE TABLE "Profiles"."ProfileHierarchies" (
    "parent" character varying(50) NOT NULL,
    "child" character varying(50) NOT NULL
);

CREATE TABLE "Profiles"."Profiles" (
    "profileName" character varying(50) NOT NULL,
    "profileDate" date
);


CREATE TABLE "Resources"."AttachedFiles" (
    "fileName" character varying(100) NOT NULL,
    "form" integer NOT NULL,
    "typeMIME" character varying(50),
    "creationDate" date,
    "pathToOnlineRessource" character varying(150),
    "fileSize" integer
);

CREATE TABLE "Resources"."Dependencies" (
    "dependant" character varying(100),
    "dependency" character varying(100),
    "form" integer
);

CREATE TABLE "Resources"."ImageFiles" (
    "fileName" character varying(100) NOT NULL,
    "form" integer NOT NULL,
    "typeMIME" character varying(50),
    "creationDate" date,
    "pathToOnlineRessource" character varying(150),
    "fileSize" integer,
    "pathToBrowseGraphic" character varying(150)
);



CREATE TABLE "Schemas"."Elements" (
    "name" character varying(50) NOT NULL,
    "shortname" character varying(25),
    "standard" character varying(50) NOT NULL,
    "definition" character varying(200)
);

CREATE TABLE "Schemas"."Classes" (
    "name" character varying(50) NOT NULL,
    "shortname" character varying(25),
    "standard" character varying(50) NOT NULL,
    "definition" character varying(200),
    "abstract" smallint,
    "superClasse" character varying(50),
    "standard_superClasse" character varying(50),
    "childPolicy" character(1)
);


CREATE TABLE "Schemas"."Properties" (
    "name" character varying(50) NOT NULL,
    "shortname" character varying(25),
    "standard" character varying(50) NOT NULL,
    "definition" character varying(300),
    "minOccurrence" integer,
    "maxOccurrence" integer,
    "owner" character varying(50) NOT NULL,
    "type" character varying(50),
    "codelist" character varying(50),
    "obligation" character varying(5),
    "order" integer NOT NULL,
    "type_standard" character varying(50) NOT NULL,
    "owner_standard" character varying(50) NOT NULL,
    "asAttribute" character(1)
);


CREATE TABLE "Schemas"."CodeListElements" (
    "name" character varying(50) NOT NULL,
    "shortname" character varying(25),
    "standard" character varying(50) NOT NULL,
    "definition" character varying(100),
    "minOccurrence" integer,
    "maxOccurrence" integer,
    "owner" character varying(50) NOT NULL,
    "type" character varying(50),
    "codelist" character varying(50),
    "obligation" character varying(5),
    "order" integer NOT NULL,
    "type_standard" character varying(50) NOT NULL,
    "owner_standard" character varying(50) NOT NULL,
    "asAttribute" character(1),
    "code" integer NOT NULL
);


CREATE TABLE "Schemas"."CodeLists" (
    "name" character varying(50) NOT NULL,
    "shortname" character varying(25),
    "standard" character varying(50) NOT NULL,
    "definition" character varying(100),
    "abstract" smallint,
    "superClasse" character varying(50),
    "standard_superClasse" character varying(50),
    "childPolicy" character(1)
);


CREATE TABLE "Schemas"."Locales" (
    "name" character varying(50) NOT NULL,
    "shortname" character varying(25),
    "standard" character varying(50) NOT NULL,
    "definition" character varying(100),
    "minOccurrence" integer,
    "maxOccurrence" integer,
    "owner" character varying(50) NOT NULL,
    "type" character varying(50),
    "codelist" character varying(50),
    "obligation" character varying(5),
    "order" integer NOT NULL,
    "type_standard" character varying(50) NOT NULL,
    "owner_standard" character varying(50) NOT NULL,
    "asAttribute" character(1),
    "code" integer NOT NULL
);



CREATE TABLE "Schemas"."Obligations" (
    "code" character varying(5) NOT NULL,
    "name" character varying(20)
);



CREATE TABLE "Schemas"."Paths" (
    "id" character varying(200) NOT NULL,
    "name" character varying(50) NOT NULL,
    "standard" character varying(50) NOT NULL,
    "owner" character varying(50) NOT NULL,
    "parent" character varying(200),
    "owner_Standard" character varying(50) NOT NULL
);


CREATE TABLE "Schemas"."Standard" (
    "name" character varying(50) NOT NULL,
    "namespace" character varying(50)
);



CREATE TABLE "Storage"."Catalogs" (
    "code" character varying(6) NOT NULL,
    "name" character varying(50)
);


CREATE TABLE "Storage"."Values" (
    "form" integer NOT NULL,
    "path" character varying(200) NOT NULL,
    "ordinal" integer NOT NULL,
    "type" character varying(50),
    "typeStandard" character varying(50),
    "id_value" character varying(200) NOT NULL
);


CREATE TABLE "Storage"."DateValues" (
    "form" integer NOT NULL,
    "path" character varying(150) NOT NULL,
    "ordinal" integer NOT NULL,
    "type" character varying(50),
    "typeStandard" character varying(50),
    "id_value" character varying(200) NOT NULL,
    "value" date
);


CREATE TABLE "Storage"."FormHierarchies" (
    "parent" integer,
    "child" integer
);


CREATE TABLE "Storage"."Forms" (
    "identifier" integer NOT NULL,
    "catalog" character varying(6),
    "title" character varying(200),
    "inputLogin" character varying(20),
    "validationLogin" character varying(20),
    "profile" character varying(50),
    "updateDate" date,
    "isValidated" smallint,
    "isPublished" smallint,
    "type" character varying(20)
);



CREATE TABLE "Storage"."ImportForms" (
    "form" integer NOT NULL,
    "sourceImport" character varying(25),
    "xmlFile" character varying(100),
    "dateImport" date
);


CREATE TABLE "Storage"."InputLevelCompletions" (
    "form" integer NOT NULL,
    "inputLevel" character varying(50) NOT NULL,
    "isCompleted" smallint,
    "date" date
);


CREATE TABLE "Storage"."LinkedValues" (
    "form" integer NOT NULL,
    "path" character varying(150) NOT NULL,
    "ordinal" integer NOT NULL,
    "type" character varying(50),
    "typeStandard" character varying(50),
    "id_value" character varying(200) NOT NULL,
    "linkedForm" integer,
    "linkedPath" character varying(200)
);


CREATE TABLE "Storage"."PredefinedValues" (
    "linkedForm" integer,
    "linkedPath" character varying(200)
);

CREATE TABLE "Storage"."TextValues" (
    "form" integer NOT NULL,
    "path" character varying(200) NOT NULL,
    "ordinal" integer NOT NULL,
    "type" character varying(50),
    "typeStandard" character varying(50),
    "id_value" character varying(200) NOT NULL,
    "value" character varying(500)
);


CREATE TABLE "mapIds" (
    "identifier" integer,
    "oldId" integer
);

CREATE TABLE "Users"."Assignements" (
    "login" character varying(20) NOT NULL,
    "group" character varying(30) NOT NULL
);


CREATE TABLE "Users"."Roles" (
    "name" character varying(30) NOT NULL
);


CREATE TABLE "Users"."UserGroups" (
    "name" character varying(30) NOT NULL,
    "role" character varying(30),
    "catalog" character varying(6) NOT NULL
);


CREATE TABLE "Users"."Users" (
    "login" character varying(20) NOT NULL,
    "password" character varying(40),
    "form" integer,
    "creationDate" date,
    "expirationDate" date
);


CREATE TABLE "Markers" (
    "idgeom" integer NOT NULL,
    "idform" integer NOT NULL,
    "toponym" character varying(50),
    "catalog" character varying(50),
    "w" real,
    "s" real,
    "e" real,
    "n" real,
    "id_metadata" integer NOT NULL
);

create table "version" (
"number"  character varying(10));

ALTER TABLE  "Markers" ADD CONSTRAINT "Markers_pkey" PRIMARY KEY ("idgeom", "idform", "id_metadata");

ALTER TABLE  "Profiles"."FieldElements"  ADD CONSTRAINT "FieldElements_pkey" PRIMARY KEY ("profileName", "path");

ALTER TABLE  "Profiles"."ControlParameters" ADD CONSTRAINT pk_controlparameters PRIMARY KEY ("orderControl", "orderParameter", "profileName", "path");

ALTER TABLE  "Profiles"."FieldControls" ADD CONSTRAINT pk_fieldcontrol PRIMARY KEY ("orderControl", "profileName", "path");

ALTER TABLE  "Profiles"."ProfileElements" ADD CONSTRAINT pk_profileelements PRIMARY KEY ("profileName", "path");

ALTER TABLE  "Profiles"."ProfileHierarchies" ADD CONSTRAINT pk_profilehierarchies PRIMARY KEY ("parent", "child");

ALTER TABLE  "Resources"."AttachedFiles" ADD CONSTRAINT pk_attachedfiles PRIMARY KEY ("fileName", "form");

ALTER TABLE  "Resources"."ImageFiles"  ADD CONSTRAINT pk_imagefiles PRIMARY KEY ("fileName", "form");

ALTER TABLE  "Schemas"."Obligations" ADD CONSTRAINT "Obligations_pkey" PRIMARY KEY ("code");

ALTER TABLE  "Schemas"."Paths" ADD CONSTRAINT "Paths_pkey" PRIMARY KEY ("id");

ALTER TABLE  "Schemas"."Standard" ADD CONSTRAINT "Standard_pkey" PRIMARY KEY ("name");

ALTER TABLE  "Schemas"."Classes" ADD CONSTRAINT classes_pkey PRIMARY KEY ("name", "standard");

ALTER TABLE  "Schemas"."CodeListElements"  ADD CONSTRAINT codelistelements_pkey PRIMARY KEY ("name", "standard", "owner", "code");

ALTER TABLE  "Schemas"."CodeLists" ADD CONSTRAINT codelists_pkey PRIMARY KEY ("name", "standard");

ALTER TABLE  "Schemas"."Locales" ADD CONSTRAINT pk_locales PRIMARY KEY ("name");

ALTER TABLE  "Schemas"."Properties" ADD CONSTRAINT pk_properties PRIMARY KEY ("name", "standard", "owner", "owner_standard");

ALTER TABLE  "Schemas"."Paths" ADD CONSTRAINT unique_paths UNIQUE ("name", "standard", "owner", "parent");

ALTER TABLE  "Storage"."Catalogs" ADD CONSTRAINT "Catalogs_pkey" PRIMARY KEY ("code");

ALTER TABLE  "Storage"."DateValues" ADD CONSTRAINT "DateValues_pkey" PRIMARY KEY ("form", "ordinal", "path");

ALTER TABLE  "Storage"."Forms" ADD CONSTRAINT "Forms_pkey" PRIMARY KEY ("identifier");

ALTER TABLE  "Storage"."ImportForms" ADD CONSTRAINT "ImportForms_pkey" PRIMARY KEY ("form");

ALTER TABLE  "Storage"."TextValues"  ADD CONSTRAINT "TextValues_pkey" PRIMARY KEY ("form", "id_value");

ALTER TABLE  "Storage"."InputLevelCompletions"  ADD CONSTRAINT pk_inputlevelcompletions PRIMARY KEY ("form", "inputLevel");

ALTER TABLE  "Storage"."LinkedValues" ADD CONSTRAINT pk_linkedvalues PRIMARY KEY ("form", "id_value");

ALTER TABLE  "Storage"."Values" ADD CONSTRAINT pk_values PRIMARY KEY ("form", "id_value");

ALTER TABLE  "Storage"."Forms" ADD CONSTRAINT unique_forms UNIQUE ("catalog", "title", "inputLogin");

ALTER TABLE  "Users"."Roles" ADD CONSTRAINT "Role_pkey" PRIMARY KEY ("name");

ALTER TABLE  "Users"."UserGroups" ADD CONSTRAINT "UserGroups_pkey" PRIMARY KEY ("name", "catalog");

ALTER TABLE  "Users"."Users" ADD CONSTRAINT "Users_pkey" PRIMARY KEY ("login");

ALTER TABLE  "Users"."Assignements" ADD CONSTRAINT pk_assignements PRIMARY KEY ("login", "group");

ALTER TABLE  "Resources"."AttachedFiles" ADD CONSTRAINT "AttachedFiles_fk" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Resources"."Dependencies" ADD CONSTRAINT "Dependencies_fk" FOREIGN KEY ("dependant", "form") REFERENCES "Resources"."AttachedFiles"("fileName", "form");

ALTER TABLE  "Resources"."Dependencies" ADD CONSTRAINT "Dependencies_fk1" FOREIGN KEY ("dependency", "form") REFERENCES "Resources"."AttachedFiles"("fileName", "form");

ALTER TABLE  "Resources"."ImageFiles" ADD CONSTRAINT "ImageFiles_fk" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Schemas"."Classes" ADD CONSTRAINT "Classes_fk1" FOREIGN KEY ("superClasse", "standard_superClasse") REFERENCES "Schemas"."Classes"("name", "standard");

ALTER TABLE  "Schemas"."Elements" ADD CONSTRAINT "Elements_fk" FOREIGN KEY ("standard") REFERENCES "Schemas"."Standard"("name");

ALTER TABLE  "Schemas"."Paths" ADD CONSTRAINT "Paths_fk" FOREIGN KEY ("standard") REFERENCES "Schemas"."Standard"("name");

ALTER TABLE  "Schemas"."Paths" ADD CONSTRAINT "Paths_fk1" FOREIGN KEY ("parent") REFERENCES "Schemas"."Paths"("id");

ALTER TABLE  "Schemas"."Paths"  ADD CONSTRAINT "Paths_fk2" FOREIGN KEY ("owner", "owner_Standard") REFERENCES "Schemas"."Classes"("name", "standard");

ALTER TABLE  "Schemas"."Properties"  ADD CONSTRAINT "Properties_fk1" FOREIGN KEY ("type", "type_standard") REFERENCES "Schemas"."Classes"("name", "standard");

ALTER TABLE  "Schemas"."Properties"  ADD CONSTRAINT "Properties_fk2" FOREIGN KEY ("codelist", "type_standard") REFERENCES "Schemas"."CodeLists"("name", "standard");

ALTER TABLE  "Schemas"."Properties" ADD CONSTRAINT "Properties_fk3" FOREIGN KEY ("obligation") REFERENCES "Schemas"."Obligations"("code");

ALTER TABLE  "Schemas"."Properties" ADD CONSTRAINT "Properties_fk4" FOREIGN KEY ("owner", "owner_standard") REFERENCES "Schemas"."Classes"( "name", "standard");

ALTER TABLE  "Storage"."DateValues"  ADD CONSTRAINT "DateValues_fk" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."FormHierarchies" ADD CONSTRAINT "FormHierarchies_fk" FOREIGN KEY ("parent") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."FormHierarchies"  ADD CONSTRAINT "FormHierarchies_fk1" FOREIGN KEY ("child") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."Forms" ADD CONSTRAINT "Forms_fk1" FOREIGN KEY ("catalog") REFERENCES "Storage"."Catalogs"("code");

ALTER TABLE  "Storage"."Forms" ADD CONSTRAINT "Forms_fk2" FOREIGN KEY ("inputLogin") REFERENCES "Users"."Users"("login");

ALTER TABLE  "Storage"."Forms"  ADD CONSTRAINT "Forms_fk3" FOREIGN KEY ("validationLogin") REFERENCES "Users"."Users"("login");

ALTER TABLE  "Storage"."ImportForms" ADD CONSTRAINT "ImportForms_fk" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."InputLevelCompletions" ADD CONSTRAINT "InputLevelCompletions_fk" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."LinkedValues" ADD CONSTRAINT "LinkedValues_fk" FOREIGN KEY ("linkedForm") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."LinkedValues" ADD CONSTRAINT "LinkedValues_fk1" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."PredefinedValues" ADD CONSTRAINT "PredefinedValues_fk" FOREIGN KEY ("linkedForm") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."TextValues" ADD CONSTRAINT "TextValues_fk1" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."TextValues" ADD CONSTRAINT "TextValues_fk2" FOREIGN KEY ("path") REFERENCES "Schemas"."Paths"("id");

--ALTER TABLE  "Storage"."TextValues" ADD CONSTRAINT "TextValues_fk3" FOREIGN KEY ("type", "typeStandard") REFERENCES "Schemas"."Classes"("name", "standard")

ALTER TABLE  "Storage"."Values" ADD CONSTRAINT "Values_fk1" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");

ALTER TABLE  "Storage"."Values" ADD CONSTRAINT "Values_fk2"  FOREIGN KEY ("path") REFERENCES "Schemas"."Paths"("id");

--ALTER TABLE  "Storage"."Values" ADD CONSTRAINT "Values_fk3" FOREIGN KEY ("type", "typeStandard") REFERENCES "Schemas"."Classes"("name", "standard")

ALTER TABLE  "Users"."UserGroups" ADD CONSTRAINT "UserGroups_fk" FOREIGN KEY ("role") REFERENCES "Users"."Roles"("name");

ALTER TABLE  "Users"."Users" ADD CONSTRAINT "Users_fk1" FOREIGN KEY ("form") REFERENCES "Storage"."Forms"("identifier");