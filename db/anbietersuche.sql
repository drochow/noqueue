/*
aquery  <--  the searched string
1.0   <-- the radius in km
56.23 <-- betrieb latitude/longitude
56.33 <-- anwenders location latitude/longitude
*/
SELECT *
 FROM
    "ANBIETER" as "btr",
     "ADRESSE" as "adr",
     "DIENSTLEISTUNG" as "dl",
     "DIENSTLEISTUNGSTYP" as "dlt"
 WHERE
      "btr"."ADRESSE_ID" = "adr"."ID"
    AND
      "dl"."BTR_ID" = "btr"."ID"
    AND
      "dl"."DLT_ID" = "dlt"."DLT_ID"
    AND (
        "dlt"."NAME" LIKE '%aquery%'
      OR
        "btr"."NAME" LIKE '%aquery%'
      OR
        "btr"."KONTAKTEMAIL" LIKE '%aquery%'
      OR
        "dl"."KOMMENTAR" LIKE '%aquery%'
    )
    AND
      sqrt(power(abs(56.23-56.33), 2) + power(abs(56.23-56.33), 2)) <= 1.0;


SELECT btr.*, adr.*, (sqrt(power(abs((adr."LONGITUDE"-52.5466067)*cos(13.3589464)), 2) + power(abs(adr."LATITUDE"-13.3589464), 2))*6371000) as "DISTANCE"
         FROM "ANBIETER" as btr, "ADRESSE" as adr
         JOIN LEFT "DIENSTLEISTUNG" as dl ON  "DIENSTLEISTUNGSTYP" as dlt
          WHERE btr."ADRESSE_ID" = adr."ID"
          AND dl."BTR_ID" = btr."ID"
          AND dl."DLT_ID" = dlt."DLT_ID"
          AND ( dlt."NAME" LIKE '%a%'
            OR btr."NAME" LIKE '%a%'
            OR btr."KONTAKTEMAIL" LIKE '%a%'
            OR dl."KOMMENTAR" LIKE '%a%'
          )
          AND "DISTANCE" <= 1000.0
          ORDER BY "DISTANCE" ASC
          );