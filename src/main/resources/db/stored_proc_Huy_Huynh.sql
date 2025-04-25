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

-- Xem các vai trò trong PDB, lấy thông tin Name, Id của Role
CREATE OR REPLACE FUNCTION FN_GET_PDB_ROLES
RETURN SYS_REFCURSOR
AS
    role_cursor SYS_REFCURSOR;
BEGIN
    OPEN role_cursor FOR
        SELECT ROLE FROM DBA_ROLES WHERE common = 'NO';
    RETURN role_cursor;
END;
/

-- HÀM KIỂM TRA XEM USER CÓ TỒN TẠI KHÔNG
CREATE OR REPLACE FUNCTION FN_DOES_USER_EXIST(
    p_username IN VARCHAR2
) RETURN INT
AS
    v_count INT;
BEGIN
    SELECT COUNT(*) INTO v_count FROM all_users WHERE username = UPPER(p_username);
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;


CREATE OR REPLACE PROCEDURE SP_ADD_USER_ALLOW_CREATESESSION_ISADMINUSER(
    p_username IN VARCHAR2,
    p_password IN VARCHAR2
)
AS
    user_already_existed  EXCEPTION;
    PRAGMA EXCEPTION_INIT(user_already_existed, -20001);
    
    v_result INT;
BEGIN
    -- Check if user exists
    v_result := fn_does_user_exist(p_username);
    IF v_result = 1 THEN
        RAISE user_already_existed;
    END IF;

    -- Create user
    EXECUTE IMMEDIATE 'CREATE USER ' || p_username || ' IDENTIFIED BY "' || p_password || '"';

    -- Grant session
    EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO ' || p_username;
    EXECUTE IMMEDIATE 'GRANT EXECUTE ON ATBMCQ_ADMIN.IS_ADMIN_USER TO ' || p_username;
    
    COMMIT;
EXCEPTION
    WHEN user_already_existed THEN 
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'User "' || p_username || '" already exists.');
        
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;



-- thay đổi Password của người dùng trong PDB
CREATE OR REPLACE PROCEDURE SP_CHANGE_USER_PASSWORD(
    p_username      IN VARCHAR2,
    p_new_password  IN VARCHAR2
) AS
    user_exist INT;
    v_sql       VARCHAR2(1000);
BEGIN
    -- Validate username to contain only allowed characters
    IF NOT REGEXP_LIKE(p_username, '^[A-Za-z0-9_]+$') THEN
        RAISE_APPLICATION_ERROR(-20002, 'Invalid username.');
    END IF;

    user_exist := fn_does_user_exist(p_username);
    IF user_exist = 1 THEN
        v_sql := 'ALTER USER "' || p_username || '" IDENTIFIED BY "' || p_new_password || '"';
        EXECUTE IMMEDIATE v_sql;
    ELSE
        RAISE_APPLICATION_ERROR(-20003, 'User does not exist.');
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;


// XÓA ROLE
CREATE OR REPLACE PROCEDURE SP_DROP_ROLE(
    p_role_name IN VARCHAR2
) AS
BEGIN
    -- Xóa role
    EXECUTE IMMEDIATE 'DROP ROLE ' || p_role_name;
    DBMS_OUTPUT.PUT_LINE('Role "' || p_role_name || '" đã được xóa.');
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Lỗi khi xóa role "' || p_role_name || '": ' || SQLERRM);
END;
/

-- XOA USER
CREATE OR REPLACE PROCEDURE SP_DROP_USER(
    p_username IN VARCHAR2
) AS
    v_user_exist INT;
BEGIN
    v_user_exist := fn_does_user_exist(p_username);
    IF v_user_exist = 1 THEN
        EXECUTE IMMEDIATE 'DROP USER ' || p_username;
        COMMIT;
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERROR DELETING USER "' || p_username || '"');
        RAISE;
END;
/

-- TẠO ROLE
CREATE OR REPLACE PROCEDURE SP_ADD_ROLE(
p_role_name IN VARCHAR2
) AS
    v_count NUMBER;
    role_already_existed  EXCEPTION;
    PRAGMA EXCEPTION_INIT(role_already_existed, -20010);
BEGIN
    -- Check if the role already exists
    SELECT COUNT(*) INTO v_count
    FROM dba_roles
    WHERE role = UPPER(p_role_name);

    IF v_count = 0 THEN
        -- Role does not exist, create it
        EXECUTE IMMEDIATE 'CREATE ROLE ' || DBMS_ASSERT.SIMPLE_SQL_NAME(p_role_name);
        DBMS_OUTPUT.PUT_LINE('Role "' || p_role_name || '" has been created.');
    ELSE
        -- Role already exists
        RAISE role_already_existed;
        DBMS_OUTPUT.PUT_LINE('Role "' || p_role_name || '" already exists.');
    END IF;
EXCEPTION
    WHEN role_already_existed THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Role "' || p_role_name || '" already exists.');
        
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
/
