-- YEU CAU 3 AUDIT
-- KET NOI BANG ATBMCQ_ADMIN
-----------------------------------------------------------------------------------------------
-- CAU 2 STANDARD AUDIT
CREATE OR REPLACE PROCEDURE PROC_TANG_LUONG(p_manv VARCHAR2, p_muc NUMBER) IS
BEGIN
  UPDATE NHANVIEN SET LUONG = LUONG + p_muc WHERE MANV = p_manv;
END;

-- AUDIT CHO USER NÀO THÌ CẤP QUYỀN CHO USER ĐÓ
GRANT EXECUTE ON PROC_TANG_LUONG TO NV004;
AUDIT EXECUTE ON PROC_TANG_LUONG BY ACCESS;


SELECT 
   *
FROM 
    DBA_AUDIT_TRAIL
WHERE 
    OBJ_NAME = 'PROC_TANG_LUONG'
ORDER BY 
    TIMESTAMP DESC;



-------------------------------------------------------------
CREATE OR REPLACE FUNCTION FN_TINHLUONG(p_luong NUMBER, p_phucap NUMBER)
RETURN NUMBER IS
BEGIN
  RETURN p_luong * 0.1 + p_phucap * 0.05;
END;

GRANT EXECUTE ON FN_TINHLUONG TO NV004;
AUDIT EXECUTE ON FN_TINHLUONG BY ACCESS;


SELECT *
FROM DBA_AUDIT_TRAIL
WHERE OBJ_NAME = 'FN_TINHLUONG'
ORDER BY TIMESTAMP DESC;

-----------------------------------------------


CREATE OR REPLACE VIEW V_XEMDIEM AS
SELECT DIEMTH, DIEMQT, DIEMCK, DIEMTK FROM ATBMCQ_ADMIN.DANGKY;

AUDIT SELECT ON V_XEMDIEM BY ACCESS;
GRANT SELECT ON V_XEMDIEM TO NV009;

SELECT 
  *
FROM 
    DBA_AUDIT_TRAIL
WHERE 
    OBJ_NAME = 'V_XEMDIEM'
    AND ACTION_NAME = 'SELECT'
ORDER BY 
    TIMESTAMP DESC;

-----------------------------------------------

AUDIT SELECT, INSERT, UPDATE ON HOCPHAN BY ACCESS;
AUDIT SELECT, INSERT, UPDATE ON SINHVIEN BY ACCESS;
AUDIT SELECT, INSERT, UPDATE ON DANGKY BY ACCESS;

GRANT SELECT, INSERT, UPDATE ON HOCPHAN TO NV010;
GRANT SELECT, INSERT, UPDATE ON SINHVIEN TO NV010;
GRANT SELECT, INSERT, UPDATE ON DANGKY TO NV009;
GRANT SELECT, INSERT, UPDATE ON NHANVIEN TO NV001;

SELECT 
   *
FROM 
    DBA_AUDIT_TRAIL
WHERE 
    USERNAME = 'NV010'
    AND OBJ_NAME = 'SINHVIEN'
    AND ACTION_NAME IN ('SELECT', 'INSERT', 'UPDATE')
ORDER BY 
    TIMESTAMP DESC;

------------------------------------------------------------------------------------------------------

-- CAU 3
-- 3A

-- Cấp vai trò NV_PKT cho user


BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'ATBMCQ_ADMIN',
    object_name     => 'DANGKY',
    policy_name     => 'FGA_AUDIT_UPDATE_NOT_NVPKT',
    audit_column    => 'DIEMTH, DIEMQT, DIEMCK, DIEMTK',
    audit_condition => 'SYS_CONTEXT(''USER_CTX'', ''VAI_TRO'') <> ''NV PKT''',
    statement_types => 'UPDATE',
    enable          => TRUE
  );
END;


--
--BEGIN
--  DBMS_FGA.DROP_POLICY(
--    object_schema => 'ATBMCQ_ADMIN',
--    object_name   => 'DANGKY',
--    policy_name   => 'FGA_DANGKY_NGOAI_TG'
--  );
--END;



