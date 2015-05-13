--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.1
-- Dumped by pg_dump version 9.4.1
-- Started on 2015-05-13 14:38:39

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 2060 (class 1262 OID 16394)
-- Name: GeoAdDb; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "GeoAdDb" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Italian_Italy.1252' LC_CTYPE = 'Italian_Italy.1252';


ALTER DATABASE "GeoAdDb" OWNER TO postgres;

\connect "GeoAdDb"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 182 (class 3079 OID 11855)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2063 (class 0 OID 0)
-- Dependencies: 182
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 175 (class 1259 OID 24605)
-- Name: Categories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "Categories" (
    "Id" integer NOT NULL,
    "Name" text NOT NULL
);


ALTER TABLE "Categories" OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 24603)
-- Name: Categories_Id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Categories_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Categories_Id_seq" OWNER TO postgres;

--
-- TOC entry 2064 (class 0 OID 0)
-- Dependencies: 174
-- Name: Categories_Id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Categories_Id_seq" OWNED BY "Categories"."Id";


--
-- TOC entry 181 (class 1259 OID 24682)
-- Name: LocationTypes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "LocationTypes" (
    "Id" integer NOT NULL,
    "Name" text
);


ALTER TABLE "LocationTypes" OWNER TO postgres;

--
-- TOC entry 180 (class 1259 OID 24680)
-- Name: LocationTypes_Id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "LocationTypes_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "LocationTypes_Id_seq" OWNER TO postgres;

--
-- TOC entry 2065 (class 0 OID 0)
-- Dependencies: 180
-- Name: LocationTypes_Id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "LocationTypes_Id_seq" OWNED BY "LocationTypes"."Id";


--
-- TOC entry 173 (class 1259 OID 24578)
-- Name: Locations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "Locations" (
    "Id" integer NOT NULL,
    "UserId" integer NOT NULL,
    "PCatId" integer NOT NULL,
    "SCatId" integer,
    "Name" text NOT NULL,
    "Lat" text NOT NULL,
    "Lng" text NOT NULL,
    "Desc" text,
    "TypeId" integer
);


ALTER TABLE "Locations" OWNER TO postgres;

--
-- TOC entry 172 (class 1259 OID 24576)
-- Name: Locations_Id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Locations_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Locations_Id_seq" OWNER TO postgres;

--
-- TOC entry 2066 (class 0 OID 0)
-- Dependencies: 172
-- Name: Locations_Id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Locations_Id_seq" OWNED BY "Locations"."Id";


--
-- TOC entry 179 (class 1259 OID 24628)
-- Name: Offerings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "Offerings" (
    "Id" integer NOT NULL,
    "LocationId" integer,
    "Desc" text,
    "ExpDate" date,
    "InsDate" date
);


ALTER TABLE "Offerings" OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 24626)
-- Name: Offerings_Id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Offerings_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Offerings_Id_seq" OWNER TO postgres;

--
-- TOC entry 2067 (class 0 OID 0)
-- Dependencies: 178
-- Name: Offerings_Id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Offerings_Id_seq" OWNED BY "Offerings"."Id";


--
-- TOC entry 177 (class 1259 OID 24616)
-- Name: Photos; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "Photos" (
    "Id" integer NOT NULL,
    "LocationId" integer,
    "Photo" bytea,
    "Width" integer,
    "Height" integer
);


ALTER TABLE "Photos" OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 24614)
-- Name: Photos_Id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Photos_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Photos_Id_seq" OWNER TO postgres;

--
-- TOC entry 2068 (class 0 OID 0)
-- Dependencies: 176
-- Name: Photos_Id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Photos_Id_seq" OWNED BY "Photos"."Id";


--
-- TOC entry 1911 (class 2604 OID 24608)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Categories" ALTER COLUMN "Id" SET DEFAULT nextval('"Categories_Id_seq"'::regclass);


--
-- TOC entry 1914 (class 2604 OID 24685)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "LocationTypes" ALTER COLUMN "Id" SET DEFAULT nextval('"LocationTypes_Id_seq"'::regclass);


--
-- TOC entry 1910 (class 2604 OID 24581)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Locations" ALTER COLUMN "Id" SET DEFAULT nextval('"Locations_Id_seq"'::regclass);


--
-- TOC entry 1913 (class 2604 OID 24631)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Offerings" ALTER COLUMN "Id" SET DEFAULT nextval('"Offerings_Id_seq"'::regclass);


--
-- TOC entry 1912 (class 2604 OID 24619)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Photos" ALTER COLUMN "Id" SET DEFAULT nextval('"Photos_Id_seq"'::regclass);


