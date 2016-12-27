package hello;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "person")
public class Person {

	@Id
	private Long id;
	private String name;
	private String mail;
	private String phone;
	private Date birthday;
	private Long entity_id;
	private String tag1;
	private String tag2;
	private String tag3;
	private String tag4;
	private String wa_guest;
	private String wa_entered;
	private String wa_status;
	private String wa_comment;

	protected Person() {
	}

	public Person(long id) {
		this.id = id;
	}

	public Person(String name, Long entity_id) {
		this.name = name;
		this.entity_id = entity_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Long getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(Long entity_id) {
		this.entity_id = entity_id;
	}

	public String getTag1() {
		return tag1;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public String getTag2() {
		return tag2;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public String getTag3() {
		return tag3;
	}

	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}

	public String getTag4() {
		return tag4;
	}

	public void setTag4(String tag4) {
		this.tag4 = tag4;
	}

	public String getWa_guest() {
		return wa_guest;
	}

	public void setWa_guest(String wa_guest) {
		this.wa_guest = wa_guest;
	}

	public String getWa_entered() {
		return wa_entered;
	}

	public void setWa_entered(String wa_entered) {
		this.wa_entered = wa_entered;
	}

	public String getWa_status() {
		return wa_status;
	}

	public void setWa_status(String wa_status) {
		this.wa_status = wa_status;
	}

	public String getWa_comment() {
		return wa_comment;
	}

	public void setWa_comment(String wa_comment) {
		this.wa_comment = wa_comment;
	}
}