SELECT 
  *
FROM 
  DBA_FGA_AUDIT_TRAIL
WHERE 
  OBJECT_NAME = 'DANGKY'
  AND POLICY_NAME = 'FGA_AUDIT_UPDATE_NOT_NVPKT'
ORDER BY TIMESTAMP DESC;



ALTER USER USER_PKT DEFAULT ROLE NV_PKT;
GRANT CREATE SESSION TO USER_PKT;


SELECT
  l.session_id AS sid,
  s.serial#,
  s.username,
  o.object_name,
  l.locked_mode
FROM
  v$locked_object l
JOIN dba_objects o ON l.object_id = o.object_id
JOIN v$session s ON s.sid = l.session_id;

SELECT sid, serial#, username, status, osuser, program
FROM v$session
WHERE username = 'USER_PKT';

ALTER SYSTEM KILL SESSION '249,14760' IMMEDIATE;

ALTER SYSTEM KILL SESSION '1104,20577' IMMEDIATE;

------------------------------------------------------------------------------------------------------

-- 3B

BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'ATBMCQ_ADMIN',
    object_name     => 'NHANVIEN',
    policy_name     => 'FGA_SEL_LUONG_PHUCAP_NOT_NVTCHC',
    audit_column    => 'LUONG,PHUCAP',
    audit_condition => 'SYS_CONTEXT(''USER_CTX'', ''VAI_TRO'') <> ''NV TCHC''',
    statement_types => 'SELECT',
    enable          => TRUE
  );
END;
/


BEGIN
  DBMS_FGA.DROP_POLICY(
    object_schema => 'ATBMCQ_ADMIN',
    object_name   => 'NHANVIEN',
    policy_name   => 'FGA_SEL_LUONG_PHUCAP_NOT_NVTCHC'
  );
END;
/

SELECT
  *
FROM
  dba_fga_audit_trail
WHERE
  object_name = 'NHANVIEN'
ORDER BY timestamp DESC;




BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'ATBMCQ_ADMIN',
    object_name     => 'NHANVIEN',
    policy_name     => 'FGA_UPD_NHANVIEN_NOT_NVTCHC',
    audit_condition => 'SYS_CONTEXT(''USER_CTX'', ''VAI_TRO'') <> ''NV TCHC''',
    statement_types => 'UPDATE',
    enable          => TRUE
  );
END;
/

SELECT
 *
FROM
  dba_fga_audit_trail
WHERE
  policy_name = 'FGA_UPD_NHANVIEN_NOT_NVTCHC'
ORDER BY
  timestamp DESC;

------------------------------------------------------------------------------------------------------

-- 3C

BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'ATBMCQ_ADMIN',
    object_name     => 'DANGKY',
    policy_name     => 'FGA_DANGKY_SAI_SV',
    audit_condition => 'MASV <> SYS_CONTEXT(''USERENV'', ''SESSION_USER'')',
    audit_column    => NULL,
    handler_module  => NULL,
    enable          => TRUE,
    statement_types => 'UPDATE,DELETE',
    audit_trail     => DBMS_FGA.DB + DBMS_FGA.EXTENDED
  );
END;
/

BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'ATBMCQ_ADMIN',
    object_name     => 'DANGKY',
    policy_name     => 'FGA_DANGKY_NGOAI_TG',
    audit_condition => q'[
      regexp_count(
        to_char(sysdate,'MM-DD'),
        '^(01|05|09)-(0[1-9]|1[0-4])$'
      ) = 0
    ]',
    audit_column    => NULL,
    handler_module  => NULL,
    enable          => TRUE,
    statement_types => 'UPDATE,INSERT,DELETE',
    audit_trail     => DBMS_FGA.DB + DBMS_FGA.EXTENDED
  );
END;
/



SELECT
 *
FROM dba_fga_audit_trail


