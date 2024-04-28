--
-- PostgreSQL database dump
--

-- Dumped from database version 14.11 (Debian 14.11-1.pgdg110+2)
-- Dumped by pg_dump version 16.0

-- Started on 2024-04-28 04:06:53 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE IF EXISTS "mesh-network-db";
--
-- TOC entry 4670 (class 1262 OID 16384)
-- Name: mesh-network-db; Type: DATABASE; Schema: -; Owner: admin_user
--

CREATE DATABASE "mesh-network-db" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


ALTER DATABASE "mesh-network-db" OWNER TO admin_user;

\connect -reuse-previous=on "dbname='mesh-network-db'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4671 (class 0 OID 0)
-- Name: mesh-network-db; Type: DATABASE PROPERTIES; Schema: -; Owner: admin_user
--

ALTER DATABASE "mesh-network-db" SET search_path TO '$user', 'public', 'topology', 'tiger';


\connect -reuse-previous=on "dbname='mesh-network-db'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 8 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: admin_user
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO admin_user;

--
-- TOC entry 11 (class 2615 OID 19292)
-- Name: tiger; Type: SCHEMA; Schema: -; Owner: admin_user
--

CREATE SCHEMA tiger;


ALTER SCHEMA tiger OWNER TO admin_user;

--
-- TOC entry 12 (class 2615 OID 19548)
-- Name: tiger_data; Type: SCHEMA; Schema: -; Owner: admin_user
--

CREATE SCHEMA tiger_data;


ALTER SCHEMA tiger_data OWNER TO admin_user;

--
-- TOC entry 10 (class 2615 OID 19114)
-- Name: topology; Type: SCHEMA; Schema: -; Owner: admin_user
--

CREATE SCHEMA topology;


ALTER SCHEMA topology OWNER TO admin_user;

--
-- TOC entry 4673 (class 0 OID 0)
-- Dependencies: 10
-- Name: SCHEMA topology; Type: COMMENT; Schema: -; Owner: admin_user
--

COMMENT ON SCHEMA topology IS 'PostGIS Topology schema';


--
-- TOC entry 4 (class 3079 OID 19281)
-- Name: fuzzystrmatch; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA public;


--
-- TOC entry 4674 (class 0 OID 0)
-- Dependencies: 4
-- Name: EXTENSION fuzzystrmatch; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION fuzzystrmatch IS 'determine similarities and distance between strings';


--
-- TOC entry 2 (class 3079 OID 18039)
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- TOC entry 4675 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';


--
-- TOC entry 5 (class 3079 OID 19293)
-- Name: postgis_tiger_geocoder; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder WITH SCHEMA tiger;


--
-- TOC entry 4676 (class 0 OID 0)
-- Dependencies: 5
-- Name: EXTENSION postgis_tiger_geocoder; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis_tiger_geocoder IS 'PostGIS tiger geocoder and reverse geocoder';


--
-- TOC entry 3 (class 3079 OID 19115)
-- Name: postgis_topology; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;


--
-- TOC entry 4677 (class 0 OID 0)
-- Dependencies: 3
-- Name: EXTENSION postgis_topology; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis_topology IS 'PostGIS topology spatial types and functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 280 (class 1259 OID 53019)
-- Name: connections; Type: TABLE; Schema: public; Owner: admin_user
--

CREATE TABLE public.connections
(
    distance    double precision,
    destination bigint,
    id          bigint NOT NULL,
    source      bigint
);


ALTER TABLE public.connections
    OWNER TO admin_user;

--
-- TOC entry 278 (class 1259 OID 53017)
-- Name: connections_seq; Type: SEQUENCE; Schema: public; Owner: admin_user
--

CREATE SEQUENCE public.connections_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.connections_seq OWNER TO admin_user;

--
-- TOC entry 281 (class 1259 OID 53024)
-- Name: nodes; Type: TABLE; Schema: public; Owner: admin_user
--

