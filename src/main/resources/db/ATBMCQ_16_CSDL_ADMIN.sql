
ALTER SESSION SET CONTAINER = ATBMCQ_16_CSDL;
SHOW CON_NAME;
-- 2. Xem danh sách tài khoản người dùng và role trong hệ thống Oracle DB Server.



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


-- Stored Procedure thu hồi quyền 

CREATE OR REPLACE PROCEDURE REVOKE_PRIVILEGE_FROM_USER_OR_ROLE (
    privilege_name IN VARCHAR2,
    grantee_name IN VARCHAR2
) AS
BEGIN
    -- Kiểm tra nếu là quyền trên đối tượng (ví dụ: SELECT, INSERT)
    IF privilege_name IN ('SELECT', 'INSERT', 'UPDATE', 'DELETE') THEN
        EXECUTE IMMEDIATE 'REVOKE ' || privilege_name || ' ON EMPLOYEES FROM ' || grantee_name;
        DBMS_OUTPUT.PUT_LINE('Privilege ' || privilege_name || ' on EMPLOYEES revoked from ' || grantee_name);

    -- Kiểm tra nếu là quyền hệ thống (ví dụ: CREATE TABLE, CREATE SESSION)
    ELSIF privilege_name IN ('CREATE TABLE', 'CREATE SESSION') THEN
        EXECUTE IMMEDIATE 'REVOKE ' || privilege_name || ' FROM ' || grantee_name;
        DBMS_OUTPUT.PUT_LINE('System privilege ' || privilege_name || ' revoked from ' || grantee_name);

    -- Kiểm tra nếu là role (ví dụ: CONNECT, RESOURCE)
    ELSE
        EXECUTE IMMEDIATE 'REVOKE ' || privilege_name || ' FROM ' || grantee_name;
        DBMS_OUTPUT.PUT_LINE('Role ' || privilege_name || ' revoked from ' || grantee_name);
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
/


CREATE OR REPLACE PROCEDURE VIEW_PRIVILEGES_FOR_USER_OR_ROLE (
    user_or_role_name IN VARCHAR2
) AS
BEGIN
    -- Hiển thị quyền trên các bảng và view
    DBMS_OUTPUT.PUT_LINE('Table/View Privileges:');
    FOR rec IN (
        SELECT grantee, privilege, table_name, grantable
        FROM dba_tab_privs
        WHERE grantee = UPPER(user_or_role_name)
    ) LOOP
        DBMS_OUTPUT.PUT_LINE('Grantee: ' || rec.grantee || ', Privilege: ' || rec.privilege ||
                             ', Table/View: ' || rec.table_name || ', Grantable: ' || rec.grantable);
    END LOOP;

    -- Hiển thị quyền hệ thống
    DBMS_OUTPUT.PUT_LINE('System Privileges:');
    FOR rec IN (
        SELECT grantee, privilege, admin_option
        FROM dba_sys_privs
        WHERE grantee = UPPER(user_or_role_name)
    ) LOOP
        DBMS_OUTPUT.PUT_LINE('Grantee: ' || rec.grantee || ', Privilege: ' || rec.privilege ||
                             ', Admin Option: ' || rec.admin_option);
    END LOOP;

    -- Hiển thị các role được cấp
    DBMS_OUTPUT.PUT_LINE('Role Privileges:');
    FOR rec IN (
        SELECT grantee, granted_role, admin_option
        FROM dba_role_privs
        WHERE grantee = UPPER(user_or_role_name)
    ) LOOP
        DBMS_OUTPUT.PUT_LINE('Grantee: ' || rec.grantee || ', Role: ' || rec.granted_role ||
                             ', Admin Option: ' || rec.admin_option);
    END LOOP;
END;
/