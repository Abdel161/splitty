package client.scenes;

import client.utils.ConfigManager;
import client.utils.ServerUtils;
import commons.dtos.EventDTO;
import commons.dtos.ParticipantDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class AddParticipantCtrlTest {

    @Mock
    private ServerUtils serverUtils;

    @Mock
    private MainCtrl mainCtrl;

    @InjectMocks
    private AddParticipantCtrl addParticipantCtrl;

    @BeforeAll
    static void setupAll() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    @Start
    private void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("languages.labels", new Locale("en"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/AddParticipant.fxml"), bundle);

        loader.setControllerFactory(parameter -> addParticipantCtrl);
        Pane pane = loader.load();
        addParticipantCtrl = loader.getController();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("languages.labels", Locale.ENGLISH);
        addParticipantCtrl.updateResources(resourceBundle);

        reset(serverUtils, mainCtrl);
    }

    @Test
    void testBackButtonReturnsToEvents(FxRobot robot) {
        robot.clickOn("#back_button");

        verify(mainCtrl).showEventOverview();
    }
}