CREATE TABLE public.nodes
(
    id          bigint                 NOT NULL,
    name        character varying(255) NOT NULL,
    coordinates public.geometry
);


ALTER TABLE public.nodes
    OWNER TO admin_user;

--
-- TOC entry 279 (class 1259 OID 53018)
-- Name: nodes_seq; Type: SEQUENCE; Schema: public; Owner: admin_user
--

CREATE SEQUENCE public.nodes_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.nodes_seq OWNER TO admin_user;

--
-- TOC entry 4663 (class 0 OID 53019)
-- Dependencies: 280
-- Data for Name: connections; Type: TABLE DATA; Schema: public; Owner: admin_user
--

INSERT INTO public.connections (distance, destination, id, source)
VALUES (638.186638088271, 5, 1, 4)
ON CONFLICT DO NOTHING;
INSERT INTO public.connections (distance, destination, id, source)
VALUES (1151.8656370998922, 8, 2, 2)
ON CONFLICT DO NOTHING;
INSERT INTO public.connections (distance, destination, id, source)
VALUES (13218.759469958999, 18, 3, 15)
ON CONFLICT DO NOTHING;
INSERT INTO public.connections (distance, destination, id, source)
VALUES (3383.1131724437696, 19, 5, 15)
ON CONFLICT DO NOTHING;
INSERT INTO public.connections (distance, destination, id, source)
VALUES (10453.095928604396, 8, 6, 7)
ON CONFLICT DO NOTHING;
INSERT INTO public.connections (distance, destination, id, source)
VALUES (7839.816990346728, 10, 7, 7)
ON CONFLICT DO NOTHING;
INSERT INTO public.connections (distance, destination, id, source)
VALUES (6850.717987028037, 10, 8, 8)
ON CONFLICT DO NOTHING;
INSERT INTO public.connections (distance, destination, id, source)
VALUES (18056.914903839446, 15, 52, 8)
ON CONFLICT DO NOTHING;


--
-- TOC entry 4664 (class 0 OID 53024)
-- Dependencies: 281
-- Data for Name: nodes; Type: TABLE DATA; Schema: public; Owner: admin_user
--

INSERT INTO public.nodes (id, name, coordinates)
VALUES (1, 'Bogotá', '0101000020E6100000AA8251499D8452C0D5E76A2BF6D71240')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (2, 'New Delhi', '0101000020E6100000D0D6C1C1DE4D5340F775E09C11A53C40')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (4, 'London', '0101000020E6100000DA1B7C613255C0BFFE43FAEDEBC04940')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (5, 'Frankfurt', '0101000020E6100000053411363C5D21402E90A0F8310E4940')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (6, 'Amsterdam', '0101000020E61000002041F163CC9D13403B014D840D2F4A40')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (7, 'Sydney', '0101000020E6100000B1E1E995B2E66240E561A1D634EF40C0')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (8, 'Mumbai', '0101000020E6100000C0EC9E3C2C385240FA7E6ABC74133340')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (9, 'Jakarta', '0101000020E6100000D95F764F1EB65A40849ECDAACFD518C0')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (10, 'Tokyo', '0101000020E610000095D4096822766140C74B378941D84140')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (11, 'São Paulo', '0101000020E6100000454772F90F5147C0B0726891ED8C37C0')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (12, 'Buenos Aires', '0101000020E6100000A913D044D8304DC0304CA60A464D41C0')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (13, 'Cape Town', '0101000020E6100000CD3B4ED1916C324003098A1F63F640C0')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (14, 'Nairobi', '0101000020E61000008C4AEA0434694240EA95B20C71ACF4BF')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (15, 'Mexico City', '0101000020E6100000F1F44A5986C858C0E63FA4DFBE6E3340')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (16, 'Rio de Janeiro', '0101000020E61000003CBD5296219645C04703780B24E836C0')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (17, 'Lagos', '0101000020E6100000462575029A080B40F0164850FC181A40')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (18, 'Cairo', '0101000020E6100000CEAACFD5563C3F4041F163CC5D0B3E40')
ON CONFLICT DO NOTHING;
INSERT INTO public.nodes (id, name, coordinates)
VALUES (19, 'New York', '0101000020E6100000AAF1D24D628052C05E4BC8073D5B4440')
ON CONFLICT DO NOTHING;


