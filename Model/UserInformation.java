package Model;

public class UserInformation {
	private String userid;
	private String password;
	private String cmbfavoritegroup;
	private String cmbfavoriteanswer;
	private String phonenumber;

	public UserInformation(String userid, String password, String cmbfavoritegroup, String cmbfavoriteanswer,
			String phonenumber) {
		this.userid = userid;
		this.password = password;
		this.cmbfavoritegroup = cmbfavoritegroup;
		this.cmbfavoriteanswer = cmbfavoriteanswer;
		this.phonenumber = phonenumber;
	}

	public String getUserid() {
		return userid;
	}

	@Override
	public String toString() {
		return "UserInformation [userid=" + userid + ", password=" + password + ", cmbfavoritegroup=" + cmbfavoritegroup
				+ ", cmbfavoriteanswer=" + cmbfavoriteanswer + ", phonenumber=" + phonenumber + "]";
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCmbfavoritegroup() {
		return cmbfavoritegroup;
	}

	public void setCmbfavoritegroup(String cmbfavoritegroup) {
		this.cmbfavoritegroup = cmbfavoritegroup;
	}

	public String getCmbfavoriteanswer() {
		return cmbfavoriteanswer;
	}

	public void setCmbfavoriteanswer(String cmbfavoriteanswer) {
		this.cmbfavoriteanswer = cmbfavoriteanswer;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

}