--
-- TOC entry 2049 (class 0 OID 24605)
-- Dependencies: 175
-- Data for Name: Categories; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2069 (class 0 OID 0)
-- Dependencies: 174
-- Name: Categories_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Categories_Id_seq"', 1, false);


--
-- TOC entry 2055 (class 0 OID 24682)
-- Dependencies: 181
-- Data for Name: LocationTypes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "LocationTypes" ("Id", "Name") VALUES (1, 'CA');
INSERT INTO "LocationTypes" ("Id", "Name") VALUES (2, 'POI');


--
-- TOC entry 2070 (class 0 OID 0)
-- Dependencies: 180
-- Name: LocationTypes_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"LocationTypes_Id_seq"', 2, true);


--
-- TOC entry 2047 (class 0 OID 24578)
-- Dependencies: 173
-- Data for Name: Locations; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2071 (class 0 OID 0)
-- Dependencies: 172
-- Name: Locations_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Locations_Id_seq"', 1, false);


--
-- TOC entry 2053 (class 0 OID 24628)
-- Dependencies: 179
-- Data for Name: Offerings; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2072 (class 0 OID 0)
-- Dependencies: 178
-- Name: Offerings_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Offerings_Id_seq"', 1, false);


--
-- TOC entry 2051 (class 0 OID 24616)
-- Dependencies: 177
-- Data for Name: Photos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2073 (class 0 OID 0)
-- Dependencies: 176
-- Name: Photos_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Photos_Id_seq"', 1, false);


--
-- TOC entry 1923 (class 2606 OID 24637)
-- Name: PKCategories; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Categories"
    ADD CONSTRAINT "PKCategories" PRIMARY KEY ("Id");


--
-- TOC entry 1931 (class 2606 OID 24690)
-- Name: PKLocationTypes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "LocationTypes"
    ADD CONSTRAINT "PKLocationTypes" PRIMARY KEY ("Id");


--
-- TOC entry 1916 (class 2606 OID 24643)
-- Name: PKLocations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "PKLocations" PRIMARY KEY ("Id");


--
-- TOC entry 1928 (class 2606 OID 24641)
-- Name: PKOfferings; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Offerings"
    ADD CONSTRAINT "PKOfferings" PRIMARY KEY ("Id");


--
-- TOC entry 1925 (class 2606 OID 24639)
-- Name: PKPhotos; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Photos"
    ADD CONSTRAINT "PKPhotos" PRIMARY KEY ("Id");


--
-- TOC entry 1918 (class 2606 OID 24645)
-- Name: UniqName; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "UniqName" UNIQUE ("Name");


--
-- TOC entry 1919 (class 1259 OID 24651)
-- Name: fki_FKCategoryId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKCategoryId" ON "Locations" USING btree ("PCatId");


--
-- TOC entry 1929 (class 1259 OID 24668)
-- Name: fki_FKLocationId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKLocationId" ON "Offerings" USING btree ("LocationId");


--
-- TOC entry 1926 (class 1259 OID 24679)
-- Name: fki_FKPhotosLocationId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKPhotosLocationId" ON "Photos" USING btree ("LocationId");


--
-- TOC entry 1920 (class 1259 OID 24662)
-- Name: fki_FKSCatId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKSCatId" ON "Locations" USING btree ("SCatId");


--
-- TOC entry 1921 (class 1259 OID 24696)
-- Name: fki_FKTypeId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKTypeId" ON "Locations" USING btree ("TypeId");


--
-- TOC entry 1936 (class 2606 OID 24663)
-- Name: FKLocationId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Offerings"
    ADD CONSTRAINT "FKLocationId" FOREIGN KEY ("LocationId") REFERENCES "Locations"("Id");


--
-- TOC entry 1932 (class 2606 OID 24652)
-- Name: FKPCatId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "FKPCatId" FOREIGN KEY ("PCatId") REFERENCES "Categories"("Id");


--
-- TOC entry 1935 (class 2606 OID 24674)
-- Name: FKPhotosLocationId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Photos"
    ADD CONSTRAINT "FKPhotosLocationId" FOREIGN KEY ("LocationId") REFERENCES "Locations"("Id");


--
-- TOC entry 1933 (class 2606 OID 24657)
-- Name: FKSCatId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "FKSCatId" FOREIGN KEY ("SCatId") REFERENCES "Categories"("Id");


--
-- TOC entry 1934 (class 2606 OID 24691)
-- Name: FKTypeId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "FKTypeId" FOREIGN KEY ("TypeId") REFERENCES "LocationTypes"("Id");


--
-- TOC entry 2062 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2015-05-13 14:38:39

--
-- PostgreSQL database dump complete
--

