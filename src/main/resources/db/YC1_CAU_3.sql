
-- TẠO NGƯỜI DÙNG CHUYÊN PHỤ TRÁCH VPD
CREATE USER VPD_MGR IDENTIFIED BY 999321;
GRANT CREATE SESSION TO VPD_MGR;
DROP USER VPD_MGR

GRANT CREATE PROCEDURE TO VPD_MGR;
GRANT EXECUTE ON DBMS_RLS TO VPD_MGR;
GRANT EXECUTE ANY PROCEDURE TO VPD_MGR;
GRANT EXECUTE ON DBMS_SESSION TO VPD_MGR;
GRANT EXECUTE ON DBMS_APPLICATION_INFO TO VPD_MGR;
GRANT DROP ANY PROCEDURE TO VPD_MGR;
GRANT UNLIMITED TABLESPACE TO VPD_MGR;

-- XÂY DỰNG HÀM VỊ TỪ XEM SINHVIEN
CREATE OR REPLACE FUNCTION PF_SINHVIEN_SELECT(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_username VARCHAR2(40);
  v_temp     VARCHAR2(40);
  v_count    NUMBER;
BEGIN
    v_username := SYS_CONTEXT('userenv', 'SESSION_USER');
    
    -- Exclude VPD_MGR from policy restrictions
--    IF v_username = 'VPD_MGR' THEN
--        RETURN '0=0'; -- No restriction
--    END IF;

    IF EXISTS (SELECT 1 FROM ATBMCQ_ADMIN.SINHVIEN WHERE MASV = v_username) THEN
      RETURN '100 = 100';
    ELSE
      RETURN '100 = 1';
    END IF;

--    SELECT VAITRO INTO v_temp FROM ATBMCQ_ADMIN.NHANVIEN WHERE MANV = v_username;
--    IF v_temp = 'GV' THEN
--      SELECT D.MADV INTO v_temp
--      FROM ATBMCQ_ADMIN.DONVI D
--      JOIN ATBMCQ_ADMIN.NHANVIEN N ON D.MADV = N.MADV
--      WHERE N.MANV = v_username;
--      RETURN 'KHOA = ''' || v_temp || '''';
--    ELSE
--      RETURN '0 = 1';
--    END IF;
    return '3=100';
END;
/
SET SERVEROUTPUT ON;

DECLARE
  res VARCHAR2(200);
BEGIN
  res := PF_SINHVIEN_SELECT('ATBMCQ_ADMIN', 'SINHVIEN');
  DBMS_OUTPUT.PUT_LINE('Policy Result: ' || res);
END;
/


-- ÁP DỤNG HÀM VỊ TỪ
BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_SELECT',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINHVIEN_SELECT',
    statement_types  => 'SELECT'
  );
END;


-- XÂY DỰNG HÀM VỊ TỪ CHO VIỆC CẬP NHẬT DIACHI, DT
CREATE OR REPLACE FUNCTION PF_SINVIEN_UPDATE_DCHI_DT(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_username VARCHAR2(40);
    v_temp VARCHAR2(100);
BEGIN
    v_username := SYS_CONTEXT('userenv', 'SESSION_USER');
    SELECT MASV INTO v_temp FROM SINVIEN WHERE MASV = v_username;
    IF v_temp IS NOT NULL THEN
        RETURN 'MASV = ''' || v_username || '''';
    END IF;
    
    SELECT VAITRO INTO v_temp FROM NHANVIEN WHERE MANV = v_username;
    IF v_temp = 'NV PCTSV' THEN
        RETURN '1 = 1';
    ELSE
        RETURN '0 = 1';
    END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN '0 = 1';
    END;
END;

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_UPDATE_DCHI_DT',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINVIEN_UPDATE_DCHI_DT',
    statement_types  => 'UPDATE',
    sec_relevant_cols => 'DCHI,DT'
  );
END;


-- HÀM VỊ TỪ CHO THÊM, SỬA (MỌI CỘT TRỪ TINHTRANG), XÓA
CREATE OR REPLACE FUNCTION PF_SINHVIEN_INSERT_UPDATE_DELETE(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_username VARCHAR2(40);
    v_temp VARCHAR2(100);
BEGIN
    v_username := SYS_CONTEXT('userenv', 'SESSION_USER');
    SELECT VAITRO INTO v_temp FROM NHANVIEN WHERE MANV = v_username;
    
    IF v_temp = 'NV PCTSV' THEN
        RETURN '1 = 1';
    ELSE
        RETURN '0 = 1';
    END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN '0 = 1';
    END;
END;

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_INSERT_UPDATE_DELETE',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINHVIEN_INSERT_UPDATE_DELETE',
    statement_types  => 'INSERT, UPDATE, DELETE',
    sec_relevant_cols => 'MASV, HOTEN, PHAI, NGSINH, DCHI, DT, KHOA'
  );
END;

BEGIN
    DBMS_RLS.DROP_POLICY(
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_SELECT'
    );
END;
/

SELECT * FROM ATBMCQ_ADMIN.NHANVIEN;
SELECT * FROM ATBMCQ_ADMIN.DONVI;
SELECT * FROM ATBMCQ_ADMIN.SINHVIEN;
