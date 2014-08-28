/*
 --//am 20140815 find a way to NOT use so many scripts
 Script: testdb1.HABITATE.schema.4.sql
 Description: Tables Fillup
 */
SET SCHEMA HABITATE;

-- COUNTRY
insert INTO country  values (1, 'United states');
insert INTO country  values (2, 'Russia');
insert INTO country  values (3, 'India');
insert INTO country  values (4, 'Failed Country');
--select * from country;

-- PROVINCE
insert INTO province values (1, 'Alabama');
insert INTO province values (2, 'California');
insert INTO province values (3, 'KY');
insert INTO province values (4, 'Indiana');

insert INTO province values (5, 'Moskovskaya');
insert INTO province values (6, 'Kaliningradskaya');
insert INTO province values (7, 'Zabaikalskaya');

insert INTO province values (8, 'ShrakaShruka');
insert INTO province values (9, 'Kumar');
-- select * from province;

--TOWNS
insert INTO town values (1, 'Alabama 1', 100);
insert INTO town values (2, 'Alabama 2', 200);
insert INTO town values (3, 'Alabama 3', 300);

insert INTO town values (4, 'San Fran', 1000);
insert INTO town values (5, 'Sacramento', 2000);

insert INTO town values (6, 'Owensboro', 10);
insert INTO town values (7, 'Louisville', 100);
insert INTO town values (8, 'Lexington', 100);

insert INTO town values (9, 'New Albany', 5);

insert INTO town values (10, 'MOSKVA', 200);
insert INTO town values (11, 'Orexovo-Zievo', 70);

insert INTO town values (12, 'Kaliningrad', 10);
insert INTO town values (13, 'Sovetsk', 10);

insert INTO town values (14, 'Irkutsk', 3);

insert INTO town values (15, 'Deli City', 100);
insert INTO town values (16, 'Boombay', 200);
insert INTO town values (17, 'Moomby', 300);

insert INTO town (town_name, population) values ('NoWhereTown', 89);
--select * from town;



-- COUNTRY-PROVINCE
--US
insert INTO country_province values (1, 1, 1, current_timestamp);
insert INTO country_province values (2, 1, 2, current_time);
insert INTO country_province values (3, 1, 3, current_time);
insert INTO country_province values (4, 1, 4, current_time);
--Rus
insert INTO country_province values (5, 2, 5, current_time);
insert INTO country_province values (6, 2, 6, current_time);
insert INTO country_province values (7, 2, 7, current_time);
--India
insert INTO country_province values (8, 3, 8, current_time);
insert INTO country_province values (9, 3, 9, current_time);
--select * from country_province;


-- PROVINCE-TOWN
--Alaba
insert INTO province_town values (1, 1, 1, current_time);
insert INTO province_town values (2, 1, 2, current_timestamp);
insert INTO province_town values (3, 1, 3, current_timestamp);

--Califo
insert INTO province_town values (4, 2, 4, current_timestamp);
insert INTO province_town values (5, 2, 5, current_timestamp);

--Ky
insert INTO province_town values (6, 3, 6, current_timestamp);
insert INTO province_town values (7, 3, 7, current_timestamp);
insert INTO province_town values (8, 3, 8, current_timestamp);

--India
insert INTO province_town values (9, 4, 9, current_timestamp);

--Moskva
insert INTO province_town values (12, 5, 10, current_timestamp);
insert INTO province_town values (13, 5, 11, current_timestamp);
--Kal-d
insert INTO province_town values (14, 6, 12, current_timestamp);
insert INTO province_town values (15, 6, 13, current_timestamp);
--Irkutsk
insert INTO province_town values (16, 7, 14, current_timestamp);
--Shakash
insert INTO province_town values (17, 8, 15, current_timestamp);
insert INTO province_town values (18, 8, 16, current_timestamp);
--Boomay
insert INTO province_town values (19, 9, 17, current_timestamp);
--insert INTO province_town values (next value for seq_1, 2, 2, localtimestamp);
-- select * from province_town;