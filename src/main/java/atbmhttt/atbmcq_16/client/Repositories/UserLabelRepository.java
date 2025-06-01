package atbmhttt.atbmcq_16.client.Repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserLabelRepository {
    public static class UserLabelInfo {
        public String dinhdanh;
        public String name;

        public UserLabelInfo(String dinhdanh, String name) {
            this.dinhdanh = dinhdanh;
            this.name = name;
        }
    }

    public static List<UserLabelInfo> getAllUserLabels(Connection conn) throws SQLException {
        List<UserLabelInfo> labels = new ArrayList<>();
        CallableStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareCall("{ ? = call ADMIN_OLS.GET_ALL_USER_LABELS }", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.registerOutParameter(1, Types.REF_CURSOR);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(1);
            while (rs.next()) {
                String dinhdanh = rs.getString("DINHDANH");
                String name = rs.getString("NAME");
                labels.add(new UserLabelInfo(dinhdanh, name));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
        return labels;
    }

    public static List<String> getUserList(Connection conn) throws SQLException {
        List<String> users = new ArrayList<>();
        CallableStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareCall("{ ? = call ADMIN_OLS.GET_USER_LIST }", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.registerOutParameter(1, Types.REF_CURSOR);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(1);
            while (rs.next()) {
                users.add(rs.getString("USERNAME"));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
        return users;
    }

    public static void setOlsLabelForUser(Connection conn, String username, String dinhdanh) throws SQLException {
        CallableStatement stmt = null;
        try {
            stmt = conn.prepareCall("{ call ADMIN_OLS.SET_OLS_LABEL_FOR_USER(?, ?) }");
            stmt.setString(1, username);
            stmt.setString(2, dinhdanh);
            stmt.execute();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }
}
