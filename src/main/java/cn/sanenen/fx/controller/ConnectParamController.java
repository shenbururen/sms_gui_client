package cn.sanenen.fx.controller;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.sanenen.fx.common.Global;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.Data;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
@Data
public class ConnectParamController implements Initializable {

    public TextField ip;
    public TextField port;
    public TextField username;
    public TextField pwd;
    public TextField serviceId;
    public TextField spNumber;
    public TextField connectNum;
    public TextField speed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Field[] fields = ReflectUtil.getFields(ConnectParamController.class);
        for (Field field : fields) {
            String value = Global.get(field.getName());
            if (StrUtil.isNotBlank(value)){
                TextField textField = (TextField) ReflectUtil.getFieldValue(this, field.getName());
                textField.setText(value);
            }
        }
    }
}
