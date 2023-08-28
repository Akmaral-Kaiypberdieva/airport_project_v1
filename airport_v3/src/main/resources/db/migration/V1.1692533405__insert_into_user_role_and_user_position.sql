insert into public.user_positions
(position_title)
values
    ('ADMIN'),
    ('AIRPORT_MANAGER'),
    ('MAIN_DISPATCHER'),
    ('DISPATCHER'),
    ('MAIN_ENGINEER'),
    ('ENGINEER'),
    ('PILOT'),
    ('MAIN_STEWARD'),
    ('STEWARD'),
    ('CLIENT');

insert into public.app_roles
(role_title, position_id)
values
    ('ADMIN', 1),
    ('MANAGER', 2),
    ('MAIN_DISPATCHER', 3),
    ('DISPATCHER', 4),
    ('MAIN_ENGINEER', 5),
    ('ENGINEER', 6),
    ('PILOT', 7),
    ('MAIN_STEWARD', 8),
    ('STEWARD', 9),
    ('CLIENT', null)
