package com.qdm.cs.usermanagement.dto;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.qdm.cs.usermanagement.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class FormDataDTO {

	long careGiverId;
	long careCoordinatorId;
	long careProviderId;
	MultipartFile uploadPhoto;
	String careGiverName;
	String careCoordinatorName;
	String careProviderName;
	List<Integer> category;
	long mobileNo;
	String emailId;
	String address;
	List<String> skills;
	List<String> offerings;
	Status activeStatus;
	int clientsCount;
	int careGiversCount;
	String inChargesName;
}
