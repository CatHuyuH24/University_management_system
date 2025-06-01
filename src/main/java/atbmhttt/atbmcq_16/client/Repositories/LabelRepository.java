package atbmhttt.atbmcq_16.client.Repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabelRepository {
    public static class LabelInfo {
        public String id;
        public String dinhdanh;
        public String nameLabel;

        public LabelInfo(String id, String dinhdanh, String nameLabel) {
            this.id = id;
            this.dinhdanh = dinhdanh;
            this.nameLabel = nameLabel;
        }
    }

    public static List<LabelInfo> getAllLabels(Connection conn) throws SQLException {
        List<LabelInfo> labels = new ArrayList<>();
        CallableStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareCall("{ ? = call ADMIN_OLS.GET_ALL_LABELS }", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.registerOutParameter(1, Types.REF_CURSOR);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(1);
            while (rs.next()) {
                String id = rs.getString("ID");
                String dinhdanh = rs.getString("DINHDANH");
                String nameLabel = rs.getString("NAME_LABEL");
                labels.add(new LabelInfo(id, dinhdanh, nameLabel));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
        return labels;
    }

    public static void insertNotification(Connection conn, String noidung, String dinhdanh) throws SQLException {
        CallableStatement stmt = null;
        try {
            stmt = conn.prepareCall("{ call ADMIN_OLS.INSERT_THONGBAO(?, ?) }");
            stmt.setString(1, noidung);
            stmt.setString(2, dinhdanh);
            stmt.execute();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }
}
