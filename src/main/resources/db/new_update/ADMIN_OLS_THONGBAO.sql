-- Đăng nhập với ADMIN_OLS/123 để thực hiện
--B3: BẮT ĐẦU TẠO CHÍNH SÁCH OLS (CONNECT VÀO TÀI KHOẢN ADMIN_OLS)
-- TẠO CHÍNH SÁCH OLS (KHỞI ĐỘNG LẠI SQLDEV ĐỂ CẬP NHẬT OLS ENABLE)
-- TẠO CHÍNH SÁCH OLS
BEGIN
  SA_SYSDBA.CREATE_POLICY(
    policy_name     => 'THONGBAO_POLICY',
    column_name     => 'OLS_LABEL',
    default_options => 'READ_CONTROL,WRITE_CONTROL,LABEL_DEFAULT'
  );
END;
/

--ENABLE POLICY VỪA TẠO -> KHOI DONG LẠI SQLDEV
EXEC SA_SYSDBA.ENABLE_POLICY ('THONGBAO_POLICY');

-- Kết nối lại với ADMIN_OLS_THONGBAO
-- TẠO NHÃN (LABELS)
-- Gán LEVELS
EXEC SA_COMPONENTS.CREATE_LEVEL('THONGBAO_POLICY', 10, 'SINHVIEN',   'Sinh viên');
EXEC SA_COMPONENTS.CREATE_LEVEL('THONGBAO_POLICY', 20, 'NHANVIEN',   'Nhân viên');
EXEC SA_COMPONENTS.CREATE_LEVEL('THONGBAO_POLICY', 30, 'TRUONGDV','Trưởng đơn vị');

-- Gán COMPARTMENTS (Chuyên môn)
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 10,  'TOAN',  'Khoa Toán');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 20,  'HOA',  'Khoa Hóa');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 30,  'LY',   'Khoa Lý');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 40,  'HC',   'Hành chính');

-- Gán GROUPS (Cơ sở)
EXEC SA_COMPONENTS.CREATE_GROUP('THONGBAO_POLICY', 10,  'COSO1', 'Cơ sở 1');
EXEC SA_COMPONENTS.CREATE_GROUP('THONGBAO_POLICY', 20,  'COSO2', 'Cơ sở 2');

-- Mỗi định danh người dùng gán nhãn
CREATE TABLE USER_DINHDANH_LABEL_MAP (
    DINHDANH    VARCHAR2(10),
    NAME        VARCHAR2(200),
    LEVEL_NO    VARCHAR2(50),
    COMPARTMENT VARCHAR2(50),
    GROUPS      VARCHAR2(50)
);
ALTER TABLE USER_DINHDANH_LABEL_MAP ADD CONSTRAINT uniq_dinhdanh UNIQUE (DINHDANH);
-- Thêm dữ liệu 
-- u1: Trưởng đơn vị có thể đọc được toàn bộ thông báo
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u1','Trưởng đơn vị có thể đọc được toàn bộ thông báo', 'TRUONGDV', 'TOAN,HOA,LY,HC', 'COSO1,COSO2');    
-- u2: Trưởng đơn vị phụ trách khoa Hóa tại cơ sở 2
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u2','Trưởng đơn vị phụ trách khoa Hóa tại cơ sở 2', 'TRUONGDV', 'HOA', 'COSO2');    
-- u3: Trưởng đơn vị phụ trách khoa Lý tại cơ sở 2
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u3','Trưởng đơn vị phụ trách khoa Lý tại cơ sở 2', 'TRUONGDV', 'LY', 'COSO2');
-- u4: Nhân viên thuộc khoa Hóa tại cơ sở 2       
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u4','Nhân viên thuộc khoa Hóa tại cơ sở 2  ', 'NHANVIEN', 'HOA', 'COSO2');      
-- u5: Sinh viên khoa Hóa tại cơ sở 2
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u5','Sinh viên khoa Hóa tại cơ sở 2', 'SINHVIEN', 'HOA', 'COSO2');     
-- u6: Trưởng đơn vị có thể đọc được các thông báo về Hành chính
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u6','Trưởng đơn vị có thể đọc được các thông báo về Hành chính', 'TRUONGDV', 'HC', 'COSO1,COSO2');  
-- u7: Nhân viên có thể đọc toàn bộ thông báo dành cho tất cả nhân viên     
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u7','Nhân viên có thể đọc toàn bộ thông báo dành cho tất cả nhân viên', 'NHANVIEN', 'TOAN,HOA,LY,HC', 'COSO1,COSO2');    
-- u8: Nhân viên có ,thể đọc thông báo về Hành chính tại cơ sở 1
INSERT INTO USER_DINHDANH_LABEL_MAP VALUES ('u8','Nhân viên có ,thể đọc thông báo về Hành chính tại cơ sở 1', 'NHANVIEN', 'HC', 'COSO1');   

