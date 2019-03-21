package com.anhtt.miui.translation.helper.model;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class ListWrongApplication extends Application {
    List<WrongApplication> untranslatedApplications;

    public ListWrongApplication(List<WrongApplication> untranslatedApplications) {
        this.untranslatedApplications = untranslatedApplications;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tạo một ListView
        ListView<WrongApplication> listView = new ListView<WrongApplication>(FXCollections.observableList(untranslatedApplications));


        // Cho phép lựa chọn nhiều dòng trên danh sách.
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        // Lựa chọn phần tử Index = 1,2
        listView.getSelectionModel().selectIndices(1, 2);

        // Focus
        listView.getFocusModel().focus(1);

        StackPane root = new StackPane();
        root.getChildren().add(listView);

        primaryStage.setTitle("ListView (o7planning.org)");

        Scene scene = new Scene(root, 350, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
