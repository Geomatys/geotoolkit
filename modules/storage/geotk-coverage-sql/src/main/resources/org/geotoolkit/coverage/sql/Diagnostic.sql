--
-- Identifies entries that should be unique.
-- This query should return an empty result.
--
SELECT "format", "band", "name", "lower", "upper", "scale", "offset" FROM rasters."Categories"
  GROUP BY ("format", "band", "name", "lower", "upper", "scale", "offset") HAVING COUNT("format") > 1;

--
-- Lists prefixes of formats that are repeated many times
-- (not necessarily with the exact same sample dimensions).
--
SELECT REVERSE(SUBSTRING(REVERSE("name"), POSITION('-' IN REVERSE("name")))) AS "prefix", "driver", COUNT(*)
  FROM rasters."Formats" WHERE "name" SIMILAR TO '%-\d+' GROUP BY "prefix", "driver";

--
-- Lists duplicated Series
--
SELECT MIN("identifier") AS "id", "product", "dataset", "directory", "extension", "format", "comments"
  FROM rasters."Series" GROUP BY ("product", "dataset", "directory", "extension", "format", "comments")
  HAVING COUNT(*) > 1;
