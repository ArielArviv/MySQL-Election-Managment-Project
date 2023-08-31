package electionMVC.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Vector;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import electionMVC.listeneres.ViewListenable;

public class View {

	static int index = 0;
	private Vector<ViewListenable> allListeners = new Vector<ViewListenable>();
	private ComboBox<String> cmbParty = new ComboBox<String>();
	private Alert alert = new Alert(AlertType.INFORMATION);
	private ComboBox<String> cmbPvote = new ComboBox<String>();

	public View(Stage primaryStage) {
		primaryStage.setTitle("Election menu");
		GridPane gpRoot = new GridPane();	
		gpRoot.setPadding(new Insets(10));
		gpRoot.setHgap(10);
		gpRoot.setVgap(10);

		//1st line.bbox
		Label lblBbox = new Label("Ballot box:");
		TextField tfCity = new TextField();
		TextField tfStreet = new TextField();
		TextField tfAddress = new TextField();
		Button btnAddBbox = new Button("Add Bbox");
		ComboBox<String> cmbBboxType = new ComboBox<String>();

		tfCity.setPromptText("City");
		tfCity.setFocusTraversable(false);
		tfCity.setPrefWidth(80);
		tfStreet.setPromptText("Street");
		tfStreet.setFocusTraversable(false);
		tfStreet.setPrefWidth(80);
		tfAddress.setPromptText("Address");
		tfAddress.setFocusTraversable(false);
		tfAddress.setPrefWidth(80);
		cmbBboxType.setPromptText("Type");
		cmbBboxType.getItems().addAll("Citizen", "SickCitizen", "Soldier", "SickSoldier");
		cmbBboxType.setPrefWidth(100);
		btnAddBbox.setPrefWidth(100);

		gpRoot.add(lblBbox, 0, 0);
		gpRoot.add(tfCity, 1, 0);
		gpRoot.add(tfStreet, 2, 0);
		gpRoot.add(tfAddress, 3, 0);
		gpRoot.add(cmbBboxType, 4, 0);
		gpRoot.add(btnAddBbox, 8, 0);

		btnAddBbox.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				if (tfCity.getText().isEmpty() || tfStreet.getText().isEmpty() ||
						tfAddress.getText().isEmpty() ||
						cmbBboxType.getSelectionModel().isEmpty()) {
					showAlertMsg("Error: empty fields");
				}
				else {
					for (ViewListenable l : allListeners) {
						try {
							String type = cmbBboxType.getValue();
							l.viewUpdateNewBbox(tfCity.getText(), tfStreet.getText(),
									Integer.parseInt(tfAddress.getText()), type);
						} catch (NumberFormatException e) {
							showAlertMsg("Address must be a number");
						}
					}
					tfCity.clear();
					tfStreet.clear();
					tfAddress.clear();
					cmbBboxType.valueProperty().set(null);	
				}
			}
		});

		//2nd line.person
		Label lblCitizen = new Label("Citizen:");
		TextField tfName = new TextField();
		TextField tfId = new TextField();
		TextField tfYear = new TextField();
		RadioButton rbCandidate = new RadioButton("Candidate");
		RadioButton rbSick = new RadioButton("Corona");
		Button btnAddCitizen = new Button("Add Citizen");
		HBox hbCandidate = new HBox();

		hbCandidate.setSpacing(10);
		tfName.setPromptText("Name");
		tfName.setFocusTraversable(false);
		tfName.setPrefWidth(80);
		tfId.setPromptText("Id");
		tfId.setFocusTraversable(false);
		tfId.setPrefWidth(80);
		tfYear.setPromptText("Year of birth");
		tfYear.setFocusTraversable(false);
		tfYear.setPrefWidth(80);
		cmbParty.setPromptText("Party");
		cmbParty.setVisible(false);
		cmbParty.setPrefWidth(90);
		rbCandidate.setMaxHeight(150);
		btnAddCitizen.setPrefWidth(100);

		btnAddCitizen.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {

				if (tfName.getText().isEmpty() || tfId.getText().isEmpty() || 
						tfYear.getText().isEmpty() || 
						(cmbParty.getSelectionModel().isEmpty() && rbCandidate.isSelected())) {
					showAlertMsg("Error: empty fields");
				}
				else {
					for (ViewListenable l : allListeners) {
						try {
							l.viewUpdateNewCitizen(tfName.getText(), tfId.getText(),
									Integer.parseInt(tfYear.getText()), rbSick.isSelected(),
									cmbParty.getSelectionModel().getSelectedIndex());
						} catch (NumberFormatException e) {
							showAlertMsg("Year must be a number");
						}
					}
					rbSick.setSelected(false);
					rbCandidate.setSelected(false);
					rbCandidate.setVisible(true);
					cmbParty.setVisible(false);
					tfName.clear();
					tfId.clear();
					tfYear.clear();
					cmbParty.valueProperty().set(null);
				}
			}
		});

		rbSick.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				rbCandidate.setVisible(!rbSick.isSelected());
				if (rbSick.isSelected()) {
					rbCandidate.setSelected(false);
					cmbParty.setVisible(false);
					cmbParty.valueProperty().set(null);
				}	
			}
		});

		rbCandidate.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				cmbParty.setVisible(rbCandidate.isSelected() );		
			}
		});

		hbCandidate.getChildren().addAll(rbCandidate, cmbParty);
		gpRoot.add(lblCitizen, 0, 1);
		gpRoot.add(tfName, 1, 1);
		gpRoot.add(tfId, 2, 1);
		gpRoot.add(tfYear, 3, 1);
		gpRoot.add(hbCandidate, 5, 1);

		gpRoot.add(rbSick, 4, 1);
		gpRoot.add(btnAddCitizen, 8, 1);

		//3rd line.party
		Label lblParty = new Label("Party:");
		TextField tfPName = new TextField();
		ComboBox<String> cmbStream = new ComboBox<String>();
		DatePicker datePicker = new DatePicker();
		Button btnAddParty = new Button("Add Party");


		btnAddParty.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				if (tfPName.getText().isEmpty() || datePicker.getValue() == null ||
						cmbStream.getSelectionModel().isEmpty()) {
					showAlertMsg("Error: empty fields");
				}
				else {
					try {
						datePicker.getConverter().fromString(datePicker.getEditor().getText());
						for (ViewListenable l : allListeners) {
							l.viewUpdateNewParty(tfPName.getText(),
									cmbStream.getValue(), datePicker.getValue());
						}
					} catch (DateTimeParseException e) {
						showAlertMsg("You need to enter a date format");
					}
					tfPName.clear();
					datePicker.valueProperty().set(null);
					datePicker.getEditor().clear();
					cmbStream.valueProperty().set(null);
				}
			}
		});
		tfPName.setPromptText("Name");
		tfPName.setFocusTraversable(false);
		tfPName.setPrefWidth(80);
		cmbStream.setPromptText("Strem");
		cmbStream.setFocusTraversable(false);
		cmbStream.setPrefWidth(100);
		cmbStream.getItems().addAll(
				"Left",
				"Center",
				"Right");
		datePicker.setPrefWidth(120);
		btnAddParty.setPrefWidth(100);

		gpRoot.add(lblParty, 0, 2);
		gpRoot.add(tfPName, 1, 2);
		gpRoot.add(cmbStream, 2, 2);
		gpRoot.add(datePicker, 3, 2);
		gpRoot.add(btnAddParty, 8, 2);

		//3rd line.show
		Label lblShow = new Label("Show all:");
		ComboBox<String> cmbShow = new ComboBox<String>();
		Button btnShow = new Button("Show");

		btnShow.setPrefWidth(100);
		cmbShow.setPrefWidth(80);

		cmbShow.getItems().addAll(
				"Bbox",
				"Citizens",
				"Parties",
				"Results");


		btnShow.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				if (cmbShow.getSelectionModel().isEmpty()) {
					showAlertMsg("Error: empty fields");
				}
				else {
					for (ViewListenable l : allListeners) {
						TextArea area = new TextArea();
						Alert infoMsg = new Alert(AlertType.INFORMATION);
						area.setWrapText(true);
						area.setEditable(false);
						infoMsg.setResizable(true);

						infoMsg.getDialogPane().setContent(area);
						if (cmbShow.getSelectionModel().getSelectedIndex() == 0) {
							infoMsg.setTitle("view ballot box");
							area.setText(l.viewAskAllBallotboxes());
							infoMsg.showAndWait();
						}
						else if (cmbShow.getSelectionModel().getSelectedIndex() == 1) {	
							infoMsg.setTitle("view citizen");
							area.setText(l.viewAskAllCitizens());
							infoMsg.showAndWait();
						}
						else if (cmbShow.getSelectionModel().getSelectedIndex() == 2) {
							infoMsg.setTitle("view parties");
							area.setText(l.viewAskAllParties());
							infoMsg.showAndWait();
						}
						else {
							infoMsg.setTitle("view parties");
							area.setText(l.viewAskElectionResults());
							infoMsg.showAndWait();
						}
					}
				}		
			}
		});
		gpRoot.add(lblShow, 0, 3);
		gpRoot.add(cmbShow, 1, 3);
		gpRoot.add(btnShow, 8, 3);

		Button btnStart = new Button("Start election");
		btnStart.setPrefWidth(120);
		btnStart.setPrefHeight(50);
		gpRoot.add(btnStart, 3, 10);

		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(getStartRound()) {
					popElectionWindow();
					btnAddBbox.setDisable(true);
					btnAddCitizen.setDisable(true);
					btnAddParty.setDisable(true);
				}
				else {
					showAlertMsg("This election round has already done");
				}	
			}
		});


		Button btnEditDel = new Button("Delete citizen");
		btnEditDel.setPrefWidth(120);
		btnEditDel.setPrefHeight(50);
		gpRoot.add(btnEditDel, 5, 10);

		btnEditDel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				editAndDeleteConsoleMenu();
			}
		});

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				//popSaveFileWindow();
				primaryStage.close();
				Platform.setImplicitExit(true);
			}
		});

		primaryStage.setScene(new Scene(gpRoot, 856, 300));
		primaryStage.show();
	}
	private void editAndDeleteConsoleMenu() {
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter citizen id to delete:");
		String ID = sc.next();
		
		for (ViewListenable l : allListeners) {
			l.viewDeleteCitizenFromDB(ID);
		}

	}
	
	private void popElectionWindow() {
		int numOfCitizen = getNumOfCitize();

		Stage stage2 = new Stage();
		stage2.setTitle("Election with " + numOfCitizen + " voters");
		Button btnEnter = new Button("No");
		GridPane gpSecond = new GridPane();
		Label lblVote = new Label();
		CheckBox chbVote = new CheckBox();
		lblVote.setText("Hello citizen, ID: " + getIdOfCitize(index) + " - would you like to vote?");	
		HBox hb = new HBox();
		VBox vb = new VBox();
		gpSecond.setAlignment(Pos.CENTER);
		cmbPvote.setVisible(false);
		cmbPvote.setPrefWidth(100);

		gpSecond.setHgap(14);
		gpSecond.setVgap(35);
		vb.setSpacing(20);
		vb.setPrefWidth(100);
		vb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(lblVote,chbVote);
		hb.setSpacing(10);
		vb.getChildren().addAll(cmbPvote, btnEnter);
		cmbPvote.setPrefWidth(100);

		btnEnter.setPrefSize(50, 30);

		gpSecond.add(hb, 0, 0);
		gpSecond.add(vb, 0, 1);

		btnEnter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				if (chbVote.isSelected()) {
					if (cmbPvote.getSelectionModel().getSelectedIndex() != -1) {
						for (ViewListenable l : allListeners) {
							l.viewUpdateVote(cmbPvote.getSelectionModel().getSelectedIndex(), index);
						}
						chbVote.setSelected(false);
						btnEnter.setText("No");
						cmbPvote.valueProperty().set(null);
						cmbPvote.setVisible(false);
						index++;
					}
					else {
						showAlertMsg("You need to choose party!");
					}
				}
				else {
					index++;
				}

				if (index == numOfCitizen) {
					stage2.close();
				}
				else {
					lblVote.setText("Hello citizen, ID: " + getIdOfCitize(index) + " would you like to vote?");		
				}
				cmbPvote.getSelectionModel().clearSelection();
			}
		});

		chbVote.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				if(chbVote.isSelected()) {
					btnEnter.setText("Vote");
				}
				cmbPvote.setVisible(chbVote.isSelected());
				if (!chbVote.isSelected()) {
					cmbPvote.valueProperty().set(null);
					btnEnter.setText("No");
				}
			}
		});

		stage2.setScene(new Scene(gpSecond, 350, 200));
		stage2.show();
	}

	public void showAlertMsg(String msg) {
		alert.setContentText(msg);
		alert.show();	
	}

	private void popSaveFileWindow() {
		Stage stage3 = new Stage();
		stage3.setTitle("Save file");
		Button btnOk= new Button("OK");

		GridPane gpLoadFile = new GridPane();
		Label lblLoadFile = new Label("*Enter file name to save or exit");
		TextField txfSave = new TextField();
		txfSave.setPromptText("file name");
		txfSave.setFocusTraversable(false);
		VBox vb = new VBox();
		lblLoadFile.setMaxWidth(230);
		gpLoadFile.setAlignment(Pos.TOP_CENTER);
		gpLoadFile.setHgap(14);
		gpLoadFile.setVgap(35);
		lblLoadFile.setAlignment(Pos.TOP_CENTER);
		vb.setSpacing(10);
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(lblLoadFile, txfSave, btnOk);
		btnOk.setPrefSize(100, 30);
		gpLoadFile.add(vb, 0, 1);

		btnOk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (!txfSave.getText().trim().isEmpty()) {
					String fileName = txfSave.getText();
					File file = null;
					DirectoryChooser directoryChooser = new DirectoryChooser();
					file = directoryChooser.showDialog(null);

					if(file != null) {
						file = new File(file.getAbsolutePath() + "/" + fileName + ".dat");
					}

					try {
						FileOutputStream fos = new FileOutputStream(file);
						ObjectOutputStream outFile = new ObjectOutputStream(fos);
						for (ViewListenable l : allListeners) {
							l.viewSaveFile(outFile);
						}
						outFile.close();
					} 
					catch (FileNotFoundException e1) {
						showAlertMsg(e1.getMessage());
					} 
					catch (IOException e1) {
						showAlertMsg(e1.getMessage());
					}
					catch (Exception e) {
						showAlertMsg(e.getMessage());
					}
					finally {
						stage3.close();
					}

				}
				else {
					showAlertMsg("You have to choose name file!");
				}
			}
		});
		stage3.setScene(new Scene(gpLoadFile, 350, 200));
		stage3.show();

	}


	private boolean getStartRound(){
		boolean elected = false;
		for (ViewListenable l : allListeners) {
			elected = l.viewAskToStartRound();
		}
		return elected;
	}

	public void updatePartyToView(String newParty) {
		cmbParty.getItems().add(newParty);
		cmbPvote.getItems().add(newParty);
	}

	public void registerListener(ViewListenable l) {
		allListeners.add(l);
	}

	private String getIdOfCitize(int indexVoter){
		String id = "";
		for (ViewListenable l : allListeners) {
			id = l.viewAskIDvoter(indexVoter);
		}
		return id;
	}	

	private int getNumOfCitize(){
		int number = 0;
		for (ViewListenable l : allListeners) {
			number = l.viewAskCItizenNumber();
		}
		return number;
	}
}
