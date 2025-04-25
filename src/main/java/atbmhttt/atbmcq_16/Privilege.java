package atbmhttt.atbmcq_16;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Privilege {
    private final StringProperty type;
    private final StringProperty privilege;
    private final StringProperty object;
    private final StringProperty column;
    private final StringProperty grantOption;

    public Privilege(String type, String privilege, String object, String column, String grantOption) {
        this.type = new SimpleStringProperty(type);
        this.privilege = new SimpleStringProperty(privilege);
        this.object = new SimpleStringProperty(object);
        this.column = new SimpleStringProperty(column);
        this.grantOption = new SimpleStringProperty(grantOption);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty privilegeProperty() {
        return privilege;
    }

    public StringProperty objectProperty() {
        return object;
    }

    public StringProperty columnProperty() {
        return column;
    }

    public StringProperty grantOptionProperty() {
        return grantOption;
    }
}