
ALTER SESSION SET CONTAINER = ATBMCQ_16_CSDL;
SHOW CON_NAME;
-- 2. Xem danh sách tài khoản người dùng và role trong hệ thống Oracle DB Server.

SELECT * FROM USER_ROLE_PRIVS WHERE ADMIN_OPTION = 'YES';

SELECT role FROM dba_roles; -- SEE ALL, INCLUDING THOSE MANAGED BY ORACLE DB


CREATE OR REPLACE FUNCTION SP_VIEW_ALL_USERS_ROLES
AS
BEGIN
SELECT username, account_status, created
FROM dba_users
WHERE ACCOUNT_STATUS = 'OPEN';
    
    
END


CREATE OR REPLACE FUNCTION SP_VIEW_ALL_USERS_ROLES
RETURN user_info_tab
PIPELINED
AS
BEGIN
  FOR rec IN (
    SELECT username, account_status, created
    FROM dba_users
    WHERE oracle_maintained = 'N'
      AND account_status = 'OPEN'
  )
  LOOP
    PIPE ROW (user_info_obj(rec.username, rec.account_status, rec.created));
  END LOOP;

  RETURN;
END;
/


SELECT role
FROM dba_roles
WHERE oracle_maintained = 'N'; -- SEE ONLY ROLES MANAGED BY THE USER (NOT BY ORACLE DB)

SELECT grantee, granted_role 
FROM dba_role_privs 
WHERE grantee IN (SELECT username FROM dba_users);


-- CREATE A USER TO TEST AS CLIENT
CREATE USER USER1 IDENTIFIED BY "321";
GRANT CREATE SESSION TO USER1;
GRANT EXECUTE ON IS_ADMIN_USER TO USER1;