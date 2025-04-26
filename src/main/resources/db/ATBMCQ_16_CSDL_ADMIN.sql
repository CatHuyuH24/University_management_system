
ALTER SESSION SET CONTAINER = ATBMCQ_16_CSDL;
SHOW CON_NAME;

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

--Thực hiện cấp quyền trên một số loại đối tượng của CSDL như: table, view,
--stored procedure, function.
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

CREATE TABLE EMPLOYEES (
    EMP_ID      NUMBER PRIMARY KEY,
    FIRST_NAME  VARCHAR2(50),
    LAST_NAME   VARCHAR2(50),
    HIRE_DATE   DATE,
    SALARY      NUMBER(10, 2),
    DEPT_ID     NUMBER
);

CREATE TABLE EMPLOYEES2 (
    EMP_ID      NUMBER PRIMARY KEY,
    FIRST_NAME  VARCHAR2(50),
    LAST_NAME   VARCHAR2(50),
    HIRE_DATE   DATE,
    SALARY      NUMBER(10, 2),
    DEPT_ID     NUMBER
);

CREATE USER ANNU IDENTIFIED BY 123;
GRANT UPDATE (FIRST_NAME,SALARY) ON ATBMCQ_ADMIN.EMPLOYEES TO ANNU;

GRANT CREATE SESSION TO ANNU;
select * from employees

REVOKE UPDATE ON EMPLOYEES FROM ANNU;

INSERT INTO EMPLOYEES (EMP_ID, FIRST_NAME, LAST_NAME, HIRE_DATE, SALARY, DEPT_ID) 
VALUES (1, 'John', 'Doe', TO_DATE('2020-01-15', 'YYYY-MM-DD'), 5000, 10);

INSERT INTO EMPLOYEES (EMP_ID, FIRST_NAME, LAST_NAME, HIRE_DATE, SALARY, DEPT_ID) 
VALUES (2, 'Jane', 'Smith', TO_DATE('2021-06-10', 'YYYY-MM-DD'), 5500, 20);

INSERT INTO EMPLOYEES (EMP_ID, FIRST_NAME, LAST_NAME, HIRE_DATE, SALARY, DEPT_ID) 
VALUES (3, 'Alice', 'Brown', TO_DATE('2022-03-01', 'YYYY-MM-DD'), 6000, 10);

BEGIN
    grant_column_privilege(
        p_privilege   => 'UPDATE',
        p_object_name => 'ATBMCQ_ADMIN.EMPLOYEES',
        p_columns     => 'FIRST_NAME, SALARY',
        p_grantee     => 'ANNU',
        p_with_option => 0
    );
END;
/
CREATE OR REPLACE PROCEDURE say_hello IS
BEGIN
    DBMS_OUTPUT.PUT_LINE('Hello from procedure!');
END;
/

BEGIN
    grant_privilege(
        p_privilege     => 'EXECUTE',
        p_object_name   => 'ATBMCQ_ADMIN.SAY_HELLO',
        p_object_type   => 'PROCEDURE',
        p_grantee       => 'ANNU',
        p_with_option   => FALSE
    );
END;
/
REVOKE EXECUTE ON ATBMCQ_ADMIN.SAY_HELLO FROM ANNU;

COMMIT;




