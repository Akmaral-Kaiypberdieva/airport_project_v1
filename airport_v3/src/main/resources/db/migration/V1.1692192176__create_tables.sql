CREATE TABLE IF NOT EXISTS public.user_positions(
                           id bigserial primary key,
                           position_title varchar not null
);

CREATE TABLE IF NOT EXISTS public.app_roles(
                           id bigserial primary key,
                           role_title varchar not null,
                           position_id bigint references public.user_positions(id)
);

CREATE TABLE IF NOT EXISTS public.users(
                          id bigserial primary key,
                          username varchar not null,
                          user_password varchar not null,
                          email varchar not null,
                          registered_at timestamp not null,
                          is_enabled boolean not null default true,
                          position_id bigint,
                          foreign key (position_id) references public.user_positions(id)
);

CREATE TABLE IF NOT EXISTS public.user_role(
                           user_id bigint references users(id),
                           role_id bigint references app_roles(id)
);

CREATE TABLE IF NOT EXISTS public.aircraft(
                           id bigserial primary key,
                           aircraft_title varchar not null,
                           aircraft_status varchar not null default 'REGISTERED',
                           registered_at timestamp not null,
                           serviced_by bigint references public.users(id)
);

CREATE TABLE IF NOT EXISTS public.seats_in_aircraft(
                           id bigserial primary key,
                           number_in_row integer not null,
                           row_number integer not null,
                           is_reserved boolean not null default false,
                           aircraft_id bigint references public.aircraft(id)
);

CREATE TABLE IF NOT EXISTS public.parts_of_aircraft(
                           id bigserial primary key,
                           part_title varchar not null,
                           part_type varchar not null,
                           registered_at timestamp not null
);

CREATE TABLE IF NOT EXISTS public.inspections(
                           id bigserial primary key,
                           part_state varchar not null,
                           registered_at timestamp not null,
                           inspection_code bigint not null default 0,
                           conducted_by bigint references public.users(id),
                           part_id bigint references public.parts_of_aircraft(id),
                           aircraft_id bigint references public.aircraft(id)
);

CREATE TABLE IF NOT EXISTS public.aircraft_parts(
                                                         aircraft_id bigint references public.aircraft(id),
                                                         part_id bigint references public.parts_of_aircraft(id)
);

CREATE TABLE IF NOT EXISTS public.flights(
                                             id bigserial primary key,
                                             destination varchar not null,
                                             flight_status varchar not null,
                                             tickets_left integer not null default 0,
                                             registered_at timestamp not null,
                                             aircraft_id bigint references public.aircraft(id)
);

CREATE TABLE IF NOT EXISTS public.users_flights(
                                                   id bigserial primary key,
                                                   user_status varchar not null,
                                                   registered_at timestamp not null,
                                                   user_id bigint references public.users(id),
                                                   seat_id bigint references public.users(id),
                                                   flight_id bigint references public.flights(id)
);

CREATE TABLE IF NOT EXISTS public.client_reviews(
                                                      id bigserial primary key,
                                                      feedback_text text not null,
                                                      registered_at timestamp not null,
                                                      client_id bigint references public.users(id),
                                                      flight_id bigint references public.flights(id)
);
