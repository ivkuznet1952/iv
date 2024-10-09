--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4 (Homebrew)
-- Dumped by pg_dump version 16.4 (Homebrew)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: customer; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.customer (
    id bigint NOT NULL,
    comment character varying(500),
    email character varying(255),
    hashedpassword character varying(255),
    firstname character varying(255) NOT NULL,
    lastname character varying(255) NOT NULL,
    phone character varying(255),
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE public.customer OWNER TO iv;

--
-- Name: customer_id_seq; Type: SEQUENCE; Schema: public; Owner: iv
--

CREATE SEQUENCE public.customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.customer_id_seq OWNER TO iv;

--
-- Name: customer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: iv
--

ALTER SEQUENCE public.customer_id_seq OWNED BY public.customer.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO iv;

--
-- Name: guide; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.guide (
    id bigint NOT NULL,
    active boolean NOT NULL,
    comment character varying(500),
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    phone character varying(255)
);


ALTER TABLE public.guide OWNER TO iv;

--
-- Name: guide_id_seq; Type: SEQUENCE; Schema: public; Owner: iv
--

CREATE SEQUENCE public.guide_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.guide_id_seq OWNER TO iv;

--
-- Name: guide_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: iv
--

ALTER SEQUENCE public.guide_id_seq OWNED BY public.guide.id;


--
-- Name: log; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.log (
    id bigint NOT NULL,
    created timestamp without time zone,
    user_id integer NOT NULL,
    action character varying(100) NOT NULL,
    comment character varying(100) NOT NULL
);


ALTER TABLE public.log OWNER TO iv;

--
-- Name: log_id_seq; Type: SEQUENCE; Schema: public; Owner: iv
--

CREATE SEQUENCE public.log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.log_id_seq OWNER TO iv;

--
-- Name: log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: iv
--

ALTER SEQUENCE public.log_id_seq OWNED BY public.log.id;


--
-- Name: ordernumber; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.ordernumber (
    id bigint NOT NULL,
    num integer NOT NULL,
    org_id integer NOT NULL
);


ALTER TABLE public.ordernumber OWNER TO iv;

--
-- Name: ordernumber_id_seq; Type: SEQUENCE; Schema: public; Owner: iv
--

CREATE SEQUENCE public.ordernumber_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ordernumber_id_seq OWNER TO iv;

--
-- Name: ordernumber_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: iv
--

ALTER SEQUENCE public.ordernumber_id_seq OWNED BY public.ordernumber.id;


--
-- Name: transport; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.transport (
    id bigint NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE public.transport OWNER TO iv;

--
-- Name: trip; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.trip (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(500) NOT NULL,
    photo character varying(100) NOT NULL,
    comment character varying(100) NOT NULL,
    duration integer,
    active boolean
);


ALTER TABLE public.trip OWNER TO iv;

--
-- Name: transport_id_seq; Type: SEQUENCE; Schema: public; Owner: iv
--

CREATE SEQUENCE public.transport_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.transport_id_seq OWNER TO iv;

--
-- Name: transport_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: iv
--

ALTER SEQUENCE public.transport_id_seq OWNED BY public.trip.id;


--
-- Name: transport_id_seq1; Type: SEQUENCE; Schema: public; Owner: iv
--

CREATE SEQUENCE public.transport_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.transport_id_seq1 OWNER TO iv;

--
-- Name: transport_id_seq1; Type: SEQUENCE OWNED BY; Schema: public; Owner: iv
--

ALTER SEQUENCE public.transport_id_seq1 OWNED BY public.transport.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: iv
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(100) NOT NULL,
    hashedpassword character varying(200) NOT NULL,
    roles character varying(400) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    active boolean
);


ALTER TABLE public.users OWNER TO iv;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: iv
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO iv;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: iv
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: customer id; Type: DEFAULT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.customer ALTER COLUMN id SET DEFAULT nextval('public.customer_id_seq'::regclass);


--
-- Name: guide id; Type: DEFAULT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.guide ALTER COLUMN id SET DEFAULT nextval('public.guide_id_seq'::regclass);


--
-- Name: log id; Type: DEFAULT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.log ALTER COLUMN id SET DEFAULT nextval('public.log_id_seq'::regclass);


--
-- Name: ordernumber id; Type: DEFAULT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.ordernumber ALTER COLUMN id SET DEFAULT nextval('public.ordernumber_id_seq'::regclass);


--
-- Name: transport id; Type: DEFAULT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.transport ALTER COLUMN id SET DEFAULT nextval('public.transport_id_seq1'::regclass);


--
-- Name: trip id; Type: DEFAULT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.trip ALTER COLUMN id SET DEFAULT nextval('public.transport_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: customer; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.customer (id, comment, email, hashedpassword, firstname, lastname, phone, created, updated) FROM stdin;
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
2	02	CreateTrip	SQL	V02__CreateTrip.sql	480179086	iv	2024-09-11 13:03:12.918544	32	t
1	01	CreateUser	SQL	V01__CreateUser.sql	-1269080669	iv	2024-09-09 15:49:09.690176	17	t
3	03	CreateTransport	SQL	V03__CreateTransport.sql	529964882	iv	2024-09-25 13:17:30.392015	19	t
4	04	CreateGuide	SQL	V04__CreateGuide.sql	768208537	iv	2024-09-25 13:17:30.429537	5	t
5	05	CreateCustomer	SQL	V05__CreateCustomer.sql	2086964786	iv	2024-09-25 13:17:30.440244	4	t
6	06	CreateLog	SQL	V06__CreateLog.sql	-2120879066	iv	2024-09-25 13:17:30.45008	3	t
7	07	CreateOrderNumber	SQL	V07__CreateOrderNumber.sql	-420399271	iv	2024-09-25 13:17:30.460024	2	t
\.


--
-- Data for Name: guide; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.guide (id, active, comment, email, firstname, lastname, phone) FROM stdin;
\.


--
-- Data for Name: log; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.log (id, created, user_id, action, comment) FROM stdin;
\.


--
-- Data for Name: ordernumber; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.ordernumber (id, num, org_id) FROM stdin;
\.


--
-- Data for Name: transport; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.transport (id, name) FROM stdin;
\.


--
-- Data for Name: trip; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.trip (id, name, description, photo, comment, duration, active) FROM stdin;
1	Я ехала домой	описалово	1.jpeg	comment	2	t
2	Петергоф, Ораниенбаум и прочие привлекательные места	Очень приятнаое путешествие с обоятельным гидом с посещением прекрасных фонтанов и большого петергофского дворца.	kot1.jpg	Экскурсия до 3-х человек на комфортабельном автомобиле или до 7 человек на микроатобусе.  	3	t
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: iv
--

COPY public.users (id, username, hashedpassword, role, created, updated, active) FROM stdin;
1	admin	185000:6e444c019e255b6d:91a807d5f5dae370f0ddf8dc82dbf47f8c2a06e6d8c9688556271865453766fb	ROLE_ADMIN	2024-09-09 15:49:09.892336	2024-09-09 15:49:09.892418	t
2	user (pwd admin)	185000:6e444c019e255b6d:91a807d5f5dae370f0ddf8dc82dbf47f8c2a06e6d8c9688556271865453766fb	ROLE_USER	2024-09-09 15:49:09.892336	2024-09-09 15:49:09.892418	t
\.


--
-- Name: customer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: iv
--

SELECT pg_catalog.setval('public.customer_id_seq', 1, false);


--
-- Name: guide_id_seq; Type: SEQUENCE SET; Schema: public; Owner: iv
--

SELECT pg_catalog.setval('public.guide_id_seq', 1, false);


--
-- Name: log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: iv
--

SELECT pg_catalog.setval('public.log_id_seq', 1, false);


--
-- Name: ordernumber_id_seq; Type: SEQUENCE SET; Schema: public; Owner: iv
--

SELECT pg_catalog.setval('public.ordernumber_id_seq', 1, false);


--
-- Name: transport_id_seq; Type: SEQUENCE SET; Schema: public; Owner: iv
--

SELECT pg_catalog.setval('public.transport_id_seq', 2, true);


--
-- Name: transport_id_seq1; Type: SEQUENCE SET; Schema: public; Owner: iv
--

SELECT pg_catalog.setval('public.transport_id_seq1', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: iv
--

SELECT pg_catalog.setval('public.users_id_seq', 2, true);


--
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: guide guide_pkey; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.guide
    ADD CONSTRAINT guide_pkey PRIMARY KEY (id);


--
-- Name: log log_pkey; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: ordernumber ordernumber_pkey; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.ordernumber
    ADD CONSTRAINT ordernumber_pkey PRIMARY KEY (id);


--
-- Name: trip transport_pkey; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.trip
    ADD CONSTRAINT transport_pkey PRIMARY KEY (id);


--
-- Name: transport transport_pkey1; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.transport
    ADD CONSTRAINT transport_pkey1 PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: iv
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: iv
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: users_username_idx; Type: INDEX; Schema: public; Owner: iv
--

CREATE UNIQUE INDEX users_username_idx ON public.users USING btree (username);


--
-- PostgreSQL database dump complete
--

