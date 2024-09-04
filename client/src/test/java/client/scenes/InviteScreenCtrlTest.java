package client.scenes;

import client.utils.ConfigManager;
import client.utils.EmailManager;
import client.utils.ServerUtils;
import commons.dtos.EventDTO;
import commons.dtos.ParticipantDTO;
import commons.dtos.TagDTO;
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

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class InviteScreenCtrlTest {

    @Mock
    private ServerUtils serverUtils;
    @Mock
    private EmailManager emailManager;

    @Mock
    private MainCtrl mainCtrl;

    @InjectMocks
    private InviteScreenCtrl inviteScreenCtrl;

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/InviteScreen.fxml"), bundle);

        loader.setControllerFactory(parameter -> inviteScreenCtrl);
        Pane pane = loader.load();
        inviteScreenCtrl = loader.getController();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("languages.labels", new Locale("en"));
        inviteScreenCtrl.updateResources(resourceBundle);

        reset(serverUtils, mainCtrl, emailManager);
    }

    @Test
    void testSendEmailCheckCredentials(FxRobot robot) {
        robot.clickOn("#textArea").write("boyanbonev43@gmail.com");
        robot.clickOn("#sendInvitesButton");

        verify(emailManager).checkCredentials();
    }

    @Test
    void testSendTestEmailSuccess(FxRobot robot) {
        robot.clickOn("#testButton");

        verify(emailManager).checkCredentials();
    }


    @Test
    void testGoBackSuccess(FxRobot robot) {
        robot.clickOn("#backButton");
        verify(mainCtrl).showEventOverview();
    }
}
