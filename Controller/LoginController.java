package Controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.UserInformation;
import Model.UserInformationDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements Initializable {
	UserInformation userInformation;// 유저정보를 담을 빈공간
	private ArrayList<UserInformation> userDBList = new ArrayList<>();// 사용자의 정보를 담은 리스트
	public Stage LoginStage;
	@FXML
	private TextField LuserID;// 로그인화면의 아디텍스트필드
	@FXML
	private PasswordField LpassWord;// 로그인화면의 비번패스워드필드
	@FXML
	private Button btnRegister;// 사용자등록버튼
	@FXML
	private Button btnLogin;// 로그인버튼
	@FXML
	private Button btnExit;// 종료버튼
	@FXML
	private Button btnIDK;// 아디,비번몰라버튼

	public String ioUserID;
	public Stage IOStage;

	ObservableList<String> cmbFavoriteList = FXCollections.observableArrayList();// 콤보그룹의 식구들을 모아둔것들

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Media media = new Media("file:/c:/media/play.m4a");
		MediaPlayer mp = new MediaPlayer(media);
		mp.play();

		// 4. 사용자등록버튼을 눌렀을때 사용자등록창이 뜸
		btnRegister.setOnAction((e) -> {
			handlebtnRegisterAction();
		});
//5. Login버튼을 눌렀을때 수입-지출 목록창이 뜸
		btnLogin.setOnAction((e) -> {
			handleBtnLoginAction();
		});
//6. Exit버튼을 눌렀을때 창이 꺼짐
		btnExit.setOnAction((e) -> {
			handleLabelExitAction();
		});
//7. ID, 비번몰라버튼 눌렀을때 아디비번몰라창이 뜸
		btnIDK.setOnAction((e) -> {
			handlebtnIDKAction();
		});

	}// end of initaliz

//4. 사용자등록버튼을 눌렀을때 사용자등록창이 뜸
	private void handlebtnRegisterAction() {
		try {
			// 창에 대한 설정
			Stage IOStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/UserRegister.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			IOStage.initModality(Modality.WINDOW_MODAL);
			IOStage.initOwner(LoginStage);
			IOStage.setScene(scene);

			IOStage.setTitle("User Register");
			IOStage.setResizable(false);
			IOStage.show();

			// 창안에서 Action
			Button URjujang = (Button) root.lookup("#urBtnJujang");// 저장버튼연결
			Button URcancel = (Button) root.lookup("#urBtnCancel");// 취소버튼연결

			TextField userID = (TextField) root.lookup("#userID");// 사용자아디 텍스트필드연결
			TextField passWord = (TextField) root.lookup("#passWord");// 비밀번호 텍스트필드연결

			TextField cmbFavoriteAnswer = (TextField) root.lookup("#cmbFavoriteAnswer");// 콤보박스답변 텍스트필드연결
			TextField phoneNumber = (TextField) root.lookup("#phoneNumber");// 전화번호 텍스트필드연결

			// 콤보박스
			ComboBox<String> cmbFavoriteGroup = (ComboBox) root.lookup("#cmbFavoriteGroup");// 콤보박스그룹연결
			cmbFavoriteList.addAll("내가 현재 사는곳은?", "가장 좋아하는 캐릭터는?", "가장 좋아하는 꽃은?", "가장 좋아하는 동물은?", "우주가 좋아하는 음식 장르는?",
					"우리아빠이름은?");// 그룹안에 식구들
			cmbFavoriteGroup.setItems(cmbFavoriteList);// 콤보박스에 식구들을 넣겠다

			// 저장버튼을누르면 벌어지는 일들
			URjujang.setOnAction((e) -> {
				userInformation = new UserInformation(userID.getText(), passWord.getText(),
						cmbFavoriteGroup.getSelectionModel().getSelectedItem().toString(), cmbFavoriteAnswer.getText(),
						phoneNumber.getText()); // 공간에 사용자정보를 넣는다
				UserInformationDAO.insertUserInformation(userInformation);// 사용자정보를 데이타베이스에 넣는다
				userID.clear();
				passWord.clear();
				cmbFavoriteGroup.getSelectionModel().clearSelection();
				cmbFavoriteAnswer.clear();
				phoneNumber.clear();

			});

			URcancel.setOnAction((e) -> {
				IOStage.close();
			}); // 취소누르면 창닫기

		} catch (Exception e) {
			callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");
		}
	}

