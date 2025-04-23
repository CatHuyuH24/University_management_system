CREATE OR REPLACE PROCEDURE IS_ADMIN_USER (result OUT NUMBER)
AUTHID CURRENT_USER
AS
BEGIN
    SELECT CASE
        WHEN EXISTS (
            SELECT 1
            FROM USER_ROLE_PRIVS
            WHERE GRANTED_ROLE = 'PDB_DBA' AND ADMIN_OPTION = 'YES'
        ) THEN 1
        ELSE 0
    END INTO result
    FROM DUAL;
END;


-- XEM MỌI NGƯỜI DÙNG TRONG PDB, LẤY THÔNG TIN USERNAME, USER_ID, CREATED
CREATE OR REPLACE FUNCTION FN_GET_USERS
RETURN SYS_REFCURSOR
AS
    user_cursor SYS_REFCURSOR;
BEGIN
    OPEN user_cursor FOR
        SELECT USERNAME, CREATED FROM all_users WHERE common = 'NO';
    RETURN user_cursor;
END;
/

// Xem các vai trò trong PDB, lấy thông tin Name, Id của Role
CREATE OR REPLACE FUNCTION FN_GET_PDB_ROLES
RETURN SYS_REFCURSOR
AS
    role_cursor SYS_REFCURSOR;
BEGIN
    OPEN role_cursor FOR
        SELECT ROLE, ROLE_ID FROM DBA_ROLES WHERE common = 'NO';
    RETURN role_cursor;
END;
/

// thay đổi Password của người dùng trong PDB
CREATE OR REPLACE PROCEDURE change_user_password(
    p_username      IN VARCHAR2,
    p_new_password  IN VARCHAR2
) AS
BEGIN
    EXECUTE IMMEDIATE 'ALTER USER "' || p_username || '" IDENTIFIED BY "' || p_new_password || '"';
    DBMS_OUTPUT.PUT_LINE('Password for user "' || p_username || '" has been changed.');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
/
