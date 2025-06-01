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

-- TẠO NHÃN (LABELS)
-- Gán LEVELS

EXEC SA_COMPONENTS.CREATE_LEVEL('THONGBAO_POLICY', 5, 'PUBLIC',   'Mọi người');
EXEC SA_COMPONENTS.CREATE_LEVEL('THONGBAO_POLICY', 10, 'SINHVIEN',   'Sinh viên');
EXEC SA_COMPONENTS.CREATE_LEVEL('THONGBAO_POLICY', 20, 'NHANVIEN',   'Nhân viên');
EXEC SA_COMPONENTS.CREATE_LEVEL('THONGBAO_POLICY', 30, 'TRUONGDV','Trưởng đơn vị');

-- Gán COMPARTMENTS (Chuyên môn)
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 10,  'TOAN',  'Khoa Toán');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 20,  'HOA',  'Khoa Hóa');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 30,  'LY',   'Khoa Lý');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 40,  'HC',   'Hành chính');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 50,  'ALL',   'Mọi Khoa');

-- Gán GROUPS (Cơ sở)
EXEC SA_COMPONENTS.CREATE_GROUP('THONGBAO_POLICY', 10,  'COSO1', 'Cơ sở 1');
EXEC SA_COMPONENTS.CREATE_GROUP('THONGBAO_POLICY', 20,  'COSO2', 'Cơ sở 2');
EXEC SA_COMPONENTS.CREATE_COMPARTMENT('THONGBAO_POLICY', 30,  'ALL',   'Cả 2 Cơ Sở');

-- Mỗi định danh người dùng gán nhãn
CREATE TABLE USER_DINHDANH_LABEL_MAP (
    DINHDANH    VARCHAR2(10),
    NAME        VARCHAR2(200),
    LEVEL_NO    VARCHAR2(50),
    COMPARTMENT VARCHAR2(50),
    GROUPS      VARCHAR2(50)
);


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

--B5: TẠO BẢNG DỮ LIỆU TEST
CREATE TABLE THONGBAO (
    ID        NUMBER(10) PRIMARY KEY,
    NOIDUNG   VARCHAR2(1000),
    DINHDANH    VARCHAR2(5)
);

---- Nhãn định danh cho thông báo t1–t9
CREATE TABLE THONGBAO_LABEL_MAP (
    ID        NUMBER(10) PRIMARY KEY,
    DINHDANH    VARCHAR2(5),
    NAME_LABEL    VARCHAR2(200),
    LEVEL_No   VARCHAR2(50),
    COMPARTMENT VARCHAR2(50),
    GROUPS      VARCHAR2(50)
);  

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

-- Chèn dữ liệu test theo yêu cầu
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (1, 'Cần phát tán đến tất cả trưởng đơn vị', 't1');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (2, 'Cần phát tán đến tất cả nhân viên', 't2');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (3, 'Cần phát tán đến tất cả sinh viên', 't3');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (4, 'Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 1', 't4');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (5, 'Cần phát tán đến sinh viên thuộc khoa Hóa ở cơ sở 2', 't5');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (6, 'Cần phát tán đến sinh viên thuộc khoa Hóa ở cả hai cơ sở', 't6');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (7, 'Cần phát tán đến tất cả sinh viên thuộc cả hai cơ sở', 't7');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (8, 'Cần phát tán đến trưởng khoa Hóa ở cơ sở 1', 't8');
INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH) VALUES (9, 'Cần phát tán đến trưởng khoa Hóa ở cơ sở 1 và cơ sở 2', 't9');

--B7: CẬP NHẬT NHÃN TRONG BẢNG
BEGIN
SA_POLICY_ADMIN.APPLY_TABLE_POLICY (
POLICY_NAME => 'THONGBAO_POLICY',
SCHEMA_NAME => 'ADMIN_OLS',
TABLE_NAME => 'THONGBAO',
TABLE_OPTIONS => 'NO_CONTROL'
);
END;

BEGIN
SA_POLICY_ADMIN.REMOVE_TABLE_POLICY('THONGBAO_POLICY','ADMIN_OLS','THONGBAO');
END;

--B9: ÁP DỤNG OLS VÀO BẢNG
BEGIN
SA_POLICY_ADMIN.REMOVE_TABLE_POLICY('THONGBAO_POLICY','ADMIN_OLS','THONGBAO');
SA_POLICY_ADMIN.APPLY_TABLE_POLICY (
    policy_name => 'THONGBAO_POLICY',
    schema_name => 'ADMIN_OLS',
    table_name => 'THONGBAO',
    table_options => 'READ_CONTROL',
    predicate => NULL
);
END;

commit;

--B10:UPDATE BẢNG THONGBAO (ĐỂ GỌI UPDATE LABEL)
UPDATE THONGBAO
SET NOIDUNG = NOIDUNG;
COMMIT;


-- Grant quyền test cho user NV001
GRANT SELECT ON SYS.DBA_USERS TO NV001;
GRANT EXECUTE ON GET_ALL_THONGBAO TO NV001;
GRANT EXECUTE ON GET_ALL_LABELS TO NV001;
GRANT EXECUTE ON INSERT_THONGBAO TO NV001;
GRANT EXECUTE ON GET_ALL_USER_LABELS TO NV001;
GRANT EXECUTE ON SET_OLS_LABEL_FOR_USER TO NV001;
GRANT EXECUTE ON GET_USER_LIST TO NV001;