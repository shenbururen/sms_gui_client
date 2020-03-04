package cn.sanenen.fx.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.sanenen.fx.service.AtomicUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import lombok.Data;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

@FXMLController
@Data
public class SendShowController implements Initializable {
    
    public Button clear;
    public Label submitCount;
    public Label responseCount;
    public Label sucCount;
    public Label failCount;
    public Label reportCount;
    public Label submitSpeed;
    public Label responseSpeed;
    public TextArea resultShow;

    @Resource
    private SendController sendController;

    public void clearCount(){
        AtomicUtil.clear();
        sendController.setSendLastNum(0);
        sendController.setResponseLastNum(0);
        resultShow.setText("");
        submitSpeed.setText("0/s");
        responseSpeed.setText("0/s");
        
    }

    /**
     * 最终结果展示
     * @param start 提交开始时间
     * @param end 提交结束时间
     * @param speed 平均速度
     */
    public void showResult(Date start,Date end, long speed){
        String format = StrUtil.format("开始时间：{}\n" +
                        "结束时间：{}\n" +
                        "提交总数：{}\n" +
                        "平均提交速度：{}\n" +
                        "响应延迟大于1秒：{}\n" +
                        "响应延迟大于3秒：{}\n" +
                        "响应延迟大于10秒：{}\n" +
                        "响应延迟大于30秒：{}",
                DateUtil.formatDateTime(start),
                DateUtil.formatDateTime(end),
                AtomicUtil.sendCount.get(),
                speed,
                AtomicUtil._1sCount.get(),
                AtomicUtil._3sCount.get(),
                AtomicUtil._10sCount.get(),
                AtomicUtil._30sCount.get()
                );
        resultShow.setText(format);
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
