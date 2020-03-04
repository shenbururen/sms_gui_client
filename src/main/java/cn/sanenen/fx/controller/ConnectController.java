package cn.sanenen.fx.controller;

import cn.hutool.core.util.ReflectUtil;
import cn.sanenen.fx.common.Global;
import cn.sanenen.fx.handler.MessageReceiveHandler;
import cn.sanenen.fx.service.AtomicUtil;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Data;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@FXMLController
@Data
public class ConnectController implements Initializable {
    
    public Button connect;
    public Button disConnect;
    
    @Resource
    private ConnectParamController paramController;
    @Resource
    private SendController sendController;
    
    @Resource
    private MessageReceiveHandler messageReceiveHandler;

    private static final EndpointManager manager = EndpointManager.INS;

    /**
     * 连接按钮触发事件
     */
    public void connect(){
        //将填写的属性持久化到文件
        Field[] fields = ReflectUtil.getFields(ConnectParamController.class);
        for (Field field : fields) {
            TextField textField = (TextField) ReflectUtil.getFieldValue(paramController, field.getName());
            Global.put(field.getName(),textField.getText());
        }
        Global.save();

        CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
        client.setId(paramController.username.getText());
        client.setHost(paramController.ip.getText());
        client.setPort(Integer.parseInt(paramController.getPort().getText()));
        client.setUserName(paramController.username.getText());
        client.setPassword(paramController.pwd.getText());
        client.setServiceId(paramController.serviceId.getText());
        client.setSpCode(paramController.spNumber.getText());
        client.setMaxChannels(Short.parseShort(paramController.connectNum.getText()));
        client.setVersion((short) 0x20);
        client.setWriteLimit(Integer.parseInt(paramController.speed.getText()));
        client.setGroupName("test");
        client.setChartset(StandardCharsets.UTF_8);
        client.setRetryWaitTimeSec((short) 30);
        client.setUseSSL(false);
        client.setMaxRetryCnt((short) 0);
        client.setReSendFailMsg(false);
        client.setSupportLongmsg(EndpointEntity.SupportLongMessage.BOTH);
        List<BusinessHandlerInterface> list = new ArrayList<>();
        list.add(messageReceiveHandler);
        // list.add( new SessionConnectedHandler());
        client.setBusinessHandlerSet(list);

        input(false);
        
        manager.addEndpointEntity(client);
        for (int i = 0; i < client.getMaxChannels(); i++) {
            manager.openEndpoint(client);
        }
    }
    public void disConnect(){
        // 连接断开
        manager.close();
        input(true);
        sendController.setCanSend(false);
        sendController.setSendLastNum(0);
        sendController.setResponseLastNum(0);
        AtomicUtil.clear();
        connect.setDisable(false);
        sendController.getSend().setDisable(true);
        
    }

    private void input(boolean input) {
        Field[] fields = ReflectUtil.getFields(ConnectParamController.class);
        for (Field field : fields) {
            TextField textField = (TextField) ReflectUtil.getFieldValue(paramController, field.getName());
            textField.setEditable(input);
        }
    }
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    
}
