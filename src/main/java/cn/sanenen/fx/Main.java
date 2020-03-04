package cn.sanenen.fx;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.cron.CronUtil;
import cn.sanenen.fx.common.Splash;
import cn.sanenen.fx.view.MainView;
import com.zx.sms.connect.manager.EndpointManager;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;

@SpringBootApplication
public class Main extends AbstractJavaFxApplicationSupport {
    public static void main(String[] args) {
        launch(Main.class, MainView.class,new Splash(),args);
    }
    
    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        setTitle("CMPP客户端测试工具");
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            EndpointManager.INS.close();
            CronUtil.stop();
            int exit = SpringApplication.exit(ctx, () -> 0);
            System.exit(exit);
        });
    }
    
    

    @Override
    public Collection<Image> loadDefaultIcons() {
        return CollUtil.newArrayList(new Image(new ClassPathResource("img/e.png").getStream()));
    }
    
}
