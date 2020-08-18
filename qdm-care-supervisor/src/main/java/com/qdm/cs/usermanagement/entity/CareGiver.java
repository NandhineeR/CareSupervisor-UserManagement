package com.qdm.cs.usermanagement.entity;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "TB_CARE_GIVER")
public class CareGiver extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CareGiver_Id")
	long careGiverId;

	@Column(name = "CareGiver_Name")
	String careGiverName;

	@ElementCollection
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "Category_Id")
	@JoinTable(name = "TB_GIVER_CATEGORY")
	Collection<Integer> category;

	@ElementCollection
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "Skills")
	@JoinTable(name = "TB_GIVER_SKILLS")
	Collection<String> skills;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "Upload_Photo_Id")
	UploadProfile uploadPhoto;

	@Column(name = "Clients_Count")
	int clientsCount;

}
