package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Controller.DBUtility;
import Controller.MainController;

public class UserInformationDAO {
	public static ArrayList<UserInformation> UItbl = new ArrayList<>();

	// 1. 사용자정보 등록하는 함수
	public static int insertUserInformation(UserInformation ui) {
		int count = 0;

		// 1.1데이타베이스에 사용자정보입력하는 쿼리문
		StringBuffer insertUserInformation = new StringBuffer();
		insertUserInformation.append("insert into userinformation ");
		insertUserInformation.append("(userid,password,cmbfavoritegroup,cmbfavoriteanswer,phonenumber) ");
		insertUserInformation.append("values ");
		insertUserInformation.append("(?,?,?,?,?) ");

		// 1.2데이타베이스 커넥숀을 가져와야한다
		Connection con = null;

		// 1.3쿼리문을 실행해야할 Statement를 만들어야한다.
		PreparedStatement psmt = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(insertUserInformation.toString());

			// 1.4쿼리문에 실제데이타를 연결
			psmt.setString(1, ui.getUserid());
			psmt.setString(2, ui.getPassword());
			psmt.setString(3, ui.getCmbfavoritegroup());
			psmt.setString(4, ui.getCmbfavoriteanswer());
			psmt.setString(5, ui.getPhonenumber());

			// 1-5실제데이터를 연결한 쿼리문을 실행한다.
			// executeUpdate(); 쿼리문을 실행해서 테이블에 저장을 할때 사용하는 번개문
			count = psmt.executeUpdate();
			if (count == 0) {
				MainController.callAlert("삽입 쿼리실패 : 삽입 쿼리문에 문제가 있습니다.");
				return count;
			}
		} catch (Exception e) {

			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 1-6. 자원객체를 닫아야한다.
			try {
				if (psmt != null) {
					psmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				MainController.callAlert("자원 닫기 실패 : 자원 닫기에 문제가 있습니다.");
			}
		}
		return count;
	}

	// 2. 테이블에서 전체내용을 모두 가져오는 함수
	public static ArrayList<UserInformation> getUserInformation() {

		// 2-1. 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문
		String selectUserInformation = "select * from userinformation";
		// 2-2. 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 2-3. 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 2-4. 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectUserInformation);
			// 2-5. 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return null;
			}
			while (rs.next()) {
				UserInformation userInformation = new UserInformation(rs.getString(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getString(5));
				UItbl.add(userInformation);
			} // end of while
		} catch (SQLException e) {
			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 2-6. 자원객체를 닫아야한다.
			try {
				if (psmt != null) {
					psmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				MainController.callAlert("자원 닫기 실패 : 자원 닫기에 문제가 있습니다.");
			}
		}

		return UItbl;
	}

	// 3. 테이블뷰에서 선택한 레코드를 데이터베이스에서 삭제하는 함수
	public static int deleteUserInformationData(String userid) {

		// 3-1. 데이터베이스 학생테이블에 있는 레코드를 삭제하는 쿼리문
		String deleteUserInformation = "delete from userInformation where no =  ? ";
		// 3-2. 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 3-3. 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 3-4. 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		int count = 0;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(deleteUserInformation);
			psmt.setString(1, userid);
			// 3-5. 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			count = psmt.executeUpdate();
			if (count == 0) {
				MainController.callAlert("delete 실패 : delete에 문제가 있습니다.");
				return count;
			}
		} catch (SQLException e) {
			MainController.callAlert("delete 실패 : delete에 문제가 있습니다.");
		} finally {
			// 3-6. 자원객체를 닫아야한다.
			try {
				if (psmt != null) {
					psmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				MainController.callAlert("자원 닫기 실패 : 자원 닫기에 문제가 있습니다.");
			}
		}
		return count;
	}

	// 4. 테이블뷰터에서 수정한 레코드를 데이터베이스 테이블에 수정하는 함수.
	public static int updateUserInformationData(UserInformation userinformation) {
		int count = 0;
		// 4-1. 데이터베이스에 학생테이블에 수정하는 쿼리문
		StringBuffer updateUserInformation = new StringBuffer();
		updateUserInformation.append("update userInformation set ");
		updateUserInformation.append("userid=?, password=?, Cmbfavoritegroup=?, Cmbfavoriteanswer=?, phonenumber=?");
		// 4-2. 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 4-3. 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(updateUserInformation.toString());
			// 4-4. 쿼리문에 실제 데이터를 연결한다.
			psmt.setString(1, userinformation.getUserid());
			psmt.setString(2, userinformation.getPassword());
			psmt.setString(3, userinformation.getCmbfavoritegroup());
			psmt.setString(4, userinformation.getCmbfavoriteanswer());
			psmt.setString(5, userinformation.getPhonenumber());
			// 4-5. 실제데이터를 연결한 쿼리문을 실행한다.
			// executeUpdate(); 쿼리문을 실행해서 테이블에 저장을 할때 사용하는 번개문
			count = psmt.executeUpdate();
			if (count == 0) {
				MainController.callAlert("update 쿼리실패 : update 쿼리문에 문제가 있습니다.");
				return count;
			}
		} catch (SQLException e) {
			MainController.callAlert("update 실패 : update 에 문제가 있습니다.");
		} finally {
			// 4-6. 자원객체를 닫아야한다.
			try {
				if (psmt != null) {
					psmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				MainController.callAlert("자원 닫기 실패 : 자원 닫기에 문제가 있습니다.");
			}
		}
		return count;
	}
}
