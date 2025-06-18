-- Đăng nhập với ADMIN_OLS/123 để thực hiện
-- Lấy toàn bộ thông báo
CREATE OR REPLACE FUNCTION GET_ALL_THONGBAO
RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT * FROM ADMIN_OLS.THONGBAO;
    RETURN v_cursor;
END;
/

-- phát tán thông báo UI
-- Tạo thủ tục INSERT_THONGBAO 
CREATE OR REPLACE PROCEDURE INSERT_THONGBAO (
    p_noidung  IN VARCHAR2,
    p_dinhdanh IN VARCHAR2
)
AS
    v_level     VARCHAR2(50);
    v_comp      VARCHAR2(50);
    v_group     VARCHAR2(50);
    v_label     NUMBER;
    v_id        NUMBER;
BEGIN
    SELECT THONGBAO_SEQ.NEXTVAL INTO v_id FROM DUAL;
    
    -- Lấy nhãn từ bảng map theo DINHDANH
    SELECT LEVEL_No, COMPARTMENT, GROUPS
    INTO v_level, v_comp, v_group
    FROM THONGBAO_LABEL_MAP
    WHERE DINHDANH = p_dinhdanh;

    DBMS_OUTPUT.PUT_LINE('Nhận được nhãn: ' || v_level || ':' || v_comp || ':' || v_group);

    -- Tạo nhãn OLS_LABEL
    v_label := CHAR_TO_LABEL('THONGBAO_POLICY', v_level || ':' || v_comp || ':' || v_group);
    DBMS_OUTPUT.PUT_LINE('Nhãn numeric: ' || TO_CHAR(v_label));

    -- Chèn vào bảng THONGBAO
    INSERT INTO THONGBAO (ID, NOIDUNG, DINHDANH, OLS_LABEL)
    VALUES (v_id, p_noidung, p_dinhdanh, v_label);

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Insert thành công với ID=' || v_id);

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Không tìm thấy nhãn trong THONGBAO_LABEL_MAP với DINHDANH = ' || p_dinhdanh);
        RAISE;  -- Nâng lỗi lên để dễ phát hiện
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Lỗi: ' || SQLERRM);
        RAISE;  -- Nâng lỗi lên để dễ phát hiện
END;
/

-- Gán nhãn cho người trên UI
-- Gán nhãn cho user, này cho admin dùng
CREATE OR REPLACE PROCEDURE SET_OLS_LABEL_FOR_USER(
    p_username IN VARCHAR2,     -- tên người dùng trong hệ thống
    p_dinhdanh IN VARCHAR2      -- mã nhãn từ bảng ánh xạ (VD: 'u1', 'u2'...)
) IS
    v_level   VARCHAR2(50);
    v_comp    VARCHAR2(50);
    v_group   VARCHAR2(50);
    v_label   VARCHAR2(150);
BEGIN
    -- Lấy thông tin nhãn từ bảng ánh xạ theo DINHDANH (vd: 'u1')
    SELECT LEVEL_NO, COMPARTMENT, GROUPS
    INTO v_level, v_comp, v_group
    FROM USER_DINHDANH_LABEL_MAP
    WHERE DINHDANH = p_dinhdanh;

    -- Ghép thành chuỗi nhãn: LEVEL:COMPARTMENT:GROUP
    v_label := v_level || ':' || v_comp || ':' || v_group;

    -- Gán nhãn cho user
    SA_USER_ADMIN.SET_USER_LABELS('THONGBAO_POLICY', p_username, v_label);
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Không tìm thấy nhãn với DINHDANH: ' || p_dinhdanh);
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Lỗi: ' || SQLERRM);
END;
/

-- gọi tương ứng với các u1-u8 đế lấy name lên UI
CREATE OR REPLACE FUNCTION GET_ALL_USER_LABELS
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
    OPEN rc FOR
        SELECT DINHDANH, NAME
        FROM USER_DINHDANH_LABEL_MAP;
    RETURN rc;
END;
/


CREATE OR REPLACE FUNCTION GET_ALL_LABELS
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
    OPEN rc FOR
        SELECT * FROM THONGBAO_LABEL_MAP;
    RETURN rc;
END;
/


CREATE OR REPLACE FUNCTION GET_USER_LIST
RETURN SYS_REFCURSOR
IS
    user_cursor SYS_REFCURSOR;
BEGIN
    OPEN user_cursor FOR
        SELECT USERNAME
        FROM DBA_USERS
        WHERE ORACLE_MAINTAINED = 'N';

    RETURN user_cursor;
END;
