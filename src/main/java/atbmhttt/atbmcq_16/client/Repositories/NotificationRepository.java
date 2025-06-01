package atbmhttt.atbmcq_16.client.Repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    public static class Notification {
        public int id;
        public String noidung; // nội dung thông báo

        public Notification(int id, String noidung) {
            this.id = id;
            this.noidung = noidung;
        }
    }

    public static List<Notification> getAllNotifications(Connection conn) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        CallableStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareCall("{ ? = call ADMIN_OLS.GET_ALL_THONGBAO }", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.registerOutParameter(1, Types.REF_CURSOR);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(1);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String noidung = rs.getString("NOIDUNG");
                notifications.add(new Notification(id, noidung));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }
        return notifications;
    }
}
