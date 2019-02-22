package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.xml.ws.handler.MessageContext;
import Model.InOutcomeList;
import Model.InOutcomeListDAO;
import Model.UserInformationDAO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements Initializable {
	public Stage mainStage;
	public String io_userID;

	public void setIouserID(String iouserID) {
		this.io_userID = iouserID;
	}

	@FXML
	Button btnRefresh;// 새로고침
	@FXML
	TextField iouserID;
	@FXML
	Button dateSearch; // 검색
	@FXML
	Button iolBtnExit;// 종료
	@FXML
	TextField presentTime;// 현재시간
	@FXML
	TextField presentDate;// 현재날짜
	@FXML
	DatePicker iolDatePicker;// 데이터피커(날짜를 고름)
	@FXML
	TextField iolTxtBalance;// 잔돈(0원)
	@FXML
	Button iolBtnIncome;// 수입
	@FXML
	Button iolBtnOutcome;// 지출
	@FXML
	Button iolBtnEdit;// 종료
	@FXML
	Button iolBtnPiechart;// 파이차트(이모티콘)

	private InOutcomeList selectInOutcomeList;
	private int selectInOutcomeIndex;

	@FXML
	TableView<InOutcomeList> iolTableview;// 수입지출목록의테이블뷰
	ObservableList<InOutcomeList> iolData = FXCollections.observableArrayList();// ?
	ObservableList<InOutcomeList> dateData = FXCollections.observableArrayList();// ?
	ArrayList<InOutcomeList> iolArrayList;
	ArrayList<InOutcomeList> dateArrayList;
	ObservableList<String> group = FXCollections.observableArrayList();// 대분류그룹의 식구들을 모아둔것들
	ObservableList<String> group1 = FXCollections.observableArrayList();// 소분류그룹의 식구들을 모아둔것들
	ObservableList<String> group2 = FXCollections.observableArrayList();// 결제그룹의 식구들을 모아둔것들

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 0.0 처음 화면 기본 셋팅 함수
		settingFirstScene();

		// 0.1 데이트피커검색을 눌렀을때
		dateSearch.setOnAction(e -> {
			datePickerPropertyTableView();
		});

		// 0. 테이블뷰를 기본셋팅을 진행
		Connection con = DBUtility.getConnection();

		// 0. 수입-지출 목록 테이블뷰 셋팅
		setInOutcomeListTab1TableView();

		// 1. 수입버튼을 눌렀을때 수입창이 뜸
		iolBtnIncome.setOnAction((e) -> {
			handlebtnIncomeAction();
		});

		// 2. 지출버튼을 눌렀을때 지출창이 뜸
		iolBtnOutcome.setOnAction((e) -> {
			handlebtnOutcomeAction();
		});

		// 3. 수입에대한 테이블리스트를 누르면 수입수정이 뜸, 지출에 대한 테이블리스트를 누르면 지출수정이 뜸
		iolBtnEdit.setOnAction((MouseEvente) -> {
			handlebtnEditAction();
		});

		// 4. 눈(이모티콘)버튼을 누르면 차트창이 뜸
		iolBtnPiechart.setOnAction((e) -> {
			handlebtnPiechartAction();
		});

		// 5. 종료버튼을 눌렀을때 종료
		iolBtnExit.setOnAction((e) -> {
			Platform.exit();
		});

		// 6. 테이블뷰 한줄 선택후 ->두번따당하면 삭제
		iolTableview.setOnMouseClicked((e) -> {
			selectTableListTwoClickRemove(e);
		});

		// 7. 새로고침누르면 모든 현재날짜, 시간, 테이블뷰, 잔액이 리프레쉬됨
		btnRefresh.setOnAction((e) -> {

			settingFirstScene();
			if(!iolData.isEmpty()&& !iolArrayList.isEmpty()) {
				iolData.clear();
				iolArrayList.clear();
			}
			iolArrayList = InOutcomeListDAO.getInOutcomeList();
			for(InOutcomeList iocl :iolArrayList) {
				iolData.add(iocl);
			}
			iolTableview.setItems(iolData);
			iolDatePicker.setValue(null);

		});

	}

	// 0.1 데이트피커가 변경될때 테이블뷰 세팅
	private void datePickerPropertyTableView() {
		dateData.clear();
		dateArrayList = InOutcomeListDAO.getDateList(iolDatePicker.getValue().toString(), io_userID);
		for (InOutcomeList ioc : dateArrayList) {
			dateData.add(ioc);
		}
		iolTableview.refresh();
		iolTableview.setItems(dateData);
	}

	// 0-0 처음 화면 기본 셋팅 함수
	private void settingFirstScene() {
		LocalDate currentDate = LocalDate.now();
		LocalTime currentTime = LocalTime.now();
		presentDate.setText(currentDate.toString());
		presentTime.setText(currentTime.toString().substring(0, 5));

		// 총잔액 표시
		int income = InOutcomeListDAO.balanceIncome(io_userID);
		int outcome = InOutcomeListDAO.balanceOutcome(io_userID);

		int total = income - outcome;
		iolTxtBalance.setText(total + "원");

	}

