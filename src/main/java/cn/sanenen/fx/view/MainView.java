package cn.sanenen.fx.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@FXMLView(value = "/fxml/main.fxml")
public class MainView extends AbstractFxmlView implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Parent view = getView();
//        Label label_ip = (Label) view.lookup("#label_ip");
//        ObservableList<Node> childrenUnmodifiable = view.getChildrenUnmodifiable();
//        childrenUnmodifiable.forEach(e->{
//            e.getId();
//            Console.log("{},id:{}",e,e.getId());
//        });
    }
    
    
}
