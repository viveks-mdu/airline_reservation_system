create schema if not exists airline;

create table airport 
(
airport_code varchar(32)
primary key,
name varchar(64),
city varchar(64),
state varchar(64)
);

create table airplane_type
(
airplane_type_name varchar(16)
primary key,
max_seats int(16) not null default 0,
company varchar(64)
);

create table airplane
(
airplane_id int(16) auto_increment
primary key,
total_number_of_seats int(16) not null default 0,
airplane_type varchar(16),
foreign key(airplane_type) references airplane_type (airplane_type_name)
);

create table can_land
(
airplane_type_name varchar(16),
airport_code varchar(32),
foreign key (airplane_type_name) references airplane_type(airplane_type_name),
foreign key (airport_code) references airport(airport_code),
primary key(airplane_type_name, airport_code)
);

create table flight
(
flight_number int(16) auto_increment
primary key,
airline varchar(64) not null,
weekdays varchar(7) not null,
departure_airport_code varchar(32),
foreign key (departure_airport_code) references airport (airport_code),
scheduled_departure_time time,
arrival_airport_code varchar(32),
foreign key (arrival_airport_code) references airport (airport_code),
scheduled_arrival_time time
);

create table flight_instance
(
flight_number int(16),
foreign key (flight_number) references flight (flight_number),
date date not null,
number_of_available_seats int(16),
airplane_id int(16),
foreign key (airplane_id) references airplane (airplane_id),
departure_time time,
arrival_time time,
primary key(flight_number, date)
);

create table fare
(
flight_number int(16),
foreign key (flight_number) references flight(flight_number),
fare_code varchar(16) not null,
amount decimal(10,2) not null default 0,
restrictions varchar(64),
primary key(flight_number, fare_code)
);

create table seat_reservation
(
flight_number int(16),
foreign key (flight_number) references flight (flight_number),
date date not null,
seat_number int(16) not null,
customer_name varchar(64),
customer_phone int(16),
primary key (flight_number, date, seat_number)
);

create trigger upd_max_seats
before update of max_seats on airplane_type
for each row
chk_total_seats(airplane_type_name,
old.max_seats, new.max_seats)
;

delimiter //

create procedure chk_total_seats(
IN type varchar(32),
IN o_max_seats int(16),
IN n_max_seats int(16))
BEGIN
	select * from airplane where airplane_type = type;
END//

delimiter ;


insert into airport values ("mdu", "Periyar Airport", "Madurai", "TN");