//0. 수입-지출 목록 테이블뷰 셋팅
	private void setInOutcomeListTab1TableView() {

		// 번호
		TableColumn tcNo = iolTableview.getColumns().get(0);
		tcNo.setCellValueFactory(new PropertyValueFactory<>("no"));
		tcNo.setStyle("-fx-alignment: CENTER;");
		// 아디
		TableColumn tcUserid = iolTableview.getColumns().get(1);
		tcUserid.setCellValueFactory(new PropertyValueFactory<>("userid"));
		tcUserid.setStyle("-fx-alignment: CENTER;");

		// 날짜
		TableColumn tcDate = iolTableview.getColumns().get(2);
		tcDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		tcDate.setStyle("-fx-alignment: CENTER;");

		// 시간
		TableColumn tcHourminute = iolTableview.getColumns().get(3);
		tcHourminute.setCellValueFactory(new PropertyValueFactory<>("hourminute"));
		tcHourminute.setStyle("-fx-alignment: CENTER;");

		// 수입
		TableColumn tcIncome = iolTableview.getColumns().get(4);
		tcIncome.setCellValueFactory(new PropertyValueFactory<>("income"));
		tcIncome.setStyle("-fx-alignment: CENTER;");

		// 지출
		TableColumn tcOutcome = iolTableview.getColumns().get(5);
		tcOutcome.setCellValueFactory(new PropertyValueFactory<>("outcome"));
		tcOutcome.setStyle("-fx-alignment: CENTER;");

		// 대분류
		TableColumn tcBiggroup = iolTableview.getColumns().get(6);
		tcBiggroup.setCellValueFactory(new PropertyValueFactory<>("biggroup"));
		tcBiggroup.setStyle("-fx-alignment: CENTER;");

		// 소분류
		TableColumn tcSmallgroup = iolTableview.getColumns().get(7);
		tcSmallgroup.setCellValueFactory(new PropertyValueFactory<>("smallgroup"));
		tcSmallgroup.setStyle("-fx-alignment: CENTER;");

		// 결제
		TableColumn tcPayment = iolTableview.getColumns().get(8);
		tcPayment.setCellValueFactory(new PropertyValueFactory<>("payment"));
		tcPayment.setStyle("-fx-alignment: CENTER;");

		// 내용
		TableColumn tcContents = iolTableview.getColumns().get(9);
		tcContents.setCellValueFactory(new PropertyValueFactory<>("contents"));
		tcContents.setStyle("-fx-alignment: CENTER;");

		iolArrayList = InOutcomeListDAO.getInOutcomeList();

		for (InOutcomeList inOutcomeList : iolArrayList) {
			iolData.add(inOutcomeList);
		}
		iolTableview.setItems(iolData);

	}

	// 1. 수입버튼을 눌렀을때 수입창이 뜸
	private void handlebtnIncomeAction() {
		try {
			// 창에 대한 설정
			Stage IOStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Income.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			IOStage.initModality(Modality.WINDOW_MODAL);
			IOStage.initOwner(mainStage);
			IOStage.setScene(scene);
			IOStage.setTitle("Income");
			IOStage.setResizable(false);
			IOStage.show();

			// 창안에서 Action
			Button ICjujang = (Button) root.lookup("#icBtnJujang");// 저장버튼연결
			Button ICcancel = (Button) root.lookup("#icBtnCancel");// 취소버튼연결
			Button btnClearIncome = (Button) root.lookup("#btnClearIncome");// 초기화버튼연결

			ComboBox<String> incomeBiggroup = (ComboBox) root.lookup("#incomeBiggroup");// 콤보박스대분류그룹연결
			ComboBox<String> incomePaymentgroup = (ComboBox) root.lookup("#incomePaymentgroup");// 콤보박스결제그룹연결
			ComboBox<String> incomeSmallgroup = (ComboBox) root.lookup("#incomeSmallgroup");// 콤보박스소분류그룹연결
			TextField incomeWon = (TextField) root.lookup("#incomeWon");// 수입금액입력텍스트필드연결
			TextField incomeT1 = (TextField) root.lookup("#incomeT1");// 시
			TextField incomeT2 = (TextField) root.lookup("#incomeT2");// 분
			TextField incomeContent = (TextField) root.lookup("#incomeContent");// 내용

			ICjujang.setOnAction((e) -> {

				InOutcomeList inoutcomeList = new InOutcomeList(null, io_userID,
						Date.valueOf(iolDatePicker.getValue().toString()),
						incomeT1.getText() + ":" + incomeT2.getText(), incomeWon.getText(), null,
						incomeBiggroup.getSelectionModel().getSelectedItem(),
						incomeSmallgroup.getSelectionModel().getSelectedItem(),
						incomePaymentgroup.getSelectionModel().getSelectedItem(), incomeContent.getText());

				int count = InOutcomeListDAO.insertInOutcome(inoutcomeList);
				iolData.add(inoutcomeList);
				IOStage.close();

			});

			group.clear();
			group.addAll("없음", "근로소득", "금융소득", "기타");// 그룹안에 식구들
			group2.clear();
			group2.addAll("현금", "체크카드", "신용카드", "기타");

			// 대분류->소분류 콤보박스

			incomeBiggroup.setItems(group);// 콤보박스에 식구들을 넣겠다
			incomePaymentgroup.setItems(group2);

			// 소분류클릭했을때
			incomeSmallgroup.setOnMouseClicked((e) -> {
				incomeBiggroup.setDisable(true);// 대분류 선택못함

				// 예1) 대분류의 근로소득선택->소분류의 없음 급여 보너스가 뜸
				// 예2) 대분류의 금융소득선택->소분류의 없음 이자 배당금이 뜸

				switch (incomeBiggroup.getSelectionModel().getSelectedItem()) {
				case "없음":
					group1.clear();
					group1.addAll("없음");// 그룹안에 식구들
					incomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "근로소득":
					group1.clear();
					group1.addAll("없음", "급여", "보너스");// 그룹안에 식구들
					incomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "금융소득":
					group1.clear();
					group1.addAll("없음", "이자", "배당금");// 그룹안에 식구들
					incomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "기타":
					group1.clear();
					group1.addAll("없음");// 그룹안에 식구들
					incomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				default:
					break;
				}
			});

			// 초기화버튼
			btnClearIncome.setOnAction((e) -> {
				incomeWon.clear();
				incomeT1.clear();
				incomeT2.clear();
				incomeBiggroup.getSelectionModel().clearSelection();
				incomeSmallgroup.getSelectionModel().clearSelection();
				incomePaymentgroup.getSelectionModel().clearSelection();
				incomeContent.clear();
			});

			ICcancel.setOnAction((e) -> {
				IOStage.close();
			}); // 취소누르면 창닫기

		} catch (Exception e) {
			callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");
		}
	}

	// 2. 지출버튼을 눌렀을때 지출창이 뜸
	private void handlebtnOutcomeAction() {
		try {
			// 창에 대한 설정
			Stage IOStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Outcome.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			IOStage.initModality(Modality.WINDOW_MODAL);
			IOStage.initOwner(mainStage);
			IOStage.setScene(scene);
			IOStage.setTitle("Outcome");
			IOStage.setResizable(false);
			IOStage.show();

			// 창안에서 Action
			Button OCjujang = (Button) root.lookup("#ocBtnJujang");// 저장버튼연결
			Button OCcancel = (Button) root.lookup("#ocBtnCancel");// 취소버튼연결
			Button btnClearOutcome = (Button) root.lookup("#btnClearOutcome");// 버튼연결
			ComboBox<String> outcomeBiggroup = (ComboBox) root.lookup("#outcomeBiggroup");// 콤보박스대분류그룹연결
			ComboBox<String> outcomePaymentgroup = (ComboBox) root.lookup("#outcomePaymentgroup");// 콤보박스결제그룹연결
			ComboBox<String> outcomeSmallgroup = (ComboBox) root.lookup("#outcomeSmallgroup");// 콤보박스소분류그룹연결
			TextField outcomeWon = (TextField) root.lookup("#outcomeWon");// 수입금액입력텍스트필드연결
			TextField outcomeT1 = (TextField) root.lookup("#outcomeT1");// 시
			TextField outcomeT2 = (TextField) root.lookup("#outcomeT2");// 분
			TextField outcomeContent = (TextField) root.lookup("#outcomeContent");// 내용

			OCjujang.setOnAction((e) -> {

				InOutcomeList inoutcomeList = new InOutcomeList(null, io_userID,
						Date.valueOf(iolDatePicker.getValue().toString()),
						outcomeT1.getText() + ":" + outcomeT2.getText(), null, outcomeWon.getText(),
						outcomeBiggroup.getSelectionModel().getSelectedItem(),
						outcomeSmallgroup.getSelectionModel().getSelectedItem(),
						outcomePaymentgroup.getSelectionModel().getSelectedItem(), outcomeContent.getText());
				int count = InOutcomeListDAO.insertInOutcome(inoutcomeList);
				iolData.add(inoutcomeList);
				IOStage.close();
			});

			group.clear();
			group.addAll("없음", "식비", "문화생활비", "주거생활비", "건강관리비", "교통비", "차량유지비", "쇼핑비", "미용비", "교육비", "사회생활비", "유흥비",
					"금융보험비", "저축", "기타");// 그룹안에 식구들
			group2.clear();
			group2.addAll("현금", "체크카드", "신용카드", "기타");

			// 대분류->소분류 콤보박스

			outcomeBiggroup.setItems(group);// 콤보박스에 식구들을 넣겠다
			outcomePaymentgroup.setItems(group2);

			// 소분류클릭했을때
			outcomeSmallgroup.setOnMouseClicked((e) -> {
				outcomeBiggroup.setDisable(true);// 대분류 선택못함

				// 예1) 대분류의 근로소득선택->소분류의 없음 급여 보너스가 뜸
				// 예2) 대분류의 금융소득선택->소분류의 없음 이자 배당금이 뜸

				switch (outcomeBiggroup.getSelectionModel().getSelectedItem()) {
				case "없음":
					group1.clear();
					group1.addAll("없음");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "식비":
					group1.clear();
					group1.addAll("없음", "식사/간식", "커피/음료", "식재료");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "문화생활비":
					group1.clear();
					group1.addAll("없음", "영화/공연", "게임/어플", "음악", "도서", "여행", "취미");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "주거생활비":
					group1.clear();
					group1.addAll("없음", "집세/관리비", "청소/세탁", "통신비", "생필품", "생활서비스", "생활세금");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "건강관리비":
					group1.clear();
					group1.addAll("없음", "운동/다이어트", "병원/약값", "요양비", "건강식품");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "교통비":
					group1.clear();
					group1.addAll("없음", "대중교통", "택시비", "장거리경비", "렌트비");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "차량유지비":
					group1.clear();
					group1.addAll("없음", "유류비", "정비/세차", "주차/통행", "자동차보험");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "쇼핑비":
					group1.clear();
					group1.addAll("없음", "의류/잡화", "전자제품", "가구");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "미용비":
					group1.clear();
					group1.addAll("없음", "헤어샵", "화장품", "뷰티관리");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "교육비":
					group1.clear();
					group1.addAll("없음", "학비", "교재비", "육아");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "사회생활비":
					group1.clear();
					group1.addAll("없음", "경조사비", "선물/용돈", "모임회비", "기부");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "유흥비":
					group1.clear();
					group1.addAll("없음", "영화/공연", "게임/어플", "음악", "도서", "여행", "취미");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "금융보험비":
					group1.clear();
					group1.addAll("없음", "금융이자", "수수료", "보장보험");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "저축":
					group1.clear();
					group1.addAll("없음", "예금/적금", "주식/펀드", "저축보험");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				case "기타":
					group1.clear();
					group1.addAll("없음");// 그룹안에 식구들
					outcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
					break;
				default:
					break;
				}
			});

			// 초기화버튼
			btnClearOutcome.setOnAction((e) -> {
				outcomeWon.clear();
				outcomeT1.clear();
				outcomeT2.clear();
				outcomeBiggroup.getSelectionModel().clearSelection();
				outcomeSmallgroup.getSelectionModel().clearSelection();
				outcomePaymentgroup.getSelectionModel().clearSelection();
				outcomeContent.clear();
			});

			OCcancel.setOnAction((e) -> {
				IOStage.close();
			}); // 취소누르면 창닫기

		} catch (Exception e) {
			callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");

		}
	}

	// 3. 수입에대한 테이블리스트를 누르면 수입수정이 뜸, 지출에 대한 테이블리스트를 누르면 지출수정이 뜸
	private void handlebtnEditAction() {

		// 만약 선택한 테이블리스트의 테이블콜롬의 수입이 null일때, 지출일때
		if (iolTableview.getSelectionModel().getSelectedItem().getIncome() == null) {
			try {
				// 창에 대한 설정
				Stage outStage = new Stage();
				FXMLLoader outLoader = new FXMLLoader(getClass().getResource("../View/OutcomeEdit.fxml"));
				Parent outRoot = outLoader.load();
				Scene scene = new Scene(outRoot);
				outStage.initModality(Modality.WINDOW_MODAL);
				outStage.initOwner(mainStage);
				outStage.setScene(scene);
				outStage.setTitle("OutcomeEdit");
				outStage.setResizable(false);
				outStage.show();

				// 창안에서 Action
				Button eoBtnJujang = (Button) outRoot.lookup("#eoBtnJujang");// 저장버튼연결
				Button eoBtnCancel = (Button) outRoot.lookup("#eoBtnCancel");// 취소버튼연결
				Button btnEoutcomeClear = (Button) outRoot.lookup("#btnEoutcomeClear");// 취소버튼연결
				ComboBox<String> EOutcomeBiggroup = (ComboBox) outRoot.lookup("#EOutcomeBiggroup");// 콤보박스대분류그룹연결
				ComboBox<String> EOutcomePaymentgroup = (ComboBox) outRoot.lookup("#EOutcomePaymentgroup");// 콤보박스결제그룹연결
				ComboBox<String> EOutcomeSmallgroup = (ComboBox) outRoot.lookup("#EOutcomeSmallgroup");// 콤보박스소분류그룹연결
				TextField EOutcomeWon = (TextField) outRoot.lookup("#EOutcomeWon");// 수입금액입력텍스트필드연결
				TextField EOutcomeT1 = (TextField) outRoot.lookup("#EOutcomeT1");// 시
				TextField EOutcomeT2 = (TextField) outRoot.lookup("#EOutcomeT2");// 분
				TextField EOutcomeContent = (TextField) outRoot.lookup("#EOutcomeContent");// 내용

				selectInOutcomeList = iolTableview.getSelectionModel().getSelectedItem();
				int index = iolTableview.getSelectionModel().getSelectedIndex();
				EOutcomeWon.setText(selectInOutcomeList.getOutcome());
				String[] hourminute = selectInOutcomeList.getHourminute().split(":");
				String hour = hourminute[0];
				String minute = hourminute[1];
				EOutcomeT1.setText(hour);
				EOutcomeT2.setText(minute);
				EOutcomeBiggroup.getSelectionModel().select(selectInOutcomeList.getBiggroup());
				EOutcomeSmallgroup.getSelectionModel().select(selectInOutcomeList.getSmallgroup());
				EOutcomePaymentgroup.getSelectionModel().select(selectInOutcomeList.getPayment());
				EOutcomeContent.setText(selectInOutcomeList.getContents());

				// 수정시
				eoBtnJujang.setOnAction((e) -> {

					String outcome = EOutcomeWon.getText().trim();
					String t1 = EOutcomeT1.getText().trim();
					String t2 = EOutcomeT2.getText().trim();
					String biggroup = EOutcomeBiggroup.getSelectionModel().getSelectedItem().trim();
					String smallgroup = EOutcomeSmallgroup.getSelectionModel().getSelectedItem().trim();
					String payment = EOutcomePaymentgroup.getSelectionModel().getSelectedItem().trim();
					String contents = EOutcomeContent.getText().trim();

					InOutcomeList inoutcomeList = new InOutcomeList(selectInOutcomeList.getNo(),
							selectInOutcomeList.getUserid(), selectInOutcomeList.getDate(),
							EOutcomeT1.getText() + ":" + EOutcomeT2.getText(), null, EOutcomeWon.getText(),
							EOutcomeBiggroup.getSelectionModel().getSelectedItem(),
							EOutcomeSmallgroup.getSelectionModel().getSelectedItem(),
							EOutcomePaymentgroup.getSelectionModel().getSelectedItem(), EOutcomeContent.getText());
					int count = InOutcomeListDAO.updateInOutcomeData(inoutcomeList);
					if (count != 0) {
						callAlert("수정 성공!:^^");
						iolArrayList.set(index, selectInOutcomeList);
						for (InOutcomeList io : iolArrayList) {
							dateData.add(io);
						}
						iolTableview.setItems(dateData);
					} else {
						callAlert("수정 실패!:T^T");
					}

				});

				group.clear();
				group.addAll("없음", "식비", "문화생활비", "주거생활비", "건강관리비", "교통비", "차량유지비", "쇼핑비", "미용비", "교육비", "사회생활비", "유흥비",
						"금융보험비", "저축", "기타");// 그룹안에 식구들
				group2.clear();
				group2.addAll("현금", "체크카드", "신용카드", "기타");

				// 대분류->소분류 콤보박스

				EOutcomeBiggroup.setItems(group);// 콤보박스에 식구들을 넣겠다
				EOutcomePaymentgroup.setItems(group2);

				// 소분류클릭했을때
				EOutcomeSmallgroup.setOnMouseClicked((e) -> {
					EOutcomeBiggroup.setDisable(true);// 대분류 선택못함

					// 예1) 대분류의 근로소득선택->소분류의 없음 급여 보너스가 뜸
					// 예2) 대분류의 금융소득선택->소분류의 없음 이자 배당금이 뜸

					switch (EOutcomeBiggroup.getSelectionModel().getSelectedItem()) {
					case "없음":
						group1.clear();
						group1.addAll("없음");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "식비":
						group1.clear();
						group1.addAll("없음", "식사/간식", "커피/음료", "식재료");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "문화생활비":
						group1.clear();
						group1.addAll("없음", "영화/공연", "게임/어플", "음악", "도서", "여행", "취미");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "주거생활비":
						group1.clear();
						group1.addAll("없음", "집세/관리비", "청소/세탁", "통신비", "생필품", "생활서비스", "생활세금");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "건강관리비":
						group1.clear();
						group1.addAll("없음", "운동/다이어트", "병원/약값", "요양비", "건강식품");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "교통비":
						group1.clear();
						group1.addAll("없음", "대중교통", "택시비", "장거리경비", "렌트비");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "차량유지비":
						group1.clear();
						group1.addAll("없음", "유류비", "정비/세차", "주차/통행", "자동차보험");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "쇼핑비":
						group1.clear();
						group1.addAll("없음", "의류/잡화", "전자제품", "가구");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "미용비":
						group1.clear();
						group1.addAll("없음", "헤어샵", "화장품", "뷰티관리");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "교육비":
						group1.clear();
						group1.addAll("없음", "학비", "교재비", "육아");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "사회생활비":
						group1.clear();
						group1.addAll("없음", "경조사비", "선물/용돈", "모임회비", "기부");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "유흥비":
						group1.clear();
						group1.addAll("없음", "영화/공연", "게임/어플", "음악", "도서", "여행", "취미");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "금융보험비":
						group1.clear();
						group1.addAll("없음", "금융이자", "수수료", "보장보험");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "저축":
						group1.clear();
						group1.addAll("없음", "예금/적금", "주식/펀드", "저축보험");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "기타":
						group1.clear();
						group1.addAll("없음");// 그룹안에 식구들
						EOutcomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					default:
						break;
					}
				});

				// 초기화버튼
				btnEoutcomeClear.setOnAction((e) -> {
					EOutcomeWon.clear();
					EOutcomeT1.clear();
					EOutcomeT2.clear();
					EOutcomeBiggroup.getSelectionModel().clearSelection();
					EOutcomeSmallgroup.getSelectionModel().clearSelection();
					EOutcomePaymentgroup.getSelectionModel().clearSelection();
					EOutcomeContent.clear();
				});

				eoBtnCancel.setOnAction((e) -> {
					outStage.close();
				}); // 취소누르면 창닫기

			} catch (Exception e) {
				callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");

			}
			// 만약 선택한 테이블리스트의 테이블콜롬의 지출이 null일때, 수입일때
		} else {
			try {
				// 창에 대한 설정
				Stage incomeStage = new Stage();
				FXMLLoader incomeLoader = new FXMLLoader(getClass().getResource("../View/IncomeEdit.fxml"));
				Parent incomeRoot = incomeLoader.load();
				Scene incomeScene = new Scene(incomeRoot);
				incomeStage.initModality(Modality.WINDOW_MODAL);
				incomeStage.initOwner(mainStage);
				incomeStage.setScene(incomeScene);
				incomeStage.setTitle("IncomeEdit");
				incomeStage.setResizable(false);
				incomeStage.show();

				// 창안에서 Action
				Button eiBtnJujang = (Button) incomeRoot.lookup("#eiBtnJujang");// 저장버튼연결
				Button eiBtnCancel = (Button) incomeRoot.lookup("#eiBtnCancel");// 취소버튼연결
				Button btnEincomeClear = (Button) incomeRoot.lookup("#btnEincomeClear");// 버튼연결
				ComboBox<String> EIncomeBiggroup = (ComboBox) incomeRoot.lookup("#EIncomeBiggroup");// 콤보박스대분류그룹연결
				ComboBox<String> EIncomePaymentgroup = (ComboBox) incomeRoot.lookup("#EIncomePaymentgroup");// 콤보박스결제그룹연결
				ComboBox<String> EIncomeSmallgroup = (ComboBox) incomeRoot.lookup("#EIncomeSmallgroup");// 콤보박스소분류그룹연결
				TextField EIncomeWon = (TextField) incomeRoot.lookup("#EIncomeWon");// 수입금액입력텍스트필드연결
				TextField EIncomeT1 = (TextField) incomeRoot.lookup("#EIncomeT1");// 시
				TextField EIncomeT2 = (TextField) incomeRoot.lookup("#EIncomeT2");// 분
				TextField EIncomeContent = (TextField) incomeRoot.lookup("#EIncomeContent");// 내용

				selectInOutcomeList = iolTableview.getSelectionModel().getSelectedItem();
				int index = iolTableview.getSelectionModel().getSelectedIndex();
				EIncomeWon.setText(selectInOutcomeList.getIncome());
				String[] hourminute = selectInOutcomeList.getHourminute().split(":");
				String hour = hourminute[0];
				String minute = hourminute[1];
				EIncomeT1.setText(hour);
				EIncomeT2.setText(minute);
				EIncomeBiggroup.getSelectionModel().select(selectInOutcomeList.getBiggroup());
				EIncomeSmallgroup.getSelectionModel().select(selectInOutcomeList.getSmallgroup());
				EIncomePaymentgroup.getSelectionModel().select(selectInOutcomeList.getPayment());
				EIncomeContent.setText(selectInOutcomeList.getContents());

				// 수정저장

				eiBtnJujang.setOnAction((e) -> {
					String income = EIncomeWon.getText().trim();
					String t1 = EIncomeT1.getText().trim();
					String t2 = EIncomeT2.getText().trim();
					String biggroup = EIncomeBiggroup.getSelectionModel().getSelectedItem().trim();
					String smallgroup = EIncomeSmallgroup.getSelectionModel().getSelectedItem().trim();
					String payment = EIncomePaymentgroup.getSelectionModel().getSelectedItem().trim();
					String contents = EIncomeContent.getText().trim();

					iolData.set(index, selectInOutcomeList);
					InOutcomeList inoutcomeList = new InOutcomeList(selectInOutcomeList.getNo(),
							selectInOutcomeList.getUserid(), selectInOutcomeList.getDate(),
							EIncomeT1.getText() + ":" + EIncomeT2.getText(), EIncomeWon.getText(), null,
							EIncomeBiggroup.getSelectionModel().getSelectedItem(),
							EIncomeSmallgroup.getSelectionModel().getSelectedItem(),
							EIncomePaymentgroup.getSelectionModel().getSelectedItem(), EIncomeContent.getText());
					int count = InOutcomeListDAO.updateInOutcomeData(inoutcomeList);
					if (count != 0) {
						callAlert("수정 성공!:^^");
						iolArrayList.set(index, selectInOutcomeList);
						for (InOutcomeList io : iolArrayList) {
							dateData.add(io);
						}
						iolTableview.setItems(dateData);
					} else {
						callAlert("수정 실패!:T^T");
					}
				});

				group.clear();
				group.addAll("없음", "근로소득", "금융소득", "기타");// 그룹안에 식구들
				group2.clear();
				group2.addAll("현금", "체크카드", "신용카드", "기타");

				// 대분류->소분류 콤보박스

				EIncomeBiggroup.setItems(group);// 콤보박스에 식구들을 넣겠다
				EIncomePaymentgroup.setItems(group2);

				// 소분류클릭했을때
				EIncomeSmallgroup.setOnMouseClicked((e) -> {
					EIncomeBiggroup.setDisable(true);// 대분류 선택못함

					// 예1) 대분류의 근로소득선택->소분류의 없음 급여 보너스가 뜸
					// 예2) 대분류의 금융소득선택->소분류의 없음 이자 배당금이 뜸

					switch (EIncomeBiggroup.getSelectionModel().getSelectedItem()) {
					case "없음":
						group1.clear();
						group1.addAll("없음");// 그룹안에 식구들
						EIncomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "근로소득":
						group1.clear();
						group1.addAll("없음", "급여", "보너스");// 그룹안에 식구들
						EIncomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "금융소득":
						group1.clear();
						group1.addAll("없음", "이자", "배당금");// 그룹안에 식구들
						EIncomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					case "기타":
						group1.clear();
						group1.addAll("없음");// 그룹안에 식구들
						EIncomeSmallgroup.setItems(group1);// 콤보박스에 식구들을 넣겠다
						break;
					default:
						break;
					}
				});

				// 초기화버튼
				btnEincomeClear.setOnAction((e) -> {
					EIncomeWon.clear();
					EIncomeT1.clear();
					EIncomeT2.clear();
					EIncomeBiggroup.getSelectionModel().clearSelection();
					EIncomeSmallgroup.getSelectionModel().clearSelection();
					EIncomePaymentgroup.getSelectionModel().clearSelection();
					EIncomeContent.clear();
				});

				eiBtnCancel.setOnAction((e) -> {
					incomeStage.close();
				}); // 취소누르면 창닫기

			} catch (Exception e) {

				callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");
			}
		} // end of if

	}// end of 함수

	// 4. 눈(이모티콘)버튼을 누르면 차트창이 뜸
	private void handlebtnPiechartAction() {
		try {
			// 창에 대한 설정
			Stage IOStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Chartpane.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			IOStage.initModality(Modality.WINDOW_MODAL);
			IOStage.initOwner(mainStage);
			IOStage.setScene(scene);
			IOStage.setTitle("Pie chart");
			IOStage.setResizable(false);
			IOStage.show();

			// 창안에서 Action
			Button pieBtnClose = (Button) root.lookup("#pieBtnClose");// 닫기버튼연결
			ComboBox<String> cmbYearGroup = (ComboBox) root.lookup("#cmbYearGroup");// 년도콤보박스연결
			if (!group.isEmpty()) {
				group.clear();
			}
			group.addAll("2018", "2019", "2020", "2021");// 그룹안에 식구들

			cmbYearGroup.setItems(group);// 콤보박스에 식구들을 넣겠다

			// 차트페인
			/*************************** 차트의 첫번쨰화면(월별) ****************************/
			Button chartIn = (Button) root.lookup("#chartIn");// 수입버튼
			Button chartOut = (Button) root.lookup("#chartOut");// 지출버튼
			Button chartInOut = (Button) root.lookup("#chartInOut");// 수입-지출버튼
			PieChart chartM = (PieChart) root.lookup("#chartM");// 월별차트
			Button chart2in = (Button) root.lookup("#chart2in");// 수입버튼
			Button chart2out = (Button) root.lookup("#chart2out");// 지출버튼
			PieChart chartC = (PieChart) root.lookup("#chartC");// 분류별차트
			Button chart3in = (Button) root.lookup("#chart3in");// 수입버튼
			Button chart3out = (Button) root.lookup("#chart3out");// 지출버튼
			PieChart chartP = (PieChart) root.lookup("#chartP");// 결제수단차트

			// 수입눌렀을때
			cmbYearGroup.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// 수입
					chartIn.setOnAction((e) -> {
						int January = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "01");
						int Febrary = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "02");
						int March = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "03");
						int April = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "04");
						int May = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "05");
						int June = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "06");
						int July = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "07");
						int August = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "08");
						int September = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "09");
						int October = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "10");
						int November = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "11");
						int December = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "12");

						chartM.setData(FXCollections.observableArrayList(new PieChart.Data("1월", January),
								new PieChart.Data("2월", Febrary), new PieChart.Data("3월", March),
								new PieChart.Data("4월", April), new PieChart.Data("5월", May),
								new PieChart.Data("6월", June), new PieChart.Data("7월", July),
								new PieChart.Data("8월", August), new PieChart.Data("9월", September),
								new PieChart.Data("10월", October), new PieChart.Data("11월", November),
								new PieChart.Data("12월", December)));
					});
					// 지출
					chartOut.setOnAction((e1) -> {
						int January1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "01");
						int Febrary1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "02");
						int March1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "03");
						int April1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "04");
						int May1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "05");
						int June1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "06");
						int July1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "07");
						int August1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "08");
						int September1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "09");
						int October1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "10");
						int November1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "11");
						int December1 = InOutcomeListDAO
								.getOutComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "12");

						chartM.setData(FXCollections.observableArrayList(new PieChart.Data("1월", January1),
								new PieChart.Data("2월", Febrary1), new PieChart.Data("3월", March1),
								new PieChart.Data("4월", April1), new PieChart.Data("5월", May1),
								new PieChart.Data("6월", June1), new PieChart.Data("7월", July1),
								new PieChart.Data("8월", August1), new PieChart.Data("9월", September1),
								new PieChart.Data("10월", October1), new PieChart.Data("11월", November1),
								new PieChart.Data("12월", December1)));
					});

					// 수입-지출
					chartInOut.setOnAction((e2) -> {
						int January2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "01")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "01");
						int Febrary2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "02")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "02");
						int March2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "03")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "03");
						int April2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "04")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "04");
						int May2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "05")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "05");
						int June2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "06")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "06");
						int July2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "07")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "07");
						int August2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "08")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "08");
						int September2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "09")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "09");
						int October2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "10")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "10");
						int November2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "11")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "11");
						int December2 = InOutcomeListDAO
								.getInComeDataOfYears(cmbYearGroup.getSelectionModel().getSelectedItem(), "12")
								- InOutcomeListDAO.getOutComeDataOfYears(
										cmbYearGroup.getSelectionModel().getSelectedItem(), "12");

						chartM.setData(FXCollections.observableArrayList(new PieChart.Data("1월", January2),
								new PieChart.Data("2월", Febrary2), new PieChart.Data("3월", March2),
								new PieChart.Data("4월", April2), new PieChart.Data("5월", May2),
								new PieChart.Data("6월", June2), new PieChart.Data("7월", July2),
								new PieChart.Data("8월", August2), new PieChart.Data("9월", September2),
								new PieChart.Data("10월", October2), new PieChart.Data("11월", November2),
								new PieChart.Data("12월", December2)));

					});
					// 분류별
					chart2in.setOnAction((e3) -> {
						int X = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(), "없음");
						int work = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"근로소득");
						int finance = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"금융소득");
						int gita = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"기타");

						chartC.setData(FXCollections.observableArrayList(new PieChart.Data("없음", X),
								new PieChart.Data("근로소득", work), new PieChart.Data("금융소득", finance),
								new PieChart.Data("기타", gita)));
					});

					chart2out.setOnAction((e4) -> {
						int X1 = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(), "없음");
						int eat = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(), "식비");
						int culture = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"문화생활비");
						int home = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"주거생활비");
						int helth = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"건강관리비");
						int traffic = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"교통비");
						int car = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"차량유지비");
						int shopping = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"쇼핑비");
						int beauty = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"미용비");
						int edu = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"교육비");
						int soc = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"사회생활비");
						int kara = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"유흥비");
						int insu = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"금융보험비");
						int pig = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(), "저축");
						int gita1 = InOutcomeListDAO.Categorize(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"기타");

						chartC.setData(FXCollections.observableArrayList(new PieChart.Data("없음", X1),
								new PieChart.Data("식비", eat), new PieChart.Data("문화생활비", culture),
								new PieChart.Data("주거생활비", home), new PieChart.Data("건강관리비", helth),
								new PieChart.Data("교통비", traffic), new PieChart.Data("차량유지비", car),
								new PieChart.Data("쇼핑비", shopping), new PieChart.Data("미용비", beauty),
								new PieChart.Data("교육비", edu), new PieChart.Data("사회생활비", soc),
								new PieChart.Data("유흥비", kara), new PieChart.Data("금융보험비", insu),
								new PieChart.Data("저축", pig), new PieChart.Data("기타", gita1)));
					});
					// 결제수단별
					chart3in.setOnAction((e5) -> {
						int X = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(), "없음");
						int work = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"근로소득");
						int finance = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"금융소득");
						int gita = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(), "기타");

						chartP.setData(FXCollections.observableArrayList(new PieChart.Data("없음", X),
								new PieChart.Data("근로소득", work), new PieChart.Data("금융소득", finance),
								new PieChart.Data("기타", gita)));
						int cash = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(), "현금");
						int check = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"체크카드");
						int sin = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(), "신용카드");
						int gita2 = InOutcomeListDAO.Payment0(cmbYearGroup.getSelectionModel().getSelectedItem(), "기타");

						chartP.setData(FXCollections.observableArrayList(new PieChart.Data("현금", cash),
								new PieChart.Data("체크카드", check), new PieChart.Data("신용카드", sin),
								new PieChart.Data("기타", gita2)));

					});
					chart3out.setOnAction((e6) -> {
						int X = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(), "없음");
						int work = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"근로소득");
						int finance = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"금융소득");
						int gita = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(), "기타");

						chartP.setData(FXCollections.observableArrayList(new PieChart.Data("없음", X),
								new PieChart.Data("근로소득", work), new PieChart.Data("금융소득", finance),
								new PieChart.Data("기타", gita)));
						int cash = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(), "현금");
						int check = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(),
								"체크카드");
						int sin = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(), "신용카드");
						int gita2 = InOutcomeListDAO.Payment1(cmbYearGroup.getSelectionModel().getSelectedItem(), "기타");

						chartP.setData(FXCollections.observableArrayList(new PieChart.Data("현금", cash),
								new PieChart.Data("체크카드", check), new PieChart.Data("신용카드", sin),
								new PieChart.Data("기타", gita2)));

					});

					pieBtnClose.setOnAction((e) -> {
						IOStage.close();
					}); // 취소누르면 창닫기

				}
			});

		} catch (Exception e) {
			callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");
		}
	}

	// 6. 테이블뷰 한줄 선택후 ->두번따당하면 삭제
	private void selectTableListTwoClickRemove(MouseEvent e) {

		selectInOutcomeList = iolTableview.getSelectionModel().getSelectedItem();
		selectInOutcomeIndex = iolTableview.getSelectionModel().getSelectedIndex();

		if (e.getClickCount() == 2) {
			int count = InOutcomeListDAO.deleteInOutcomeListData(selectInOutcomeList.getNo());

			if (count != 0) {
				try {
					Stage IOStage = new Stage();
					FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/notice.fxml"));
					Parent root = loader.load();
					Scene scene = new Scene(root);
					IOStage.initModality(Modality.WINDOW_MODAL);
					IOStage.initOwner(mainStage);
					IOStage.setScene(scene);
					IOStage.setTitle("Will you delete it?");
					IOStage.setResizable(false);
					IOStage.show();

					Button btnNDelete = (Button) root.lookup("#btnNDelete");// 삭제버튼
					Button btnNCancel = (Button) root.lookup("#btnNCancel");// 취소버튼

					// 삭제
					btnNDelete.setOnAction((ActionEvent event) -> {
						iolData.remove(selectInOutcomeIndex);
						iolTableview.refresh();
						callAlert("삭제완료 : 삭제성공");
						IOStage.close();
					});

					// 취소
					btnNCancel.setOnAction((ActionEvent event) -> {
						IOStage.close();
					});

				} catch (Exception e1) {
					callAlert("화면전환오류:화면전환에 문제가 있습니다. 검토바람.");
				}

			} else {
				callAlert("삭제실패 : 확인요망");
			}
		} // end of if
	}

	// ***알림창***
	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Notice");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 1));
		alert.showAndWait();
	}
}
