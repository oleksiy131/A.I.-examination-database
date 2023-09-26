package edu.sru.thangiah.domain;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "schedule_manager", uniqueConstraints = @UniqueConstraint(columnNames = "scheduleManagerId"))
public class ScheduleManager {

	@Id
	private Long scheduleManagerId;  // Fixed typo

	@Column(name = "password", nullable = false)
	private String smPassword;

	@Column(name = "username", nullable = false)
	private String smUsername;

	public Long getScheduleManagerId() {
		return scheduleManagerId;
	}

	public String getSmPassword() {
		return smPassword;
	}

	public String getSmUsername() {
		return smUsername;
	}

	public void setScheduleManagerId(Long scheduleManagerId) {
		this.scheduleManagerId = scheduleManagerId;
	}

	public void setSmPassword(String smPassword) {
		this.smPassword = smPassword;
	}

	public void setSmUsername(String smUsername) {
		this.smUsername = smUsername;
	}
	
	


}
