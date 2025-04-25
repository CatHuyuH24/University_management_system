-- Tạo user
CREATE USER user1 IDENTIFIED BY password1;
CREATE USER user2 IDENTIFIED BY password2;

-- Tạo role
CREATE ROLE role1;
CREATE ROLE role2;

-- Cấp quyền cơ bản để user có thể đăng nhập
GRANT CREATE SESSION TO user1, user2;

-- Tạo table
CREATE TABLE sample_table (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(50),
    description VARCHAR2(100)
);

-- Tạo view
CREATE VIEW sample_view AS
SELECT id, name FROM sample_table;

-- Tạo stored procedure
CREATE OR REPLACE PROCEDURE sample_procedure AS
BEGIN
    DBMS_OUTPUT.PUT_LINE('This is a sample procedure');
END;
/

-- Tạo function
CREATE OR REPLACE FUNCTION sample_function (p_id IN NUMBER)
RETURN VARCHAR2 AS
BEGIN
    RETURN 'Function result: ' || p_id;
END;
/

-- Cấp quyền hệ thống
GRANT SELECT ANY TABLE TO user1;
GRANT EXECUTE ANY PROCEDURE TO role1;

-- Cấp quyền đối tượng
GRANT SELECT, UPDATE ON sample_table TO user1 WITH GRANT OPTION;
GRANT EXECUTE ON sample_procedure TO role1;
GRANT SELECT ON sample_view TO user2;
GRANT EXECUTE ON sample_function TO role2;

-- Cấp quyền mức cột (chỉ UPDATE)
GRANT UPDATE (description) ON sample_table TO user2;

-- Quyền hệ thống
SELECT * FROM DBA_SYS_PRIVS WHERE grantee IN ('USER1', 'ROLE1');

-- Quyền đối tượng
SELECT * FROM DBA_TAB_PRIVS WHERE grantee IN ('USER1', 'USER2', 'ROLE1', 'ROLE2');

-- Quyền mức cột
SELECT * FROM DBA_COL_PRIVS WHERE grantee = 'USER2';

-- Stored Procedure REVOKE_PRIVILEGE
CREATE OR REPLACE PROCEDURE REVOKE_PRIVILEGE (
    p_grantee IN VARCHAR2,          -- Tên user hoặc role
    p_privilege IN VARCHAR2,        -- Loại quyền (SELECT, UPDATE, EXECUTE, etc.)
    p_object_name IN VARCHAR2,      -- Tên đối tượng (table, view, procedure, function)
    p_column_name IN VARCHAR2,      -- Tên cột (không áp dụng, để NULL)
    p_result OUT VARCHAR2           -- Kết quả hoặc thông báo lỗi
) AS
    v_sql VARCHAR2(1000);
BEGIN
    -- Kiểm tra tham số đầu vào
    IF p_grantee IS NULL OR p_privilege IS NULL THEN
        p_result := 'Error: Grantee and privilege cannot be NULL';
        RETURN;
    END IF;

    -- Không cho phép thu hồi quyền mức cột
    IF p_column_name IS NOT NULL THEN
        p_result := 'Error: Column-level revocation is not supported. Use table-level revocation for UPDATE.';
        RETURN;
    END IF;

    -- Xử lý thu hồi quyền đối tượng
    IF p_object_name IS NOT NULL THEN
        v_sql := 'REVOKE ' || p_privilege || ' ON ' || p_object_name || ' FROM ' || p_grantee;
    -- Xử lý thu hồi quyền hệ thống
    ELSE
        v_sql := 'REVOKE ' || p_privilege || ' FROM ' || p_grantee;
    END IF;

    -- Thực thi câu lệnh REVOKE
    EXECUTE IMMEDIATE v_sql;
    p_result := 'Privilege revoked successfully. Note: For UPDATE, all column-level privileges are also revoked.';

EXCEPTION
    WHEN OTHERS THEN
        p_result := 'Error: ' || SQLERRM;
END REVOKE_PRIVILEGE;
/

-- Kiểm tra
-- Thu hồi quyền hệ thống 
DECLARE
    v_result VARCHAR2(1000);
BEGIN
    REVOKE_PRIVILEGE('USER1', 'SELECT ANY TABLE', NULL, NULL, v_result);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
-- Thu hồi quyền đối tượng
DECLARE
    v_result VARCHAR2(1000);
BEGIN
    REVOKE_PRIVILEGE('USER1', 'SELECT', 'SAMPLE_TABLE', NULL, v_result);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/

-- Thu hồi quyền UPDATE
DECLARE
    v_result VARCHAR2(1000);
BEGIN
    REVOKE_PRIVILEGE('USER2', 'UPDATE', 'SAMPLE_TABLE', NULL, v_result);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
-- Stored procedure VIEW_PRIVILEGES

CREATE OR REPLACE PROCEDURE VIEW_PRIVILEGES (
    p_grantee IN VARCHAR2,          -- Tên user hoặc role
    p_result OUT SYS_REFCURSOR      -- Cursor chứa thông tin quyền
) AS
BEGIN
    -- Truy vấn quyền hệ thống, quyền đối tượng, và quyền mức cột
    OPEN p_result FOR
        SELECT 'SYSTEM' AS privilege_type, privilege, grantee, admin_option AS grant_option, NULL AS object_name, NULL AS column_name
        FROM DBA_SYS_PRIVS
        WHERE grantee = UPPER(p_grantee)
        UNION ALL
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

-- Cấp lại quyền
GRANT SELECT ANY TABLE TO user1;
GRANT EXECUTE ANY PROCEDURE TO role1;
GRANT SELECT, UPDATE ON sample_table TO user1 WITH GRANT OPTION;
GRANT EXECUTE ON sample_procedure TO role1;
GRANT SELECT ON sample_view TO user2;
GRANT EXECUTE ON sample_function TO role2;
GRANT UPDATE (description) ON sample_table TO user2;

--Chạy stored procedure
VARIABLE rc REFCURSOR;
EXEC VIEW_PRIVILEGES('USER1', :rc);
PRINT rc;
