package fsd.assignment.assignment;

import fsd.assignment.assignment.datamodel.Student;
import fsd.assignment.assignment.datamodel.StudentData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.io.IOException;

import java.util.Comparator;
import java.util.Optional;

public class Controller {

    @FXML
    private TextField studId;

    @FXML
    private TextField yearStudy;

    @FXML
    private ChoiceBox<String> mod1Choice;

    @FXML
    private ChoiceBox<String> mod2Choice;

    @FXML
    private ChoiceBox<String> mod3Choice;

    private String choice1, choice2, choice3;

    private String[] modChoices = {"OOP", "Data Algo", "DS", "Maths", "AI",
            "Adv Programming", "Project"};

    @FXML
    private Label validateStudent;

    @FXML
    private ListView<Student> studentListView;

    @FXML
    private Label yearStudyView;

    @FXML
    private Label mod1View;

    @FXML
    private Label mod2View;

    @FXML
    private Label mod3View;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private BorderPane mainWindow;

    public Student studentToAdd;

    public void initialize() {

        studentListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {

                if (newValue != null) {
                    Student student = studentListView.getSelectionModel().getSelectedItem();
                    yearStudyView.setText(student.getYearOfStudy());
                    mod1View.setText(student.getModule1());
                    mod2View.setText(student.getModule2());
                    mod3View.setText(student.getModule3());
                }
            }
        });

        mod1Choice.setOnAction(this::getChoice);
        mod2Choice.setOnAction(this::getChoice);
        mod3Choice.setOnAction(this::getChoice);

        for (String modChoice : modChoices) {
            mod1Choice.getItems().add(modChoice);
            mod2Choice.getItems().add(modChoice);
            mod3Choice.getItems().add(modChoice);
        }

        listContextMenu = new ContextMenu();

        MenuItem deleteStudent = new MenuItem("Delete?");

        deleteStudent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Student student = studentListView.getSelectionModel().getSelectedItem();
                deleteStudent(student);
            }
        });

        listContextMenu = new ContextMenu();

        MenuItem editStudent = new MenuItem("Edit??");

        editStudent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Student student = studentListView.getSelectionModel().getSelectedItem();
                editStudent(student);
            }
        });

        listContextMenu.getItems().add(deleteStudent);
        listContextMenu.getItems().add(editStudent);

        studentListView.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
            public ListCell<Student> call(ListView<Student> param) {
                ListCell<Student> cell = new ListCell<Student>() {
                    @Override
                    protected void updateItem(Student stu, boolean empty) {

                        super.updateItem(stu, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(stu.getStudId());
                        }

                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });
                return cell;
            }
        });


        SortedList<Student> sortedByYear = new SortedList<>(StudentData.getInstance().getStudents(),
                Comparator.comparing(Student::getYearOfStudy));

        studentListView.setItems(sortedByYear);
        studentListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        studentListView.getSelectionModel().selectFirst();

    }

    public void getChoice(ActionEvent event) {

        choice1 = mod1Choice.getValue();
        choice2 = mod2Choice.getValue();
        choice3 = mod3Choice.getValue();

    }

    @FXML
    public void addStudentData() {

        String studIdS = studId.getText().trim();
        String yearStudyS = yearStudy.getText().trim();

        if (studIdS.isEmpty() || yearStudyS.isEmpty()) {
            validateStudent.setText("Error: cannot add student if studId or year of study not filled in");
        } else {
            validateStudent.setText(" ");
            studentToAdd = new Student(studIdS, yearStudyS, choice1, choice2, choice3);
            StudentData.getInstance().addStudentData(studentToAdd);
            studentListView.getSelectionModel().select(studentToAdd);
        }
    }

    public void deleteStudent(Student stu) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete a student from the list");
        alert.setHeaderText("Deleting student " + stu.getStudId());
        alert.setContentText("Are you sure you want to delete the student?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            StudentData.getInstance().deleteStudent(stu);
        }
    }

    public void editStudent(Student stu) {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(mainWindow.getScene().getWindow());
        dialog.setTitle("Edit a student's details");
        dialog.setHeaderText("Editing student Id: " + stu.getStudId());

        FXMLLoader fxmlLoader = new FXMLLoader();

        fxmlLoader.setLocation(getClass().getResource("edit-students.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException event) {

            System.out.println("Could not load the dialog");
            event.printStackTrace();
            return;
        }

        EditStudentController ec = fxmlLoader.getController();

        ec.setToEdit(stu);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Student editStudent = ec.processEdit(stu);
            studentListView.getSelectionModel().select(editStudent);
        }
    }
}