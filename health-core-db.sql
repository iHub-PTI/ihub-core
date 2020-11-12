--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1
-- Dumped by pg_dump version 12.0

-- Started on 2020-10-06 07:43:00

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'LATIN1';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2821 (class 1262 OID 25122)
-- Name: health_core_db; Type: DATABASE; Schema: -; Owner: postgres
--
--

CREATE TABLE public.speciality (
    id integer NOT NULL,
    description character varying(500)
);


ALTER TABLE public.speciality OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 25123)
-- Name: speciality_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.speciality_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.speciality_id_seq OWNER TO postgres;

--
-- TOC entry 2822 (class 0 OID 0)
-- Dependencies: 202
-- Name: speciality_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.speciality_id_seq OWNED BY public.speciality.id;


--
-- TOC entry 2687 (class 2604 OID 25128)
-- Name: speciality id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.speciality ALTER COLUMN id SET DEFAULT nextval('public.speciality_id_seq'::regclass);


--
-- TOC entry 2689 (class 2606 OID 25130)
-- Name: speciality speciality_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.speciality
    ADD CONSTRAINT speciality_pkey PRIMARY KEY (id);


-- Completed on 2020-10-06 07:43:00

--
-- PostgreSQL database dump complete
--

