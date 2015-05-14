--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.1
-- Dumped by pg_dump version 9.4.1
-- Started on 2015-05-14 14:59:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 2048 (class 1262 OID 16394)
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
-- TOC entry 180 (class 3079 OID 11855)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2051 (class 0 OID 0)
-- Dependencies: 180
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
-- TOC entry 2052 (class 0 OID 0)
-- Dependencies: 174
-- Name: Categories_Id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Categories_Id_seq" OWNED BY "Categories"."Id";


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
    "Type" text
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
-- TOC entry 2053 (class 0 OID 0)
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
-- TOC entry 2054 (class 0 OID 0)
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
    "Width" integer,
    "Height" integer,
    "Data" bytea
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
-- TOC entry 2055 (class 0 OID 0)
-- Dependencies: 176
-- Name: Photos_Id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Photos_Id_seq" OWNED BY "Photos"."Id";


--
-- TOC entry 1904 (class 2604 OID 24608)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Categories" ALTER COLUMN "Id" SET DEFAULT nextval('"Categories_Id_seq"'::regclass);


--
-- TOC entry 1903 (class 2604 OID 24581)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Locations" ALTER COLUMN "Id" SET DEFAULT nextval('"Locations_Id_seq"'::regclass);


--
-- TOC entry 1906 (class 2604 OID 24631)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Offerings" ALTER COLUMN "Id" SET DEFAULT nextval('"Offerings_Id_seq"'::regclass);


--
-- TOC entry 1905 (class 2604 OID 24619)
-- Name: Id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Photos" ALTER COLUMN "Id" SET DEFAULT nextval('"Photos_Id_seq"'::regclass);


--
-- TOC entry 2039 (class 0 OID 24605)
-- Dependencies: 175
-- Data for Name: Categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Categories" ("Id", "Name") VALUES (1, 'CA');
INSERT INTO "Categories" ("Id", "Name") VALUES (2, 'POI');


--
-- TOC entry 2056 (class 0 OID 0)
-- Dependencies: 174
-- Name: Categories_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Categories_Id_seq"', 18, true);


--
-- TOC entry 2037 (class 0 OID 24578)
-- Dependencies: 173
-- Data for Name: Locations; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2057 (class 0 OID 0)
-- Dependencies: 172
-- Name: Locations_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Locations_Id_seq"', 1, false);


--
-- TOC entry 2043 (class 0 OID 24628)
-- Dependencies: 179
-- Data for Name: Offerings; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2058 (class 0 OID 0)
-- Dependencies: 178
-- Name: Offerings_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Offerings_Id_seq"', 1, false);


--
-- TOC entry 2041 (class 0 OID 24616)
-- Dependencies: 177
-- Data for Name: Photos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2059 (class 0 OID 0)
-- Dependencies: 176
-- Name: Photos_Id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Photos_Id_seq"', 1, false);


--
-- TOC entry 1914 (class 2606 OID 24637)
-- Name: PKCategories; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Categories"
    ADD CONSTRAINT "PKCategories" PRIMARY KEY ("Id");


--
-- TOC entry 1908 (class 2606 OID 24643)
-- Name: PKLocations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "PKLocations" PRIMARY KEY ("Id");


--
-- TOC entry 1921 (class 2606 OID 24641)
-- Name: PKOfferings; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Offerings"
    ADD CONSTRAINT "PKOfferings" PRIMARY KEY ("Id");


--
-- TOC entry 1918 (class 2606 OID 24639)
-- Name: PKPhotos; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Photos"
    ADD CONSTRAINT "PKPhotos" PRIMARY KEY ("Id");


--
-- TOC entry 1916 (class 2606 OID 24735)
-- Name: UniqCatName; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Categories"
    ADD CONSTRAINT "UniqCatName" UNIQUE ("Name");


--
-- TOC entry 1910 (class 2606 OID 24645)
-- Name: UniqName; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "UniqName" UNIQUE ("Name");


--
-- TOC entry 1911 (class 1259 OID 24651)
-- Name: fki_FKCategoryId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKCategoryId" ON "Locations" USING btree ("PCatId");


--
-- TOC entry 1922 (class 1259 OID 24668)
-- Name: fki_FKLocationId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKLocationId" ON "Offerings" USING btree ("LocationId");


--
-- TOC entry 1919 (class 1259 OID 24679)
-- Name: fki_FKPhotosLocationId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKPhotosLocationId" ON "Photos" USING btree ("LocationId");


--
-- TOC entry 1912 (class 1259 OID 24662)
-- Name: fki_FKSCatId; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_FKSCatId" ON "Locations" USING btree ("SCatId");


--
-- TOC entry 1926 (class 2606 OID 24663)
-- Name: FKLocationId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Offerings"
    ADD CONSTRAINT "FKLocationId" FOREIGN KEY ("LocationId") REFERENCES "Locations"("Id");


--
-- TOC entry 1923 (class 2606 OID 24652)
-- Name: FKPCatId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "FKPCatId" FOREIGN KEY ("PCatId") REFERENCES "Categories"("Id");


--
-- TOC entry 1925 (class 2606 OID 24674)
-- Name: FKPhotosLocationId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Photos"
    ADD CONSTRAINT "FKPhotosLocationId" FOREIGN KEY ("LocationId") REFERENCES "Locations"("Id");


--
-- TOC entry 1924 (class 2606 OID 24657)
-- Name: FKSCatId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Locations"
    ADD CONSTRAINT "FKSCatId" FOREIGN KEY ("SCatId") REFERENCES "Categories"("Id");


--
-- TOC entry 2050 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2015-05-14 14:59:19

--
-- PostgreSQL database dump complete
--

