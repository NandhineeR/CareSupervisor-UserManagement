package com.qdm.cs.usermanagement.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
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
@Table(name = "TB_CARE_PROVIDER")
public class CareProvider extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CareProvider_Id")
	long careProviderId;

	@Column(name = "CareProvider_Name")
	String careProviderName;

	@Column(name = "InCharges_Name")
	String inChargesName;

	@Column(name = "CareGivers_Count")
	int careGiversCount;

	@Column(name = "Products_Count")
	int productsCount;

	@Column(name = "Offers_Count")
	int offersCount;

	@ElementCollection
	@Basic(fetch = FetchType.EAGER)
	@CollectionTable(name = "TB_PROVIDER_OFFERINGS_LIST", joinColumns = @JoinColumn(name = "CareProvider_Id"))
	List<String> offerings;

	@ElementCollection
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "Category_Id")
	@JoinTable(name = "TB_PROVIDER_CATEGORY")
	Collection<Integer> category;

	@ElementCollection
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "Skills")
	@JoinTable(name = "TB_PROVIDER_SKILLS")
	Collection<String> skills;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "Upload_Photo_Id")
	UploadProfile uploadPhoto;

}
