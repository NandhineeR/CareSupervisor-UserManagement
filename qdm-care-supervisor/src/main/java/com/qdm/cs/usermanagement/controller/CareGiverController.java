package com.qdm.cs.usermanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qdm.cs.usermanagement.constants.ResponseConstants;
import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareGiver;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.response.ResponseInfo;
import com.qdm.cs.usermanagement.response.ResponseType;
import com.qdm.cs.usermanagement.service.CareGiverService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = { "/careGiver" })
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
@Slf4j
public class CareGiverController {

	@Autowired
	CareGiverService careGiverService;

	@PostMapping(value = "/addCareGiver", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> addCareGiver(FormDataDTO formDataDTO) {
		ResponseEntity response = null;
		try {
			CareGiver careGiver = careGiverService.addCareGiver(formDataDTO);
			log.info("Care Giver Created Successfully With CareGiver_Id : " + careGiver.getCareGiverId());
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.CREATED);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At AddCareGiver : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/getCareGiver", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getCareGiver(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		ResponseEntity response = null;
		try {
			List<CareGiver> careGiverList = careGiverService.getCareGiver(pageNo, pageSize);
			List<Object> careGiverRecords = new ArrayList<>();
			Map<String, Object> careGiverResponse = new HashMap<>();
			for (CareGiver careGiver : careGiverList) {
				List<Category> category = careGiverService.getCategoryListById(careGiver.getCategory());
				List<Object> categoryList = new ArrayList<>();
				for (Category categoryData : category) {
					if (categoryData != null) {
						Map<String, Object> categoryMap = new HashMap<>();
						categoryMap.put("label", categoryData.getCategoryName());
						categoryMap.put("value", categoryData.getCategoryId());
						categoryList.add(categoryMap);
					}
				}

				JSONObject obj = new JSONObject();
				obj.put("count", careGiver.getClientsCount());
				obj.put("name", "Clients");

				JSONArray jsonarr = new JSONArray();
				jsonarr.add(obj);
				careGiverResponse.put("total_count", pageSize);
				careGiverResponse.put("offset", pageNo);

				Map<String, Object> careGiverDatas = new HashMap<>();
				careGiverDatas.put("id", careGiver.getCareGiverId());
				careGiverDatas.put("name", careGiver.getCareGiverName());
				careGiverDatas.put("isactive", careGiver.getActiveStatus());
				careGiverDatas.put("service", "");
				careGiverDatas.put("profile_pic", careGiver.getUploadPhoto().getData());
				careGiverDatas.put("category", categoryList);
				careGiverDatas.put("orderList", jsonarr);
				careGiverRecords.add(careGiverDatas);
				careGiverResponse.put("list", careGiverRecords);
			}
			log.info("Care Givers List " + careGiverResponse);
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", careGiverResponse), HttpStatus.OK);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At getCareGiver : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/getCareGiverQuickSummary/{careGiverId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getCareGiverById(@PathVariable("careGiverId") int careGiverId) {
		ResponseEntity response = null;
		try {
			Optional<CareGiver> careGiver = careGiverService.getCareGiverById(careGiverId);
			Map<String, Object> careGiverRecords = new HashMap<>();
			if (careGiver.isPresent()) {
				CareGiver careGiverList = careGiver.get();
				careGiverRecords.put("Care_Giver_ID", careGiverList.getCareGiverId());
				careGiverRecords.put("Care_Giver_Name", careGiverList.getCareGiverName());
				careGiverRecords.put("Availability", careGiverList.getActiveStatus());
				careGiverRecords.put("Mobile_No", careGiverList.getMobileNo());
				careGiverRecords.put("Email_ID", careGiverList.getEmailId());
				careGiverRecords.put("Address", careGiverList.getAddress());
				careGiverRecords.put("Skills", careGiverList.getSkills());
				careGiverRecords.put("Clients_Count", careGiverList.getClientsCount());
				careGiverRecords.put("Profile_Picture", careGiverList.getUploadPhoto().getData());
				log.info("Get CareGiver Record By CareGiverId : " + careGiverId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", careGiverRecords), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareCoordinator Found with Id : " + careGiverId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
						ResponseType.NOT_FOUND.getResponseCode(), "", careGiverRecords), HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At getCareGiverQuickSummary : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/editCareGiverDetails/{careGiverId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> editCareGiverDetails(@PathVariable("careGiverId") int careGiverId) {
		ResponseEntity response = null;
		try {
			Optional<CareGiver> careGiver = careGiverService.getCareGiverById(careGiverId);
			Map<String, Object> careGiverRecord = new HashMap<>();
			if (careGiver.isPresent()) {
				CareGiver careGiverList = careGiver.get();
				List<Category> category = careGiverService.getCategoryListById(careGiverList.getCategory());
				List<Object> categoryList = new ArrayList<>();
				for (Category categoryData : category) {
					if (categoryData != null) {
						Map<String, Object> categoryMap = new HashMap<>();
						categoryMap.put("label", categoryData.getCategoryName());
						categoryMap.put("value", categoryData.getCategoryId());
						categoryList.add(categoryMap);
					}
				}

				careGiverRecord.put("id", careGiverList.getCareGiverId());
				careGiverRecord.put("name", careGiverList.getCareGiverName());
				careGiverRecord.put("isactive", careGiverList.getActiveStatus());
				careGiverRecord.put("profile_pic", careGiverList.getUploadPhoto().getData());
				careGiverRecord.put("mobile_no", careGiverList.getMobileNo());
				careGiverRecord.put("email", careGiverList.getEmailId());
				careGiverRecord.put("address", careGiverList.getAddress());
				careGiverRecord.put("clients_count", careGiverList.getClientsCount());
				careGiverRecord.put("upcoming_activities_count", "");
				careGiverRecord.put("average_rating", "");
				careGiverRecord.put("upcoming_activities_count", "");
				careGiverRecord.put("skills", careGiverList.getSkills());
				careGiverRecord.put("category", categoryList);
				careGiverRecord.put("care_provider", "");

				log.info("Get CareGiver Records By CareGiverId " + careGiverId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", careGiverRecord), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareGiver Found with Id : " + careGiverId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
						ResponseType.NOT_FOUND.getResponseCode(), "", careGiverRecord), HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At editCareGiverDetails : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping(value = "/updateCareGiver", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> updateCareGiver(FormDataDTO formDataDTO) throws IOException {
		ResponseEntity response = null;

		if (formDataDTO.getCareGiverId() == 0) {
			log.error(ResponseConstants.Care_Giver_Id);
			response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
					ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
			return response;
		}

		try {
			CareGiver careGiver = careGiverService.updateCareGiver(formDataDTO);
			if (careGiver != null) {
				log.info("CareGiver Updated Successfully with Id " + careGiver.getCareGiverId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareGiver Found with Id :  " + formDataDTO.getCareGiverId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At UpdateCareGiver : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}

	}

	@PutMapping(value = "/updateClientsCount/{careGiverId}/{clientsCount}", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> updateClientsCount(@PathVariable("careGiverId") long careGiverId,
			@PathVariable("clientsCount") int clientsCount) {
		ResponseEntity response = null;
		try {
			CareGiver careGiver = careGiverService.updateClientsCount(careGiverId, clientsCount);
			if (careGiver != null) {
				log.info("CareGiver Clients Count Successfully ");
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareCoordinator Found with Id :  " + careGiverId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
						ResponseType.NOT_FOUND.getResponseCode(), "", null), HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateClientsCount : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping(value = "/updateCareGiverAvailabilityStatus/{careGiverId}/{activeStatus}", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> updateClientsActiveStatus(@PathVariable("careGiverId") long careGiverId,
			@PathVariable("activeStatus") Status activeStatus) {
		ResponseEntity response = null;
		try {
			CareGiver careGiver = careGiverService.updateClientsActiveStatus(careGiverId, activeStatus);
			if (careGiver != null) {
				log.info("Updated Availability Status Successfully ");
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareCoordinator Found with Id : " + careGiverId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateClientsActiveStatus : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}
}
