/*
 --//am 20140810
 Script: demo.HIBITATE.datafill.sql
 Description: Insertin init data
 
 DROP SCHEMA test_schema CASCADE IF EXISTS;
 ALTER SCHEMA schema1 RENAME TO TEST_SCHEMA;
 DROP SCHEMA DUMMY CASCADE IF select count(*) from information_schema.system_schemas where UPPER(table_schem) = 'DUMMY' > 0;
 IF select count(*) from information_schema.system_schemas where UPPER(table_schem) = 'DUMMY' > 0
 */
SET SCHEMA HABITATE;

-- County/Provance
INSERT into country VALUES (1, 'United States');
INSERT into country VALUES (2, 'Russia');
INSERT into country VALUES (3, 'INDIA');
INSERT into country VALUES (4, 'UNUSED COUNTRY 1');
INSERT into country VALUES (5, 'Portugal');

-- Provance/State
INSERT into provance VALUES (1, 1 ,'Alabama');
INSERT into provance VALUES (1, 2 ,'Washington');
INSERT into provance VALUES (1, 3 ,'KY');

INSERT into provance VALUES (2, 1 ,'Kostromskaya Oblast');
INSERT into provance VALUES (2, 2 ,'Kaliningradskaya');
INSERT into provance VALUES (2, 3 ,'Moskovskaya');

INSERT into provance VALUES (3, 1 ,'Deli');
INSERT into provance VALUES (3, 2 ,'Mombay');
INSERT into provance VALUES (3, 3 ,'Pakistan?');
INSERT into provance VALUES (3, 4 ,'Boombay');

--Town
INSERT into town VALUES (1, 1, 1, 'Alkatrac', 100);
INSERT into town VALUES (1, 1, 2, 'Area 52', 200);

INSERT into town VALUES (1, 2, 1, 'W Town 1', 50);
INSERT into town VALUES (1, 2, 2, 'W Town 2', 150);
INSERT into town VALUES (1, 2, 3, 'W Town 3', 2150);

INSERT into town VALUES (1, 3, 1, 'KY 1', 10);
INSERT into town VALUES (1, 3, 2, 'KY 2', 110);
INSERT into town VALUES (1, 3, 3, 'KY 3', 310);

INSERT into town VALUES (2, 1, 1, 'Kostroma', 4);
INSERT into town VALUES (2, 1, 2, 'Kaluga', 24);

INSERT into town VALUES (2, 2, 1, 'Kaliningrad', 24);
INSERT into town VALUES (2, 2, 2, 'Sovetsk', 214);

INSERT into town VALUES (2, 3, 1, 'Moskva', 1224);

INSERT into town VALUES (3, 1, 1, 'Deli 1', 1);
INSERT into town VALUES (3, 2, 1, 'Mombay 1', 11);
INSERT into town VALUES (3, 3, 1, 'Pakista 1', 121);
INSERT into town VALUES (3, 4, 1, 'Bomb 1', 34);