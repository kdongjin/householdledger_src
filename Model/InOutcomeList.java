package Model;

import java.sql.Date;

public class InOutcomeList {
	private Integer no;
	private String userid;
	private Date date;
	private String hourminute;
	private String income;
	private String outcome;
	private String biggroup;
	private String smallgroup;
	private String payment;
	private String contents;

	public InOutcomeList(Integer no, String userid, Date date, String hourminute, String income, String outcome,
			String biggroup, String smallgroup, String payment, String contents) {
		super();
		this.no = no;
		this.userid = userid;
		this.date = date;
		this.hourminute = hourminute;
		this.income = income;
		this.outcome = outcome;
		this.biggroup = biggroup;
		this.smallgroup = smallgroup;
		this.payment = payment;
		this.contents = contents;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getHourminute() {
		return hourminute;
	}

	public void setHourminute(String hourminute) {
		this.hourminute = hourminute;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getBiggroup() {
		return biggroup;
	}

	public void setBiggroup(String biggroup) {
		this.biggroup = biggroup;
	}

	public String getSmallgroup() {
		return smallgroup;
	}

	public void setSmallgroup(String smallgroup) {
		this.smallgroup = smallgroup;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

}