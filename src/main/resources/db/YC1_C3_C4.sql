
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
CREATE OR REPLACE FUNCTION PF_SINHVIEN_SELECT (
  schema_name IN VARCHAR2,
  object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_role VARCHAR2(30);
  v_user VARCHAR2(30);
  v_donvi VARCHAR2(30);
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_user = 'ATBMCQ_ADMIN' OR v_user = 'VPD_MGR' THEN
        RETURN '1 = 1';
    ELSIF v_role = 'SINHVIEN' THEN
        RETURN 'MASV = ''' || v_user || ''''; -- Access only to own record
    ELSIF v_role = 'GV' THEN
        BEGIN
            SELECT MADV INTO v_donvi 
            FROM ATBMCQ_ADMIN.NHANVIEN
            WHERE MANV = v_user;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RETURN '111 = 999';
        END;
        RETURN 'KHOA = ''' || v_donvi || '''';
        
    ELSE
        RETURN '0 = 1'; -- No access
    END IF;
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
    v_role VARCHAR2(40);
BEGIN
    v_username := SYS_CONTEXT('userenv', 'SESSION_USER');
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_role = 'SINHVIEN' THEN
        RETURN 'MASV = ''' || v_username || '''';
    ELSIF v_role = 'NV CTSV' THEN
        RETURN '1 = 1';
    ELSE
        RETURN '0 = 1';
    END IF;
END;

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_UPDATE_DCHI_DT',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINVIEN_UPDATE_DCHI_DT',
    statement_types  => 'UPDATE',
    sec_relevant_cols => 'DCHI,DT',
    update_check => TRUE
  );
END;


-- HÀM VỊ TỪ CHO THÊM, SỬA (MỌI CỘT TRỪ TINHTRANG), XÓA
CREATE OR REPLACE FUNCTION PF_SINHVIEN_INSERT_UPDATE_DELETE(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_role VARCHAR2(40);
BEGIN
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_role = 'NV CTSV' THEN
        RETURN '1 = 1';
    ELSE
        RETURN '0 = 1';
    END IF;
END;
/

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_INSERT_UPDATE_DELETE',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINHVIEN_INSERT_UPDATE_DELETE',
    statement_types  => 'INSERT, UPDATE, DELETE',
    sec_relevant_cols => 'MASV, HOTEN, PHAI, NGSINH, DCHI, DT, KHOA',
    update_check => TRUE
  );
END;

-- GIỚI HẠN VIỆC UDATE CỘT TINHTRANG
CREATE OR REPLACE FUNCTION PF_SINHVIEN_UPDATE_TINHTRANG(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_role VARCHAR2(40);
    v_temp VARCHAR2(40);
BEGIN
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_role = 'NV PĐT' THEN
        RETURN '2 = 2';
    ELSE
        RETURN '2 = 0';
    END IF;
END;
/

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_UPDATE_TINHTRANG',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINHVIEN_UPDATE_TINHTRANG',
    statement_types  => 'UPDATE',
    sec_relevant_cols => 'TINHTRANG',
    update_check => TRUE
  );
END;


BEGIN
    DBMS_RLS.DROP_POLICY(
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_UPDATE_DCHI_DT'
    );
END;
/

SELECT * FROM ATBMCQ_ADMIN.NHANVIEN;
SELECT * FROM ATBMCQ_ADMIN.SINHVIEN;
SELECT MASV 
    FROM ATBMCQ_ADMIN.SINHVIEN
    WHERE MASV = 'SV001';