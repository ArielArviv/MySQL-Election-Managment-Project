package electionMVC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import electionMVC.controller.Controller;
import electionMVC.model.ElectionRound;
import electionMVC.model.ElectionRoundDB;
import electionMVC.view.View;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		Stage stage = new Stage();
		stage.setTitle("Create or load Election Round");
		FileChooser fileChooser = new FileChooser();
		Button btnYesLoading= new Button("Yes");
		Button btnNoLoading = new Button("No");
		Button btnLoadFile = new Button("Choose a file");
		
		GridPane gpLoadFile = new GridPane();
		//Label lblLoadFile = new Label("Hi, would you like to load election file?");
		
		Label lblLoadFile = new Label("Choose the election round date!");
		DatePicker datePicker = new DatePicker();
		
		HBox hb = new HBox();
		VBox vb = new VBox();
		
		
		datePicker.setVisible(true);
		btnYesLoading.setVisible(false);
		btnNoLoading.setVisible(false);
		
		
		lblLoadFile.setMaxWidth(230);
		gpLoadFile.setAlignment(Pos.TOP_CENTER);
		
		gpLoadFile.setHgap(14);
		gpLoadFile.setVgap(35);
		hb.setSpacing(20);
		
		
		hb.getChildren().addAll(btnYesLoading, btnNoLoading);
		vb.setSpacing(10);
		
		
		
		vb.setAlignment(Pos.CENTER);
		hb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(lblLoadFile, hb, datePicker, btnLoadFile);
		
		
		btnYesLoading.setPrefSize(50, 30);
		btnNoLoading.setPrefSize(50, 30);
		btnLoadFile.setPrefSize(100, 30);
		
		gpLoadFile.add(vb, 0, 1);
		btnLoadFile.setVisible(false);
		//datePicker.setVisible(false);
		
		
		
		datePicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					LocalDate date = datePicker.getConverter().fromString(datePicker.getEditor().getText());
					
					ElectionRoundDB elDB = new ElectionRoundDB(date);
					
					
					///ElectionRound electionRound = new ElectionRound(date);
					View theView = new View(primaryStage);
				
					//Controller controller = new Controller(electionRound , theView);
					Controller controller = new Controller(elDB , theView);
					
					//setHardCoded(controller);
					elDB.updateViewPartyNameFromDB();
					stage.close();
					
				} 
				catch (DateTimeParseException e) {
					JOptionPane.showMessageDialog(null, "Error: Date format");
				} 
				catch (Exception e) {
					
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});

		/*
		btnLoadFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				configureFileChooser(fileChooser);
				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					ObjectInputStream in;
					try {
						in = new ObjectInputStream(new FileInputStream(file));
						ElectionRound electionRound = new ElectionRound((ElectionRound)in.readObject());
						in.close();
						
						View theView = new View(primaryStage);
						Controller controller = new Controller(electionRound , theView);
						stage.close();
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
					}
					catch (ClassNotFoundException e) {
						JOptionPane.showMessageDialog(null, e.getMessage());
						
					}
				}
			}
		});
		*/
		btnYesLoading.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				lblLoadFile.setText("Loading file election!");
				lblLoadFile.setAlignment(Pos.CENTER);
				datePicker.setVisible(false);
				btnLoadFile.setVisible(true);
			}
		});
		
		btnNoLoading.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				lblLoadFile.setText("Choose election date");
				lblLoadFile.setAlignment(Pos.CENTER);
				btnLoadFile.setVisible(false);
				datePicker.setVisible(true);
			}

		});
		stage.setScene(new Scene(gpLoadFile, 350, 200));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);		
	}
	
	
	public static void setHardCoded(Controller controller) {
		//BallotBox hard code
		/*
		controller.viewUpdateNewBbox("Jerusalem", "Yafo", 1, "Citizen");
		controller.viewUpdateNewBbox("Tel aviv", "Alenbi", 12, "SickCitizen");
		controller.viewUpdateNewBbox("Jaffa", "Masrik", 14, "Soldier");
		controller.viewUpdateNewBbox("Haifa", "Nurit", 110, "SickSoldier");
		
		//Party hard code
		controller.viewUpdateNewParty("israel", "Center", LocalDate.of(2000, 12, 3));
		controller.viewUpdateNewParty("machal", "Right", LocalDate.of(1995, 8, 1));
		controller.viewUpdateNewParty("ken", "Left", LocalDate.of(1948, 8, 1));

		//Citizen hard code
		
		controller.viewUpdateNewCitizen("yoyo", "000001111", 1948, false, -1); //regular citizen
		controller.viewUpdateNewCitizen("gogo", "000011111", 1949, false, -1); 
		controller.viewUpdateNewCitizen("momo", "000111111", 1950, true, -1); // sick citizen
		controller.viewUpdateNewCitizen("bibi", "001111111", 1951, true, -1);
		controller.viewUpdateNewCitizen("gigi", "011111111", 2000, false, -1); // soldier
		controller.viewUpdateNewCitizen("Moshe", "111111111", 2001, false, -1);
		controller.viewUpdateNewCitizen("yuri", "111111110", 2002, true, -1); // sick soldier
		controller.viewUpdateNewCitizen("yoshi", "111111100", 2003, true, -1);
		*/
		controller.viewUpdateNewCitizen("yogi", "000000000", 1980, false, 0); // candidate
		controller.viewUpdateNewCitizen("yoni", "000000001", 1981, false, 1);
		controller.viewUpdateNewCitizen("Robi", "000000011", 1982, false, 1);
		controller.viewUpdateNewCitizen("yoki", "000000111", 1983, false, 2);		
	}
	
	private static void configureFileChooser(FileChooser fileChooser){                           
		fileChooser.setTitle("Welcome!!!");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
	}
}
