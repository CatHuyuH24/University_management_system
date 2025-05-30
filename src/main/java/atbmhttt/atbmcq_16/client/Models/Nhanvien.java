package atbmhttt.atbmcq_16.client.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Nhanvien {
    private final StringProperty manv = new SimpleStringProperty();
    private final StringProperty hoten = new SimpleStringProperty();
    private final StringProperty phai = new SimpleStringProperty();
    private final StringProperty ngsinh = new SimpleStringProperty();
    private final DoubleProperty luong = new SimpleDoubleProperty();
    private final DoubleProperty phucap = new SimpleDoubleProperty();
    private final StringProperty dt = new SimpleStringProperty();
    private final StringProperty vaitro = new SimpleStringProperty();
    private final StringProperty madv = new SimpleStringProperty();

    public Nhanvien(String manv, String hoten, String phai, String ngsinh, double luong,
            double phucap, String dt, String vaitro, String madv) {
        this.manv.set(manv);
        this.hoten.set(hoten);
        this.phai.set(phai);
        this.ngsinh.set(ngsinh);
        this.luong.set(luong);
        this.phucap.set(phucap);
        this.dt.set(dt);
        this.vaitro.set(vaitro);
        this.madv.set(madv);
    }

    public StringProperty manvProperty() {
        return manv;
    }

    public StringProperty hotenProperty() {
        return hoten;
    }

    public StringProperty phaiProperty() {
        return phai;
    }

    public StringProperty ngsinhProperty() {
        return ngsinh;
    }

    public DoubleProperty luongProperty() {
        return luong;
    }

    public DoubleProperty phucapProperty() {
        return phucap;
    }

    public StringProperty dtProperty() {
        return dt;
    }

    public StringProperty vaitroProperty() {
        return vaitro;
    }

    public StringProperty madvProperty() {
        return madv;
    }

    public String getManv() {
        return manv.get();
    }

    public String getHoten() {
        return hoten.get();
    }

    public String getPhai() {
        return phai.get();
    }

    public String getNgsinh() {
        return ngsinh.get();
    }

    public double getLuong() {
        return luong.get();
    }

    public double getPhucap() {
        return phucap.get();
    }

    public String getDt() {
        return dt.get();
    }

    public String getVaitro() {
        return vaitro.get();
    }

    public String getMadv() {
        return madv.get();
    }

    // Thêm các setter để tránh lỗi khi controller gọi setXXX
    public void setManv(String manv) {
        this.manv.set(manv);
    }

    public void setHoten(String hoten) {
        this.hoten.set(hoten);
    }

    public void setPhai(String phai) {
        this.phai.set(phai);
    }

    public void setNgsinh(String ngsinh) {
        this.ngsinh.set(ngsinh);
    }

    public void setLuong(double luong) {
        this.luong.set(luong);
    }

    public void setPhucap(double phucap) {
        this.phucap.set(phucap);
    }

    public void setDt(String dt) {
        this.dt.set(dt);
    }

    public void setVaitro(String vaitro) {
        this.vaitro.set(vaitro);
    }

    public void setMadv(String madv) {
        this.madv.set(madv);
    }
}
