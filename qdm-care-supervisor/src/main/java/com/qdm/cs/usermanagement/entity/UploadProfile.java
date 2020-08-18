package com.qdm.cs.usermanagement.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "TB_UPLOAD_PROFILE")
public class UploadProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Upload_Photo_Id")
	private int id;

	@Column(name = "FileName")
	private String fileName;

	@Column(name = "FileType")
	private String fileType;

	@Lob
	@Column(name = "Data")
	private byte[] data;

	@Column(name = "File_Size")
	private long size;

}
