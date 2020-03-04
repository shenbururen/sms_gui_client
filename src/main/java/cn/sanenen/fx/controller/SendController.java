package cn.sanenen.fx.controller;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.sanenen.fx.common.SignUtil;
import cn.sanenen.fx.service.AtomicUtil;
import cn.sanenen.fx.service.ConvertService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Data;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

@FXMLController
@Data
public class SendController implements Initializable {

    public Label contentCount;
    public TextArea content;
    public TextArea mobile;
    public CheckBox randomContent;
    public CheckBox randomMobile;
    public CheckBox manySend;
    public TextField sendCount;
    public Label sendCountShow;
    public Button send;

    private boolean isCanSend = true;
    private boolean sendComplete = false;
    private long sendLastNum = 0;
    private long responseLastNum = 0;
    private String contentEnd = "00000000";

    @Resource
    private ConnectParamController paramController;
    @Resource
    private SendShowController sendShowController;

    public void send() {
        String contStr = StrUtil.removeSuffix(this.content.getText().trim(), contentEnd);
        String mobile = this.mobile.getText();
        String id = paramController.username.getText();
        boolean randomContent = this.randomContent.isSelected();
        boolean randomMobile = this.randomMobile.isSelected();
        boolean manySend = this.manySend.isSelected();
        if (manySend) {
            new Thread(() -> {
                try {
                    sendComplete = false;
                    send.setDisable(true);
                    int manyCount = Integer.parseInt(sendCount.getText());
                    String tempContent = contStr;
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < manyCount; i++) {
                        if (!isCanSend) {
                            break;
                        }
                        if (randomContent) {// 加随机内容
                            tempContent = contStr + RandomUtil.randomString(8);
                        }
                        if (randomMobile) {// 随机手机号
                            ConvertService.sendSms(ConvertService.getMobile(), tempContent, id);
                            AtomicUtil.sendCount.incrementAndGet();
                        } else {
                            String[] destMobile = mobile.split("[,，]");
                            for (String tmpMobile : destMobile) {
                                if (StrUtil.isNotBlank(tmpMobile)) {
                                    ConvertService.sendSms(tmpMobile, contStr, id);
                                    AtomicUtil.sendCount.incrementAndGet();
                                }
                            }
                        }
                    }
                    sendComplete = true;
                    long end = System.currentTimeMillis();
                    long hs = (end - start) / 1000;
                    long sendSpeed = AtomicUtil.sendCount.get() / Math.max(hs,1);
                    sendShowController.showResult(new Date(start),new Date(end),sendSpeed);
                    Platform.runLater(() -> {
                        updateSpeed(sendSpeed, 0);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                send.setDisable(false);
            }).start();
        } else {
            String[] destMobile = mobile.split("[,，]");
            for (String tmpMobile : destMobile) {
                if (StrUtil.isNotBlank(tmpMobile)) {
                    ConvertService.sendSms(tmpMobile, contStr, id);
                    AtomicUtil.sendCount.incrementAndGet();
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //绑定事件
        bindAction();
        //初始化控件值
        initText();
        CronUtil.setMatchSecond(true);
        CronUtil.schedule("0/1 * * * * ? ", (Runnable) () -> {
            if (sendComplete){
                return;
            }
            long sendNow = AtomicUtil.sendCount.get();
            long sendSpeed = sendNow - sendLastNum;
            sendLastNum = sendNow;
            long responseNow = AtomicUtil.responseCount.get();
            long responseSpeed = responseNow - responseLastNum;
            responseLastNum = responseNow;
            Platform.runLater(() -> {
                updateSpeed(sendSpeed,responseSpeed);
            });
        });
        CronUtil.schedule("0/1 * * * * ? ", (Runnable) () -> {
            Platform.runLater(this::updateShow);
        });
        CronUtil.start();
    }

    private void bindAction() {
        manySend.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                sendCount.setDisable(false);
            } else {
                sendCount.setDisable(true);
            }
        });
        sendCount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StrUtil.isNotBlank(newValue)) {
                if (!NumberUtil.isNumber(newValue)) {
                    sendCount.setText(oldValue);
                    return;
                }
                sendCountShow.setText(NumberChineseFormatter.format(Double.parseDouble(newValue), false));
            } else {
                sendCountShow.setText("");
            }
        });
        content.textProperty().addListener((observable, oldValue, txt) -> {
            contentCount.setText(StrUtil.format("当前字数:{},短信条数:{}", txt.length(), SignUtil.spliteMsg(txt)));
        });

        randomContent.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                content.setText(content.getText() + contentEnd);
            } else {
                String text = content.getText();
                content.setText(StrUtil.removeSuffix(text, contentEnd));
            }
        });
    }

    private void initText() {
        content.setText("【签名】测试短信");
        sendCount.setText("1000");
    }

    private void updateSpeed(long sendSpeed, long responseSpeed) {
        sendShowController.getSubmitSpeed().setText(StrUtil.format("{}/s", sendSpeed));
        sendShowController.getResponseSpeed().setText(StrUtil.format("{}/s", responseSpeed));
    }

    private void updateShow() {
        sendShowController.getReportCount().setText(String.valueOf(AtomicUtil.reportCount.get()));
        sendShowController.getResponseCount().setText(String.valueOf(AtomicUtil.responseCount.get()));
        sendShowController.getSucCount().setText(String.valueOf(AtomicUtil.sucCount.get()));
        sendShowController.getFailCount().setText(String.valueOf(AtomicUtil.failCount.get()));
        sendShowController.getSubmitCount().setText(String.valueOf(AtomicUtil.sendCount.get()));
    }
}
