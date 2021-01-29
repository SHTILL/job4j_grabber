CREATE TABLE public.post
(
    id integer NOT NULL,
    link text NOT NULL,
    name text NOT NULL,
    text text NOT NULL,
    created date NOT NULL,
    CONSTRAINT post_pkey PRIMARY KEY (id),
    CONSTRAINT link_unique UNIQUE (link)
);
