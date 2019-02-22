package Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import Controller.DBUtility;
import Controller.MainController;

public class InOutcomeListDAO {
	public static ArrayList<InOutcomeList> IOLtbl = new ArrayList<>();

	// 1. 수입지출 등록하는 함수
	public static int insertInOutcome(InOutcomeList iol) {
		int count = 0;

		// 1.1데이타베이스에 수입지출입력하는 쿼리문
		StringBuffer insertInOutcomeList = new StringBuffer();
		insertInOutcomeList.append("insert into inoutcomelist ");
		insertInOutcomeList
				.append("(no, userid, date ,hourminute, income, outcome, biggroup, smallgroup, payment, contents) ");
		insertInOutcomeList.append("values ");
		insertInOutcomeList.append("(?,?,?,?,?,?,?,?,?,?) ");

		// 1.2데이타베이스 커넥숀을 가져와야한다
		Connection con = null;

		// 1.3쿼리문을 실행해야할 Statement를 만들어야한다.
		PreparedStatement psmt = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(insertInOutcomeList.toString());
			// 1.4쿼리문에 실제데이타를 연결

			psmt.setInt(1, 0);
			psmt.setString(2, iol.getUserid());
			psmt.setDate(3, iol.getDate());
			psmt.setString(4, iol.getHourminute());
			psmt.setString(5, iol.getIncome());
			psmt.setString(6, iol.getOutcome());
			psmt.setString(7, iol.getBiggroup());
			psmt.setString(8, iol.getSmallgroup());
			psmt.setString(9, iol.getPayment());
			psmt.setString(10, iol.getContents());

			// 1-5실제데이터를 연결한 쿼리문을 실행한다.
			// executeUpdate(); 쿼리문을 실행해서 테이블에 저장을 할때 사용하는 번개문
			count = psmt.executeUpdate();
			if (count == 0) {
				MainController.callAlert("삽입 쿼리실패 : 삽입 쿼리문에 문제가 있습니다.");
				return count;
			}
		} catch (SQLException e) {
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
	public static ArrayList<InOutcomeList> getInOutcomeList() {

		// 2-1. 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문
		String selectInOutcomeList = "select * from inoutcomelist ";
		// 2-2. 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 2-3. 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 2-4. 쿼리문을 실행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 2-5. 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return null;
			}
			while (rs.next()) {
				InOutcomeList inOutcomeList = new InOutcomeList(rs.getInt(1), rs.getString(2), rs.getDate(3),
						rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
						rs.getString(9), rs.getString(10));
				IOLtbl.add(inOutcomeList);
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
		return IOLtbl;
	}

	// 3. 테이블뷰에서 선택한 레코드를 데이터베이스에서 삭제하는 함수
	public static int deleteInOutcomeListData(Integer no) {

		// 3-1. 데이터베이스 학생테이블에 있는 레코드를 삭제하는 쿼리문
		String deleteInOutcomeList = "delete from inOutcomeList where no =  ? ";
		// 3-2. 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 3-3. 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 3-4. 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		int count = 0;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(deleteInOutcomeList);
			psmt.setInt(1, no);
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

	// 4. 테이블에서 전체내용을 모두 가져오는 함수
	public static ArrayList<InOutcomeList> getDateList(String when, String userid) {
		IOLtbl.clear();
		// 4-1. 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문
		String selectInOutcomeList = "select * from inoutcomelist where date = '" + when + "' AND userid = '" + userid
				+ "'";
		// 4-2. 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 4-3. 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 4-4. 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 4-5. 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return null;
			}
			while (rs.next()) {
				InOutcomeList inOutcomeList = new InOutcomeList(rs.getInt(1), rs.getString(2), rs.getDate(3),
						rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
						rs.getString(9), rs.getString(10));
				IOLtbl.add(inOutcomeList);
			} // end of while
		} catch (SQLException e) {
			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
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
		return IOLtbl;
	}

	// 5. 테이블뷰터에서 수정한 레코드를 데이터베이스 테이블에 수정하는 함수.
	public static int updateInOutcomeData(InOutcomeList inOutcomeList) {
		int count = 0;
		// 5-1. 데이터베이스에 수입지출테이블에 수정하는 쿼리문
		StringBuffer updateInOutcomeList = new StringBuffer();
		updateInOutcomeList.append("update inOutcomeList set ");
		updateInOutcomeList
				.append("hourminute=?, income=?, outcome=?, biggroup=?, smallgroup=?,payment=?,contents=? where no=? ");
		// 5-2. 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 5-3. 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(updateInOutcomeList.toString());
			// 5-4. 쿼리문에 실제 데이터를 연결한다.

			psmt.setString(1, inOutcomeList.getHourminute());
			psmt.setString(2, inOutcomeList.getIncome());
			psmt.setString(3, inOutcomeList.getOutcome());
			psmt.setString(4, inOutcomeList.getBiggroup());
			psmt.setString(5, inOutcomeList.getSmallgroup());
			psmt.setString(6, inOutcomeList.getPayment());
			psmt.setString(7, inOutcomeList.getContents());
			psmt.setInt(8, inOutcomeList.getNo());
			// 5-5. 실제데이터를 연결한 쿼리문을 실행한다.
			// executeUpdate(); 쿼리문을 실행해서 테이블에 저장을 할때 사용하는 번개문
			count = psmt.executeUpdate();
			if (count == 0) {
				MainController.callAlert("update 쿼리실패 : update 쿼리문에 문제가 있습니다.");
				return count;
			}
		} catch (SQLException e) {
			MainController.callAlert("update 실패 : update 에 문제가 있습니다.");
		} finally {
			// 5-6. 자원객체를 닫아야한다.
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

	// 그년도의 월별 수입
	public static int getInComeDataOfYears(String year, String month) {
		int result = 0;
		// 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문

		String selectInOutcomeList = "select sum(income) from inoutcomelist where date like '" + year + "-" + month
				+ "-%%'";

		// 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return 0;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			} // end of while
		} catch (SQLException e) {
			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 자원객체를 닫아야한다.
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
		return result;
	}

	// 그년도의 월별 지출
	public static int getOutComeDataOfYears(String year, String month) {
		int result = 0;
		// 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문
		String selectInOutcomeList = "select sum(outcome) from inoutcomelist where date like '" + year + "-" + month
				+ "-%%' ";

		// 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;

		// 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return 0;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			} // end of while
		} catch (SQLException e) {

			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 자원객체를 닫아야한다.
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
		return result;
	}

	// 그년도의 분류별
	public static int Categorize(String year, String Coutcome) {
		int result = 0;
		// 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문
		String selectInOutcomeList = "select count(biggroup) from inoutcomelist where biggroup like '" + Coutcome
				+ "' AND date like '" + year + "-%%-%%'";
		// 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;

		// 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return 0;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			} // end of while
		} catch (SQLException e) {

			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 자원객체를 닫아야한다.
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
		return result;
	}

	// 결제수단별-수입
	public static int Payment0(String year, String Payment) {
		int result = 0;
		// 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문
		String selectInOutcomeList = "select count(income) from inoutcomelist where payment like '" + Payment
				+ "' AND date like '" + year + "-%%-%%'";
		// 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;

		// 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return 0;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			} // end of while
		} catch (SQLException e) {

			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 자원객체를 닫아야한다.
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
		return result;
	}

	// 결제수단별-수입
	public static int Payment1(String year, String Payment) {
		int result = 0;
		// 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문
		String selectInOutcomeList = "select count(outcome) from inoutcomelist where payment like '" + Payment
				+ "' AND date like '" + year + "-%%-%%'";
		// 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;

		// 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return 0;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			} // end of while
		} catch (SQLException e) {

			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 자원객체를 닫아야한다.
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
		return result;
	}

	// 잔액
	public static int balanceIncome(String income) {
		int result = 0;
		// 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문

		String selectInOutcomeList = "select sum(income) from inoutcomelist;";

		// 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return 0;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			} // end of while
		} catch (SQLException e) {
			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 자원객체를 닫아야한다.
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
		return result;
	}

	public static int balanceOutcome(String outcome) {
		int result = 0;
		// 데이터베이스 수입지출테이블에 있는 레코드를 모두 가져오는 쿼리문

		String selectInOutcomeList = "select sum(outcome) from inoutcomelist;";

		// 데이터베이스 Connection을 가져와야 한다.
		Connection con = null;
		// 쿼리문을 실행해야할 Statement를 만들어야 한다.
		PreparedStatement psmt = null;
		// 쿼리문을 싱행하고나서 가져와야할 레코드를 담고있는 보자기 객체
		ResultSet rs = null;
		try {
			con = DBUtility.getConnection();
			psmt = con.prepareStatement(selectInOutcomeList);
			// 실제데이터를 연결한 쿼리문을 실행한다.(번개를 치는것)
			// executeQuery(); 쿼리문을 실행해서 결과를 가져올때 사용하는 번개문
			rs = psmt.executeQuery();
			if (rs == null) {
				MainController.callAlert("select 실패 : select에 문제가 있습니다.");
				return 0;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			} // end of while
		} catch (SQLException e) {
			MainController.callAlert("삽입 실패 : 데이터베이스 삽입에 문제가 있습니다.");
		} finally {
			// 자원객체를 닫아야한다.
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
		return result;
	}

}