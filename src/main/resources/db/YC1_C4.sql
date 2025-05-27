-- XÂY DỰNG HÀM VỊ TỪ XEM DIEM DANGKY
CREATE OR REPLACE FUNCTION PF_DANGKY_DIEM_SELECT (
  schema_name IN VARCHAR2,
  object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_role VARCHAR2(100);
  v_user VARCHAR2(100);
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_role = 'SINHVIEN' THEN
        RETURN 'MASV = ''' || v_user || '''';
    ELSIF v_role = 'GV' THEN
        RETURN 'EXISTS (SELECT 1 FROM ATBMCQ_ADMIN.MOMON m WHERE m.MAMM = ATBMCQ_ADMIN.DANGKY.MAMM AND m.MAGV = ''' || v_user || ''')';
    ELSE
        RETURN '1 = 0';
    END IF;
END;
/

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'DANGKY',
    policy_name      => 'ATBMCQ_16_DANGKY_DIEM_SELECT',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_DANGKY_DIEM_SELECT',
    statement_types  => 'SELECT',
    sec_relevant_cols => 'DIEMTH, DIEMQT, DIEMCK, DIEMTK',
    sec_relevant_cols_opt => DBMS_RLS.ALL_ROWS
  );
END;

-- XEM THONG TIN DANG KY
CREATE OR REPLACE FUNCTION PF_DANGKY_MASV_MAMM_SELECT (
  schema_name IN VARCHAR2,
  object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_role VARCHAR2(100);
  v_user VARCHAR2(100);
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_role = 'SINHVIEN' THEN
        RETURN 'MASV = ''' || v_user || '''';
    ELSIF v_role = 'GV' THEN
        RETURN 'EXISTS (SELECT 1 FROM ATBMCQ_ADMIN.MOMON m WHERE m.MAMM = ATBMCQ_ADMIN.DANGKY.MAMM AND m.MAGV = ''' || v_user || ''')';
    ELSIF v_role = 'NV PĐT' THEN
        RETURN q'[
            EXISTS (
            SELECT 1
            FROM ATBMCQ_ADMIN.MOMON m
            WHERE m.MAMM = ATBMCQ_ADMIN.DANGKY.MAMM
              AND TO_DATE('01-09-2024', 'DD-MM-YYYY') BETWEEN
                CASE m.HK
                  WHEN 1 THEN TO_DATE('01-09-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 2 THEN TO_DATE('01-01-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 3 THEN TO_DATE('01-05-' || m.NAM, 'DD-MM-YYYY')
                END
                AND
                CASE m.HK
                  WHEN 1 THEN TO_DATE('14-09-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 2 THEN TO_DATE('14-01-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 3 THEN TO_DATE('14-05-' || m.NAM, 'DD-MM-YYYY')
                END
            )
        ]';
        
    ELSIF v_role = 'NV PKT' THEN
        RETURN '1 = 1';
    ELSE
        RETURN '1 = 0';
    END IF;
END;
/


BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'DANGKY',
    policy_name      => 'ATBMCQ_16_DANGKY_MASV_MAMM_SELECT',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_DANGKY_MASV_MAMM_SELECT',
    statement_types  => 'SELECT'
  );
END;

-- INSERT, UPDATE, DELETE DIEM
CREATE OR REPLACE FUNCTION PF_DANGKY_DIEM_INSERT_UPDATE_DELETE (
  schema_name IN VARCHAR2,
  object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_role VARCHAR2(100);
BEGIN
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    
    IF v_role = 'NV PKT' THEN
        RETURN '1 = 1';
    ELSE
        RETURN '1 = 0';
    END IF;
END;
/

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'DANGKY',
    policy_name      => 'ATBMCQ_16_DANGKY_DIEM_INSERT_UPDATE_DELETE',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_DANGKY_DIEM_INSERT_UPDATE_DELETE',
    statement_types  => 'INSERT, UPDATE, DELETE',
    sec_relevant_cols => 'DIEMTH, DIEMQT, DIEMCK, DIEMTK',
    update_check     => TRUE
  );
END;

-- CHỈ INSERT, DELETE
CREATE OR REPLACE FUNCTION PF_DANGKY_MASV_MAMM_INSERT_DELETE (
  schema_name IN VARCHAR2,
  object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_role VARCHAR2(100);
  v_user VARCHAR2(100);
  v_predicate VARCHAR2(2000);
BEGIN
    v_user := SYS_CONTEXT('userenv', 'SESSION_USER');
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    v_predicate := '';
    
    IF v_role = 'SINHVIEN' THEN
        v_predicate := v_predicate || 'MASV = ''' || v_user || ''' AND ';
    END IF;
    
    IF v_role = 'SINHVIEN' or v_role = 'NV PĐT' THEN
        v_predicate := v_predicate || q'[
            EXISTS (
            SELECT 1
            FROM ATBMCQ_ADMIN.MOMON m
            WHERE m.MAMM = ATBMCQ_ADMIN.DANGKY.MAMM
              AND TO_DATE('14-09-2024', 'DD-MM-YYYY') BETWEEN
                CASE m.HK
                  WHEN 1 THEN TO_DATE('01-09-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 2 THEN TO_DATE('01-01-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 3 THEN TO_DATE('01-05-' || m.NAM, 'DD-MM-YYYY')
                END
                AND
                CASE m.HK
                  WHEN 1 THEN TO_DATE('14-09-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 2 THEN TO_DATE('14-01-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 3 THEN TO_DATE('14-05-' || m.NAM, 'DD-MM-YYYY')
                END
            )
        ]';
    ELSE
        v_predicate := v_predicate || '0 = 1';
    END IF;
    
    RETURN v_predicate;
END;
/

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'DANGKY',
    policy_name      => 'ATBMCQ_16_DANGKY_MASV_MAMM_INSERT_DELETE',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_DANGKY_MASV_MAMM_INSERT_DELETE',
    statement_types  => 'INSERT, DELETE',
    sec_relevant_cols => 'MASV, MAMM',
    update_check     => TRUE
  );
END;


-- LIÊN QUAN ĐẾN VIỆC UPDATE TRƯỜNG KHÔNG PHẢI ĐIỂM
CREATE OR REPLACE FUNCTION PF_DANGKY_MASV_MAMM_UPDATE (
  schema_name IN VARCHAR2,
  object_name IN VARCHAR2
)
RETURN VARCHAR2
AS
  v_role VARCHAR2(100);
  v_user VARCHAR2(100);
  v_predicate VARCHAR2(2000);
BEGIN
    v_user := SYS_CONTEXT('userenv', 'SESSION_USER');
    v_role := SYS_CONTEXT('user_ctx', 'VAI_TRO');
    v_predicate := '';
    
    IF v_role = 'SINHVIEN' THEN
        v_predicate := v_predicate || 'MASV = ''' || v_user || ''' AND ';
    END IF;
    
    IF v_role = 'SINHVIEN' or v_role = 'NV PĐT' THEN
        v_predicate := v_predicate || q'[
            EXISTS (
            SELECT 1
            FROM ATBMCQ_ADMIN.MOMON m
            WHERE m.MAMM = ATBMCQ_ADMIN.DANGKY.MAMM
              AND TO_DATE('14-09-2024', 'DD-MM-YYYY') BETWEEN
                CASE m.HK
                  WHEN 1 THEN TO_DATE('01-09-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 2 THEN TO_DATE('01-01-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 3 THEN TO_DATE('01-05-' || m.NAM, 'DD-MM-YYYY')
                END
                AND
                CASE m.HK
                  WHEN 1 THEN TO_DATE('14-09-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 2 THEN TO_DATE('14-01-' || m.NAM, 'DD-MM-YYYY')
                  WHEN 3 THEN TO_DATE('14-05-' || m.NAM, 'DD-MM-YYYY')
                END
            )
        ]';
    ELSIF v_role = 'NV PKT' THEN
        v_predicate := v_predicate || '1 = 1';
    ELSE
        v_predicate := v_predicate || '0 = 1';
    END IF;
    
    RETURN v_predicate;
END;
/

BEGIN
  DBMS_RLS.ADD_POLICY (
    object_schema    => 'ATBMCQ_ADMIN',
    object_name      => 'DANGKY',
    policy_name      => 'ATBMCQ_16_DANGKY_MASV_MAMM_UPDATE',
    function_schema  => 'VPD_MGR',
    policy_function  => 'PF_DANGKY_MASV_MAMM_UPDATE',
    statement_types  => 'UPDATE',
    sec_relevant_cols => 'MASV, MAMM'
  );
END;
