package atbmhttt.atbmcq_16.admin.Models;

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

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getPrivilege() {
        return privilege.get();
    }

    public void setPrivilege(String privilege) {
        this.privilege.set(privilege);
    }

    public String getObject() {
        return object.get();
    }

    public void setObject(String object) {
        this.object.set(object);
    }

    public String getColumn() {
        return column.get();
    }

    public void setColumn(String column) {
        this.column.set(column);
    }

    public String getGrantOption() {
        return grantOption.get();
    }

    public void setGrantOption(String grantOption) {
        this.grantOption.set(grantOption);
    }
}