--
-- TOC entry 4475 (class 0 OID 18356)
-- Dependencies: 217
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: admin_user
--


--
-- TOC entry 4479 (class 0 OID 19299)
-- Dependencies: 228
-- Data for Name: geocode_settings; Type: TABLE DATA; Schema: tiger; Owner: admin_user
--


--
-- TOC entry 4480 (class 0 OID 19631)
-- Dependencies: 273
-- Data for Name: pagc_gaz; Type: TABLE DATA; Schema: tiger; Owner: admin_user
--


--
-- TOC entry 4481 (class 0 OID 19641)
-- Dependencies: 275
-- Data for Name: pagc_lex; Type: TABLE DATA; Schema: tiger; Owner: admin_user
--


--
-- TOC entry 4482 (class 0 OID 19651)
-- Dependencies: 277
-- Data for Name: pagc_rules; Type: TABLE DATA; Schema: tiger; Owner: admin_user
--


--
-- TOC entry 4477 (class 0 OID 19117)
-- Dependencies: 222
-- Data for Name: topology; Type: TABLE DATA; Schema: topology; Owner: admin_user
--


--
-- TOC entry 4478 (class 0 OID 19129)
-- Dependencies: 223
-- Data for Name: layer; Type: TABLE DATA; Schema: topology; Owner: admin_user
--


--
-- TOC entry 4678 (class 0 OID 0)
-- Dependencies: 278
-- Name: connections_seq; Type: SEQUENCE SET; Schema: public; Owner: admin_user
--

SELECT pg_catalog.setval('public.connections_seq', 101, true);


--
-- TOC entry 4679 (class 0 OID 0)
-- Dependencies: 279
-- Name: nodes_seq; Type: SEQUENCE SET; Schema: public; Owner: admin_user
--

SELECT pg_catalog.setval('public.nodes_seq', 51, true);


--
-- TOC entry 4680 (class 0 OID 0)
-- Dependencies: 221
-- Name: topology_id_seq; Type: SEQUENCE SET; Schema: topology; Owner: admin_user
--

SELECT pg_catalog.setval('topology.topology_id_seq', 1, false);


--
-- TOC entry 4512 (class 2606 OID 53023)
-- Name: connections connections_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_user
--

ALTER TABLE ONLY public.connections
    ADD CONSTRAINT connections_pkey PRIMARY KEY (id);


--
-- TOC entry 4514 (class 2606 OID 53030)
-- Name: nodes nodes_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_user
--

ALTER TABLE ONLY public.nodes
    ADD CONSTRAINT nodes_pkey PRIMARY KEY (id);


--
-- TOC entry 4515 (class 2606 OID 53036)
-- Name: connections fk9g5sapm47v5bi9xuymf277d4t; Type: FK CONSTRAINT; Schema: public; Owner: admin_user
--

ALTER TABLE ONLY public.connections
    ADD CONSTRAINT fk9g5sapm47v5bi9xuymf277d4t FOREIGN KEY (source) REFERENCES public.nodes (id);


--
-- TOC entry 4516 (class 2606 OID 53031)
-- Name: connections fknmdehyy2b6oaqs0chi6q0c9gp; Type: FK CONSTRAINT; Schema: public; Owner: admin_user
--

ALTER TABLE ONLY public.connections
    ADD CONSTRAINT fknmdehyy2b6oaqs0chi6q0c9gp FOREIGN KEY (destination) REFERENCES public.nodes (id);


--
-- TOC entry 4672 (class 0 OID 0)
-- Dependencies: 8
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: admin_user
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2024-04-28 04:06:53 CEST

--
-- PostgreSQL database dump complete
--

