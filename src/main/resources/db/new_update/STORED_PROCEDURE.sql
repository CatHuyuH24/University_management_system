CONNECT "ATBMCQ_ADMIN"/"123"@//localhost:1521/ATBMCQ_16_CSDL;
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
/

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
/

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
/


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
/

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
        COMMIT;
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
        RAISE;
END;
/


CREATE OR REPLACE PROCEDURE SP_ADD_ROLE_TO_USER(
     p_username IN VARCHAR2,
     p_rolename IN VARCHAR2
) AS
BEGIN
    EXECUTE IMMEDIATE 'GRANT ' || p_rolename || ' TO ' || p_username;
    
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
/


CREATE OR REPLACE PROCEDURE SP_REMOVE_ROLE_FROM_USER(
     p_username IN VARCHAR2,
     p_rolename IN VARCHAR2
) AS
BEGIN
    EXECUTE IMMEDIATE 'REVOKE ' || p_rolename || ' FROM ' || p_username;
    
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
/
CREATE OR REPLACE FUNCTION FN_GET_USER_ROLES (
    p_username IN VARCHAR2
) RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT rp.GRANTED_ROLE
        FROM DBA_ROLE_PRIVS rp
        JOIN DBA_ROLES r ON rp.GRANTED_ROLE = r.ROLE
        WHERE rp.GRANTEE = UPPER(p_username)
          AND r.COMMON = 'NO'  -- Only roles local to the current PDB
        ORDER BY rp.GRANTED_ROLE;

    RETURN v_cursor;
END;
/

CREATE OR REPLACE PROCEDURE VIEW_PRIVILEGES (
    p_grantee IN VARCHAR2,          -- Tên user hoặc role
    p_result OUT SYS_REFCURSOR      -- Cursor chứa thông tin quyền
) AS
BEGIN
    -- Truy vấn quyền đối tượng và quyền mức cột
    OPEN p_result FOR
        SELECT 'OBJECT' AS privilege_type, privilege, grantee, grantable AS grant_option, table_name AS object_name, NULL AS column_name
        FROM DBA_TAB_PRIVS
        WHERE grantee = UPPER(p_grantee)
        UNION ALL
        SELECT 'COLUMN' AS privilege_type, privilege, grantee, grantable AS grant_option, table_name AS object_name, column_name
        FROM DBA_COL_PRIVS
        WHERE grantee = UPPER(p_grantee);
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20001, 'Error retrieving privileges: ' || SQLERRM);
END VIEW_PRIVILEGES;
/

