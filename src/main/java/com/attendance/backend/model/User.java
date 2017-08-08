package com.attendance.backend.model;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

@Entity
public class User {

    private @Id @GeneratedValue Long id;
	private @NotNull String username;
	private String name;
	private @NotNull @Email String email;
	private @Email String oldMail;
	private String password;
    private @ManyToOne Center defaultCenter;
    private UserStatus status = UserStatus.NEW;
    private String accessToken;

    private @Transient List<Center> centers = new ArrayList<Center>();
    private @Transient List<Sharing> sharings = new ArrayList<Sharing>();
    private @Transient Center currentCenter;

    protected User() {
	}

	public User(Long id) {
		this.id = id;
	}

	public User(String username, String name, String email, String password) {
		this.username = username;
		this.name = name;
		this.email = email;
		this.password = User.getEncryptedPassword(password);
	}
	
	public User(String email) {
		this.email = email;
	}

	public static String getPasswordRegexp(){
		//Reference: https://stackoverflow.com/questions/3802192/regexp-java-for-password-validation
		//^                 # start-of-string
		//(?=.*[0-9])       # a digit must occur at least once
		//(?=.*[a-z])       # a lower case letter must occur at least once
		//(?=\S+$)          # no whitespace allowed in the entire string
		//.{6,}             # anything, at least six places though
		//$                 # end-of-string
		return "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,}$";
	}
	
	public static String getEncryptedPassword(String password) {
		String result = "";
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(password.getBytes("UTF8"));
			byte s[] = m.digest();
			for (int i = 0; i < s.length; i++) {
				result += Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Center getCurrentCenter(){

		if(this.currentCenter == null && this.centers.isEmpty())
			return this.currentCenter; 
		
		if(this.currentCenter != null)
			return this.currentCenter;
		
		for (Center center : this.centers) {
			if(center.isCurrentUserDefault()){
				this.currentCenter = center;
				return this.currentCenter;
			}
		}
		
		this.currentCenter = this.centers.get(0);
		return this.currentCenter;
	}
	
	public boolean hasSharing(Center center, SharingType type){
		return this.hasSharing(center, type, null);
	}
	
	public boolean hasSharing(Center center, SharingType type, Activity activity){
		if(type == null || center == null)
			return false;
		
		for(Sharing s : this.sharings){
			
			if( (activity == null && !s.isSharingCenter()) || (activity != null && s.isSharingCenter()) )
				continue;

			if(s.getCenter().getId() != center.getId() || s.getType() != type) 
				continue;

			if(!s.isSharingCenter() && s.getActivity().getId() != activity.getId()) 
				continue;

			return true;
		}

		return false;
	}

	public boolean isInterceptedLogin(){
		return status == UserStatus.NEW || status == UserStatus.NEW_BY_SHARING || status == UserStatus.CHANGED_MAIL;
	}
	
	//accessors
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Center getDefaultCenter() {
		return defaultCenter;
	}

	public void setDefaultCenter(Center defaultCenter) {
		this.defaultCenter = defaultCenter;
	}

	public List<Center> getCenters() {
		return centers;
	}

	public void setCenters(List<Center> centers) {
		this.centers = centers;
	}

	public void setCurrentCenter(Center currentCenter) {
		this.currentCenter = currentCenter;
	}

	public List<Sharing> getSharings() {
		return sharings;
	}

	public void setSharings(List<Sharing> sharings) {
		this.sharings = sharings;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getOldMail() {
		return oldMail;
	}

	public void setOldMail(String oldMail) {
		this.oldMail = oldMail;
	}
}
