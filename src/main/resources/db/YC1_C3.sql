-- XÂY DỰNG HÀM VỊ TỪ XEM SINHVIEN
CREATE OR REPLACE FUNCTION PF_SINHVIEN_SELECT (
  schema_name IN VARCHAR2,
  object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_role VARCHAR2(100);
  v_user VARCHAR2(100);
  v_donvi VARCHAR2(100);
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_role = 'SINHVIEN' THEN
        RETURN 'MASV = ''' || v_user || ''''; 
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
        RETURN '0 = 1';
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
    v_username VARCHAR2(100);
    v_role VARCHAR2(100);
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


-- HÀM VỊ TỪ CHO THÊM, SỬA, XOA (MỌI CỘT TRỪ TINHTRANG, DCHI, DT, MASV)
CREATE OR REPLACE FUNCTION PF_SINHVIEN_INSERT_UPDATE_DELETE(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_role VARCHAR2(100);
    v_user VARCHAR2(100); 
BEGIN
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    v_user := SYS_CONTEXT('userenv', 'SESSION_USER');

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
    sec_relevant_cols => 'HOTEN, PHAI, NGSINH, KHOA',
    update_check => TRUE
  );
END;

-- XỬ LÍ TRUY CẬP VỚI CỘT MASV, VÌ ĐÂY LÀ CỘT ĐỂ ĐỊNH VỊ SINH VIÊN THEO DÒNG CHO NV CTSV, NV PĐT
CREATE OR REPLACE FUNCTION PF_SINHVIEN_MASV_INSERT_DELETE(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_role VARCHAR2(100);
    v_user VARCHAR2(100); 
BEGIN
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    v_user := SYS_CONTEXT('userenv', 'SESSION_USER');

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
    policy_name      => 'ATBMCQ_16_SINHVIEN_MASV_INSERT_DELETE',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINHVIEN_MASV_INSERT_DELETE',
    statement_types  => 'INSERT, DELETE',
    sec_relevant_cols => 'MASV',
    update_check => TRUE
  );
END;



CREATE OR REPLACE FUNCTION PF_SINHVIEN_MASV_UPDATE(
    schema_name IN VARCHAR2,
    object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_role VARCHAR2(100);
    v_user VARCHAR2(100); 
BEGIN
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    v_user := SYS_CONTEXT('userenv', 'SESSION_USER');

    IF v_role = 'NV CTSV' OR v_role = 'NV PĐT' THEN
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
    policy_name      => 'ATBMCQ_16_SINHVIEN_MASV_UPDATE',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINHVIEN_MASV_UPDATE',
    statement_types  => 'UPDATE',
    sec_relevant_cols => 'MASV',
    update_check => TRUE
  );
END;



begin
dbms_rls.drop_policy('ATBMCQ_ADMIN','SINHVIEN','ATBMCQ_16_SINHVIEN_UPDATE_TINHTRANG');
end;

-- GIỚI HẠN VIỆC THAO TÁC CỘT TINHTRANG
CREATE OR REPLACE FUNCTION PF_SINHVIEN_TINHTRANG(
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
    ELSIF v_role = 'NV CTSV' THEN
        RETURN 'TINHTRANG IS NOT NULL';
    ELSE
        RETURN '2 = 0';
    END IF;
END;
/


BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'SINHVIEN',
    policy_name      => 'ATBMCQ_16_SINHVIEN_TINHTRANG',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_SINHVIEN_TINHTRANG',
    statement_types  => 'INSERT, UPDATE, DELETE',
    sec_relevant_cols => 'TINHTRANG',
    update_check => TRUE
  );
END;