CREATE OR REPLACE PROCEDURE SP_LIST_GRANTED_ROLES(
    p_role_name   IN  VARCHAR2,
    p_result      OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_result FOR
        SELECT granted_role
        FROM dba_role_privs
        WHERE grantee = UPPER(p_role_name)
        ORDER BY granted_role;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/

CREATE OR REPLACE PROCEDURE SP_LIST_ROLES_EXCLUDE(
    p_exclude_role IN  VARCHAR2,
    p_result       OUT SYS_REFCURSOR
)
AS
BEGIN
    -- Open the cursor for the query
    OPEN p_result FOR
        SELECT role
        FROM dba_roles
        WHERE role != DBMS_ASSERT.SIMPLE_SQL_NAME(UPPER(p_exclude_role))
          AND common = 'NO'
        ORDER BY role;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/

CREATE OR REPLACE PROCEDURE SP_GRANT_ROLE_TO_ROLE(
    p_grant_role IN VARCHAR2,
    p_target_role IN VARCHAR2
)
AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra xem role cấp có tồn tại không
    SELECT COUNT(*) INTO v_count
    FROM dba_roles
    WHERE role = UPPER(p_grant_role);

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Role "' || p_grant_role || '" does not exist.');
    END IF;

    -- Kiểm tra xem role nhận có tồn tại không
    SELECT COUNT(*) INTO v_count
    FROM dba_roles
    WHERE role = UPPER(p_target_role);

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Role "' || p_target_role || '" does not exist.');
    END IF;

    -- Kiểm tra ngược lại: target_role đã được grant cho grant_role chưa?
    SELECT COUNT(*) INTO v_count
    FROM dba_role_privs
    WHERE granted_role = UPPER(p_target_role)
      AND grantee = UPPER(p_grant_role);

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Cannot grant "' || p_grant_role || '" to "' || p_target_role || '" because it would create a circular grant.');
    END IF;

    -- Cấp quyền cho role
    EXECUTE IMMEDIATE 'GRANT ' || DBMS_ASSERT.SIMPLE_SQL_NAME(p_grant_role) ||
                      ' TO ' || DBMS_ASSERT.SIMPLE_SQL_NAME(p_target_role);

    DBMS_OUTPUT.PUT_LINE('Granted role "' || p_grant_role || '" to role "' || p_target_role || '".');

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/


CREATE OR REPLACE PROCEDURE REVOKE_PRIVILEGE (
    p_grantee IN VARCHAR2,          -- Tên user hoặc role
    p_privilege IN VARCHAR2,        -- Loại quyền (SELECT, UPDATE, EXECUTE, etc.)
    p_object_name IN VARCHAR2,      -- Tên đối tượng (table, view, procedure, function)
    p_result OUT VARCHAR2           -- Kết quả hoặc thông báo lỗi
) AS
    v_sql VARCHAR2(1000);
BEGIN
    -- Kiểm tra tham số đầu vào
    IF p_grantee IS NULL OR p_privilege IS NULL THEN
        p_result := 'Error: Grantee and privilege cannot be NULL';
        RETURN;
    END IF;

    -- Kiểm tra p_object_name phải không NULL (chỉ hỗ trợ quyền đối tượng)
    IF p_object_name IS NULL THEN
        p_result := 'Error: Object name must be specified. System privilege revocation is not supported.';
        RETURN;
    END IF;

    -- Xử lý thu hồi quyền đối tượng
    v_sql := 'REVOKE ' || p_privilege || ' ON ' || p_object_name || ' FROM ' || p_grantee;

    -- Thực thi câu lệnh REVOKE
    EXECUTE IMMEDIATE v_sql;
    
    IF p_privilege = 'UPDATE' THEN
        p_result := 'Privilege revoked successfully. All column-level UPDATE privileges on ' || p_object_name || ' are also revoked.';
    ELSE
        p_result := 'Privilege revoked successfully.';
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        p_result := 'Error: ' || SQLERRM;
END REVOKE_PRIVILEGE;
/

CREATE OR REPLACE PROCEDURE grant_privilege (
    p_privilege     IN VARCHAR2,
    p_object_name   IN VARCHAR2,
    p_object_type   IN VARCHAR2,
    p_grantee       IN VARCHAR2,
    p_with_option   IN INT
) AS
    v_sql VARCHAR2(1000);
BEGIN
    -- Kiểm tra kiểu đối tượng
    IF p_object_type NOT IN ('TABLE', 'VIEW', 'PROCEDURE', 'FUNCTION') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Loại đối tượng không hợp lệ.');
    END IF;

    -- Xây dựng câu lệnh GRANT
    v_sql := 'GRANT ' || p_privilege || ' ON ' || p_object_name || ' TO ' || p_grantee;

    IF p_with_option = 1 THEN
        v_sql := v_sql || ' WITH GRANT OPTION';
    END IF;

    -- Thực thi lệnh GRANT
    EXECUTE IMMEDIATE v_sql;

    -- Output thông báo
    DBMS_OUTPUT.PUT_LINE(' Thực hiện: ' || v_sql);
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(' Lỗi: ' || SQLERRM);
END;
/

--Quyền select, update phải cho phép phân quyền tính đến mức cột; quyền insert,
--delete thì không.
CREATE OR REPLACE PROCEDURE grant_column_privilege (
    p_privilege     IN VARCHAR2,       -- 'SELECT' hoặc 'UPDATE'
    p_object_name   IN VARCHAR2,       -- Tên bảng hoặc view (schema.table_name)
    p_columns       IN VARCHAR2,       -- Danh sách cột, cách nhau bằng dấu phẩy
    p_grantee       IN VARCHAR2,       -- User hoặc role được cấp quyền
    p_with_option   IN INT
) AS
    v_sql VARCHAR2(1000);
BEGIN
    IF UPPER(p_privilege) NOT IN ('SELECT', 'UPDATE') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Chỉ hỗ trợ SELECT hoặc UPDATE với cấp cột.');
    END IF;

    v_sql := 'GRANT ' || p_privilege || ' (' || p_columns || ') ON ' || p_object_name || ' TO ' || p_grantee;

    IF p_with_option = 1 THEN
        v_sql := v_sql || ' WITH GRANT OPTION';
    END IF;

    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE(' Đã thực hiện: ' || v_sql);
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(' Lỗi: ' || SQLERRM);
END;
/


COMMIT;
DISCONNECT;
