/*
 --//am 20140810 find a way to check if schema exists
 Script: demo.HABITATE.schema.sql
 Description: Just Schema and Tables re-createion
  
 DROP SCHEMA test_schema CASCADE IF EXISTS;
 ALTER SCHEMA schema1 RENAME TO TEST_SCHEMA;
 DROP SCHEMA DUMMY CASCADE IF select count(*) from information_schema.system_schemas where UPPER(table_schem) = 'DUMMY' > 0;
 IF select count(*) from information_schema.system_schemas where UPPER(table_schem) = 'DUMMY' > 0
 */
DROP SCHEMA HABITATE CASCADE;
CREATE SCHEMA HABITATE AUTHORIZATION DBA;
SET SCHEMA HABITATE;

-- County
DROP TABLE country IF EXISTS;
CREATE TABLE country (country_id integer primary key, name varchar(30) not null);

-- Provance/State
DROP TABLE provance IF EXISTS;
CREATE TABLE provance (country_id int, provance_id int, provance varchar(30) not null);

-- Town
DROP TABLE town IF EXISTS;
CREATE TABLE town (country_id int, provance_id int, town_id int, town_name varchar(30) not null, population int);