//5. Login버튼을 눌렀을때 수입-지출 목록창이 뜸

	private void handleBtnLoginAction() {

		// pass라는 스트링타입의 빈공간
		String pass = null;
		// 빈공간의 유저정보를
		for (UserInformation userInformation : UserInformationDAO.getUserInformation()) {

			// 만약 로그인텍스트필드와 유저정보의 아이디와 같냐?
			if (userInformation.getUserid().equals(LuserID.getText())) {
				// 같으면 비번을 가져옴 빈공간에 저장
				ioUserID = userInformation.getUserid();
				pass = userInformation.getPassword();
			}
		} // end of for

		// 만약 저장된 비번정보와 로그인패스워드가 같지않으면
		if (!(pass.equals(LpassWord.getText()))) {
			// 이런알림창이 뜬다.
			callAlert("로그인실패: 아이디,패스워드가 맞지 않습니다.");
			LuserID.clear();// 로그인아디 깨끗히
			LpassWord.clear();// 로그인비번 깨끗히
		} else {
			Parent root;
			FXMLLoader loader;
			try {
				// 창에 대한 설정
				Stage IOStage = new Stage();
				loader = new FXMLLoader(getClass().getResource("../View/IncomeOutcome.fxml"));
				root = loader.load();
				MainController mainController = loader.getController();
				mainController.mainStage = IOStage;
				mainController.setIouserID(ioUserID);
				Scene scene = new Scene(root);
				IOStage.setScene(scene);
				IOStage.setResizable(false);// 수정못함
				LoginStage.close();// 로그인창닫음
				IOStage.setTitle(LuserID.getText() + "'s household ledger"); // 로그인아디가 들어감
				IOStage.show();

				// callAlert("화면전환성공:메인화면으로 전환되었습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");
			}
		} // end of else

	}// end of handleBtnLoginAction()

//6. Exit버튼을 눌렀을때 창이 꺼짐
	private void handleLabelExitAction() {
		Platform.exit();
	}

//7. ID, 비번몰라버튼 눌렀을때 아디비번몰라창이 뜸
	private void handlebtnIDKAction() {

		try {
			// 창에 대한 설정
			Stage IOStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/IDon'tKnow.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			IOStage.initModality(Modality.WINDOW_MODAL);
			IOStage.initOwner(LoginStage);
			IOStage.setResizable(false);
			IOStage.setScene(scene);
			IOStage.setTitle("I don't know");
			IOStage.show();

			// 창안에서 Action
			Button IDKok = (Button) root.lookup("#idkBtnOk");// 확인버튼연결
			IDKok.setOnAction((e) -> {
				TextField idkTxtPN = (TextField) root.lookup("#idkTxtPN");// 전화번호텍스트필드연결

				// 빈공간의 유저정보를
				for (UserInformation userInformation : UserInformationDAO.getUserInformation()) {

					// 만약 전화번호텍스트필드와 유저정보의 전화번호가 같냐?
					if (userInformation.getPhonenumber().equals(idkTxtPN.getText())) {
						// 같으면 비번을 가져옴 빈공간에 저장
						callAlert("알림창 : 아이디- " + userInformation.getUserid() + " 비밀번호- "
								+ userInformation.getPassword());
						idkTxtPN.clear();// 전화번호깨끗히
					} else {
						// callAlert("오류:등록되지않은 전화번호입니다");
					}
				} // end of for
			});

			Button IDKcancel = (Button) root.lookup("#idkBtnCancel");// 취소버튼연결

			// 취소누르면 창닫기
			IDKcancel.setOnAction((e) -> {
				IOStage.close();
			});
		} catch (Exception e) {

			callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");
		}
	}

// 기타: 알림창(중간에: 적어줄것) 예시: "오류정보: 값을제대로 입력해쥬세요"
	private void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Notice");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 1));
		alert.showAndWait();
	}

}
