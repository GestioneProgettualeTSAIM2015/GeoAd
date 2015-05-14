SELECT "Locations"."Id", "Categories"."Name" as "PCat", "SecondCatQuery"."Name" as "SCat", "Locations"."Name"
FROM "Locations"
INNER JOIN "Categories" ON
	"Locations"."PCatId" = "Categories"."Id"
LEFT JOIN
(
	SELECT "Locations"."Id", "Categories"."Name"
	FROM "Locations" INNER JOIN "Categories" ON
		"Locations"."SCatId" = "Categories"."Id"
) as "SecondCatQuery" ON "Locations"."Id" = "SecondCatQuery"."Id"
ORDER BY "Locations"."Id"