package cn.sanenen.fx.common;

import cn.hutool.core.io.resource.ClassPathResource;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

public class Splash extends SplashScreen {

    @Override
    public Parent getParent() {
        Group gp = new Group();
        ImageView imageView = new ImageView(new ClassPathResource("img/splash.png").getUrl().toExternalForm());
        gp.getChildren().add(imageView);
        return gp;
    }

}
