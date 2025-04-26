
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

REVOKE EXECUTE ON ATBMCQ_ADMIN.SP_ADD_ROLE FROM ANNU;

COMMIT;




