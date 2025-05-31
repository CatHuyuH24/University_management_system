-- YEU CAU 3 AUDIT
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
GRANT SELECT ON V_XEMDIEM TO NV_NAO_DO;

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

GRANT SELECT, INSERT, UPDATE ON HOCPHAN TO USER_SV;
GRANT SELECT, INSERT, UPDATE ON SINHVIEN TO USER_SV;
GRANT SELECT, INSERT, UPDATE ON DANGKY TO USER_SV;
GRANT SELECT, INSERT, UPDATE ON NHANVIEN TO USER_SV;

SELECT 
   *
FROM 
    DBA_AUDIT_TRAIL
WHERE 
    USERNAME = 'USER_SV'
    AND OBJ_NAME = 'SINHVIEN'
    AND ACTION_NAME IN ('SELECT', 'INSERT', 'UPDATE')
ORDER BY 
    TIMESTAMP DESC;

------------------------------------------------------------------------------------------------------

-- CAU 3
-- 3A

-- Cấp vai trò NV_PKT cho user
GRANT NV_PKT TO USER_PKT;
GRANT NV_TCHC TO USER_SV;


BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'ATBMCQ_ADMIN',
    object_name     => 'DANGKY',
    policy_name     => 'FGA_AUDIT_UPDATE_NOT_NVPKT',
    audit_column    => 'DIEMTH, DIEMQT, DIEMCK, DIEMTK',
    audit_condition => 'SYS_CONTEXT(''USERENV'', ''SESSION_USER'') <> ''NV009''',
    statement_types => 'UPDATE',
    enable          => TRUE
  );
END;



BEGIN
  DBMS_FGA.DROP_POLICY(
    object_schema => 'ATBMCQ_ADMIN',
    object_name   => 'DANGKY',
    policy_name   => 'FGA_AUDIT_UPDATE_NOT_NVPKT'
  );
END;



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
    audit_condition => 'SYS_CONTEXT(''USERENV'', ''SESSION_USER'') <> ''USER_SV''',
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
    audit_condition => 'SYS_CONTEXT(''USERENV'', ''SESSION_USER'') NOT IN (''USER_TCHC1'', ''USER_TCHC2'')',
    statement_types => 'UPDATE',
    enable          => TRUE
  );
END;
/

BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'ATBMCQ_ADMIN',
    object_name     => 'NHANVIEN',
    policy_name     => 'FGA_UPD_NHANVIEN_NOT_NVTCHC',
    audit_condition => 'SYS_CONTEXT(''USERENV'', ''SESSION_USER'') <> ''USER_SV''',
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