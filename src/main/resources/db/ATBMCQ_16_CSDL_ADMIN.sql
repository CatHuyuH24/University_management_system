-- Switch to the correct container
ALTER SESSION SET CONTAINER = ATBMCQ_16_CSDL;
SHOW CON_NAME;

-- View non-Oracle-maintained roles
SELECT role
FROM dba_roles
WHERE oracle_maintained = 'N';

-- Create initial test user and roles
CREATE USER USER1 IDENTIFIED BY "321";


-- View users that are not common (i.e., local users)
SELECT *
FROM all_users
WHERE COMMON = 'NO';

-- Initial test roles
CREATE ROLE TEST_ROLE;
CREATE ROLE TEST_ROLE2;
CREATE ROLE TEST_ROLE3;

-- Create 500 users USER1 to USER500 with password '321'
BEGIN
   FOR i IN 1 .. 500 LOOP
      EXECUTE IMMEDIATE 'CREATE USER USER' || i || ' IDENTIFIED BY "321"';
      EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO USER' || i;
      EXECUTE IMMEDIATE 'GRANT EXECUTE ON ATBMCQ_ADMIN.IS_ADMIN_USER TO USER' || i;
   END LOOP;
END;
/

-- Create 100 roles TEST_ROLE1 to TEST_ROLE400
BEGIN
   FOR i IN 1 .. 100 LOOP
      EXECUTE IMMEDIATE 'CREATE ROLE TEST_ROLE' || i;
   END LOOP;
END;
/
