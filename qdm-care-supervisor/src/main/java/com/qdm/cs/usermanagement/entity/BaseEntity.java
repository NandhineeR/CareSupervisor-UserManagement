package com.qdm.cs.usermanagement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.qdm.cs.usermanagement.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "Mobile_No")
	long mobileNo;

	@Column(name = "Email_Id")
	String emailId;

	@Column(name = "Address")
	String address;

	@Enumerated(EnumType.STRING)
	@Column(name = "Availability_Status")
	Status activeStatus;

	@CreatedDate
	@Column(name = "Created_Date", updatable = false)
	private Date createdDate;

	@LastModifiedDate
	@Column(name = "Modified_Date")
	private Date lastModifiedDate;
}