--B5: TẠO BẢNG DỮ LIỆU CHỨA ThÔNG BÁO
CREATE TABLE THONGBAO (
    ID        NUMBER(10) PRIMARY KEY,
    NOIDUNG   VARCHAR2(1000),
    DINHDANH    VARCHAR2(5)
);
CREATE SEQUENCE THONGBAO_SEQ START WITH 1 INCREMENT BY 1;


--CẬP NHẬT NHÃN TRONG BẢNG
BEGIN
    SA_POLICY_ADMIN.APPLY_TABLE_POLICY (
    POLICY_NAME => 'THONGBAO_POLICY',
    SCHEMA_NAME => 'ADMIN_OLS',
    TABLE_NAME => 'THONGBAO',
    TABLE_OPTIONS => 'NO_CONTROL'
);
END;

-- TẠO NHÃN
BEGIN
  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 1000, 'TRUONGDV', TRUE);
  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 1001, 'TRUONGDV:HOA:COSO1', TRUE);
  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 1002, 'TRUONGDV:HOA:COSO1,COSO2', TRUE);

  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 2000, 'NHANVIEN', TRUE);

  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 3000, 'SINHVIEN', TRUE);
  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 3001, 'SINHVIEN:HOA:COSO1', TRUE);
  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 3002, 'SINHVIEN:HOA:COSO2', TRUE);
  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 3003, 'SINHVIEN:HOA:COSO1,COSO2', TRUE);
  SA_LABEL_ADMIN.CREATE_LABEL('THONGBAO_POLICY', 3004, 'SINHVIEN::COSO1,COSO2', TRUE);
END;
/

---- Nhãn định danh t1–t9 cho bảng THONGBAO
CREATE TABLE THONGBAO_LABEL_MAP (
    ID        NUMBER(10) PRIMARY KEY,
    DINHDANH    VARCHAR2(5),
    NAME_LABEL    VARCHAR2(200),
    LEVEL_No   VARCHAR2(50),
    COMPARTMENT VARCHAR2(50),
    GROUPS      VARCHAR2(50)
);  
ALTER TABLE THONGBAO_LABEL_MAP ADD CONSTRAINT uniq_dinhdanh_tb UNIQUE (DINHDANH);

-- t1: Cần phát tán đến tất cả trưởng đơn vị
INSERT INTO THONGBAO_LABEL_MAP VALUES (1,'t1','Cần phát tán đến tất cả trưởng đơn vị', 'TRUONGDV', '', '');
-- t2: Cần phát tán đến tất cả nhân viên
INSERT INTO THONGBAO_LABEL_MAP VALUES (2,'t2','Cần phát tán đến tất cả nhân viên', 'NHANVIEN', '', '');
-- t3: Cần phát tán đến tất cả sinh viên
INSERT INTO THONGBAO_LABEL_MAP VALUES (3,'t3', 'Cần phát tán đến tất cả sinh viên','SINHVIEN', '', '');
-- t4: Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 1
INSERT INTO THONGBAO_LABEL_MAP VALUES (4, 't4','Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 1', 'SINHVIEN', 'HOA', 'COSO1');     
-- t5: Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 2
INSERT INTO THONGBAO_LABEL_MAP VALUES (5, 't5',' Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 2', 'SINHVIEN', 'HOA', 'COSO2');        
-- t6: Cần phát tán đến sinh viên thuộc khoa Hóa ở cả hai cơ sở
INSERT INTO THONGBAO_LABEL_MAP VALUES (6, 't6','Cần phát tán đến sinh viên thuộc khoa Hóa ở cả hai cơ sở', 'SINHVIEN', 'HOA', 'COSO1,COSO2');
-- t7: Cần phát tán đến tất cả sinh viên thuộc cả hai cơ sở
INSERT INTO THONGBAO_LABEL_MAP VALUES (7, 't7','Cần phát tán đến tất cả sinh viên thuộc cả hai cơ sở', 'SINHVIEN', '', 'COSO1,COSO2');
-- t8: Cần phát tán đến trưởng khoa Hóa ở cơ sở 1
INSERT INTO THONGBAO_LABEL_MAP VALUES (8, 't8','Cần phát tán đến trưởng khoa Hóa ở cơ sở 1', 'TRUONGDV', 'HOA', 'COSO1');
-- t9: Cần phát tán đến trưởng khoa Hóa ở cơ sở 1 và cơ sở 2
INSERT INTO THONGBAO_LABEL_MAP VALUES (9, 't9','Cần phát tán đến trưởng khoa Hóa ở cơ sở 1 và cơ sở 2', 'TRUONGDV', 'HOA', 'COSO1,COSO2');


