package client.scenes;

import client.utils.ConfigManager;
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

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class AddTagCtrlTest {

    @Mock
    private ServerUtils serverUtils;

    @Mock
    private MainCtrl mainCtrl;

    @InjectMocks
    private AddTagCtrl addTagCtrl;

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/AddTag.fxml"), bundle);

        loader.setControllerFactory(parameter -> addTagCtrl);
        Pane pane = loader.load();
        addTagCtrl = loader.getController();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("languages.labels", new Locale("en"));
        addTagCtrl.updateResources(resourceBundle);

        EventDTO eventDTO = new EventDTO(1L, "Party", "ABC123", Calendar.getInstance(), Calendar.getInstance(), Set.of(1L));
        addTagCtrl.setEvent(eventDTO, false);

        reset(serverUtils, mainCtrl);
    }

    @Test
    void testOnSaveTagSuccess(FxRobot robot) {
        robot.clickOn("#tagName").write("Drinks");

        robot.clickOn("#addButton");
        robot.clickOn("OK");
        robot.clickOn("OK");

        verify(serverUtils).addTag(anyLong(), any(TagDTO.class));

        robot.clickOn("#backButton");
        verify(mainCtrl).returnToAddExpenseScreen(any(Boolean.class));
    }
}
