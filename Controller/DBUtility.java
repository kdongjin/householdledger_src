package Controller;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtility {
	public static Connection getConnection() {
		Connection con = null;
		// 1. MySql database class 로드한다.
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 2. 주소 아이디 비밀번호를 통해 접속요청한다.
			con = DriverManager.getConnection("jdbc:mysql://192.168.0.207/householdledgerdb", "root", "123456");
			// MainController.callAlert("연결성공 : 데이터베이스 연결성공");
		} catch (Exception e) {
			MainController.callAlert("연결실패: 데이타베이스 문제있음 점검바람");
			return null;
		}
		return con;
	}
}