-- Chạy Script AMIN_OLS_THONGBAO_PROC trước
-- Chèn dữ liệu test theo yêu cầu vào bảng THONGBAO
BEGIN
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến tất cả trưởng đơn vị', 't1');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến tất cả nhân viên', 't2');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến tất cả sinh viên', 't3');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 1', 't4');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 2', 't5');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến sinh viên thuộc khoa Hóa ở cả hai cơ sở', 't6');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến tất cả sinh viên thuộc cả hai cơ sở', 't7');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến trưởng khoa Hóa ở cơ sở 1', 't8');
    ADMIN_OLS.INSERT_THONGBAO( 'Cần phát tán đến trưởng khoa Hóa ở cơ sở 1 và cơ sở 2', 't9');        
END;
/

BEGIN
    SA_POLICY_ADMIN.REMOVE_TABLE_POLICY('THONGBAO_POLICY','ADMIN_OLS','THONGBAO');
END;
--B9: ÁP DỤNG OLS VÀO BẢNG 
BEGIN
SA_POLICY_ADMIN.APPLY_TABLE_POLICY (
    policy_name => 'THONGBAO_POLICY',
    schema_name => 'ADMIN_OLS',
    table_name => 'THONGBAO',
    table_options => 'READ_CONTROL, WRITE_CONTROL',
    predicate => NULL
);
END;

COMMIT;


// Đăng nhập với ATBMCQ_ADMIN/123
-- Tạo user test
CREATE USER NV001 IDENTIFIED BY 1;
CREATE USER NV006 IDENTIFIED BY 1;

-- Gán quyền kết nối user test
GRANT CONNECT,RESOURCE TO NV001,NV006;
GRANT EXECUTE ON ATBMCQ_ADMIN.IS_ADMIN_USER TO NV001,NV006, ADMIN_OLS;

-- Điều chỉnh để Test gán nhãn ( Test chỉ ADMIN_OLS có quyền )
-- Chỉnh lại IS_ADMIN_USER cho ADMIN_OLS vào giao diện PH2
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

-- Grant quyền test cho user NV001, NV006
GRANT EXECUTE ON ADMIN_OLS.GET_ALL_THONGBAO TO NV001,NV006;
GRANT EXECUTE ON ADMIN_OLS.GET_ALL_LABELS TO NV001,NV006;
GRANT EXECUTE ON ADMIN_OLS.INSERT_THONGBAO TO  NV001,NV006;
GRANT EXECUTE ON ADMIN_OLS.GET_USER_LIST TO NV001,NV006;
GRANT EXECUTE ON ADMIN_OLS.SET_OLS_LABEL_FOR_USER TO NV001,NV006;
GRANT EXECUTE ON ADMIN_OLS.GET_ALL_USER_LABELS TO NV001,NV006;


SELECT * FROM THONGBAO;


// Đăng nhập với SYS/12345
GRANT SELECT ON SYS.DBA_USERS TO  NV001,NV006;


BEGIN
  SA_SYSDBA.DISABLE_POLICY('THONGBAO_POLICY');
END;
/

BEGIN
  SA_SYSDBA.DROP_POLICY('THONGBAO_POLICY');
END;
/
DROP TABLE USER_DINHDANH_LABEL_MAP;
DROP TABLE THONGBAO;
DROP TABLE THONGBAO_LABEL_MAP;
DROP USER ADMIN_OLS CASCADE;
DROP USER NV001 CASCADE;
DROP USER NV006 CASCADE;
DROP USER U1 CASCADE;
DROP USER U1 CASCADE;
DROP USER U1 CASCADE;
DROP USER U1 CASCADE;
DROP USER U1 CASCADE;
DROP USER U1 CASCADE;


SELECT * FROM THONGBAO;
