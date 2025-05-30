package atbmhttt.atbmcq_16;

public class MonHoc {
    private String mamm, mahp, magv;
    private int hk, nam;

    public MonHoc(String mamm, String mahp, String magv, int hk, int nam) {
        this.mamm = mamm;
        this.mahp = mahp;
        this.magv = magv;
        this.hk = hk;
        this.nam = nam;
    }

    public String getMamm() { return mamm; }
    public String getMahp() { return mahp; }
    public String getMagv() { return magv; }
    public int getHk() { return hk; }
    public int getNam() { return nam; }
}
