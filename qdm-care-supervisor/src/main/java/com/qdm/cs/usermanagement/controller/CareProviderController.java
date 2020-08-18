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
import com.qdm.cs.usermanagement.entity.CareProvider;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.response.ResponseInfo;
import com.qdm.cs.usermanagement.response.ResponseType;
import com.qdm.cs.usermanagement.service.CareProviderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = { "/careProvider" })
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
@Slf4j
public class CareProviderController {
	@Autowired
	CareProviderService careProviderService;

	@PostMapping(value = "/addCareProvider", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> addCareProvider(FormDataDTO formDataDTO) throws IOException {
		ResponseEntity response = null;
		try {
			CareProvider careProvider = careProviderService.addCareProvider(formDataDTO);
			log.info("Created Care Provider Successfully With ID : " + careProvider.getCareProviderId());
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.CREATED);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At addCareProvider : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/getCareProvider", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getCareProvider(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		ResponseEntity response = null;
		try {
			List<CareProvider> careProviderList = careProviderService.getCareProvider(pageNo,pageSize);
			List<Object> careProviderRecords = new ArrayList<>();
			Map<String, Object> careProvidersResponse = new HashMap<>();
			
			for (CareProvider careProvider : careProviderList) {	
				List<Category> category = careProviderService.getCategoryListById(careProvider.getCategory());
				List<Object> categoryList = new ArrayList<>();
				for (Category categoryData : category) {
					if (categoryData != null) {
						Map<String, Object> categoryMap = new HashMap<>();
						categoryMap.put("label", categoryData.getCategoryName());
						categoryMap.put("value", categoryData.getCategoryId());
						categoryList.add(categoryMap);
					}
				}
				
				JSONObject careGivers = new JSONObject();
				careGivers.put("count", careProvider.getCareGiversCount());
				careGivers.put("careGivers", "CareGivers");
				
				JSONObject products = new JSONObject();
				products.put("count", careProvider.getProductsCount());
				products.put("careGivers", "Products");
				
				JSONObject offers = new JSONObject();
				offers.put("count", careProvider.getOffersCount());
				offers.put("careGivers", "Offers");

				JSONArray jsonarr = new JSONArray();
				jsonarr.add(careGivers);
				jsonarr.add(products);
				jsonarr.add(offers);
				
				
				
				careProvidersResponse.put("total_count", pageSize);
				careProvidersResponse.put("offset", pageNo);
				
				Map<String, Object> careProvidersData = new HashMap<>();
				careProvidersData.put("id", careProvider.getCareProviderId());
				careProvidersData.put("name", careProvider.getCareProviderName());
				careProvidersData.put("isactive",  careProvider.getActiveStatus());
				careProvidersData.put("service", "");
				careProvidersData.put("profile_pic", careProvider.getUploadPhoto().getData());
				careProvidersData.put("category", categoryList);
				careProvidersData.put("orderList", jsonarr);
				careProviderRecords.add(careProvidersData);
				careProvidersResponse.put("list", careProviderRecords);
				
			}
			log.info("Get All CareProviders Records");
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", careProvidersResponse), HttpStatus.OK);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At getCareProvider : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping("/getCareProviderQuickSummary/{careProviderId}")
	public ResponseEntity<?> getCareProviderById(@PathVariable("careProviderId") long careProviderId) {
		ResponseEntity response = null;
		try {
			Optional<CareProvider> careProvider = careProviderService.getCareProviderById(careProviderId);
			Map<String, Object> careProvidersRecords = new HashMap<>();
			if (careProvider.isPresent()) {
				CareProvider careProviderList = careProvider.get();

				careProvidersRecords.put("Care_Provider_ID", careProviderList.getCareProviderId());
				careProvidersRecords.put("Care_Provider_Name", careProviderList.getCareProviderName());
				careProvidersRecords.put("InCharges_Name", careProviderList.getInChargesName());
				careProvidersRecords.put("Mobile_No", careProviderList.getMobileNo());
				careProvidersRecords.put("Email_ID", careProviderList.getEmailId());
				careProvidersRecords.put("Address", careProviderList.getAddress());
				careProvidersRecords.put("Offerings", careProviderList.getOfferings());
				careProvidersRecords.put("Products", "");
				careProvidersRecords.put("Profile_Picture", careProviderList.getUploadPhoto().getData());
				log.info("Get CareProviders Records By CareProviderId ");
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", careProvidersRecords), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareProvider Found with Id : " + careProviderId);
				response = new ResponseEntity(
						new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
								ResponseType.NOT_FOUND.getResponseCode(), "", careProvidersRecords),
						HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At getCareProviderById : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/editCareProviderDetails/{careProviderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> editCareProviderDetails(@PathVariable("careProviderId") int careProviderId) {
		ResponseEntity response = null;
		try {
			Optional<CareProvider> careProvider = careProviderService.getCareProviderById(careProviderId);
			Map<String, Object> careProviderRecord = new HashMap<>();
			if (careProvider.isPresent()) {
				CareProvider careProviderList = careProvider.get();
				List<Category> category = careProviderService.getCategoryListById(careProviderList.getCategory());

				List<Object> categoryList = new ArrayList<>();
				for (Category categoryData : category) {
					if (categoryData != null) {
						Map<String, Object> categoryMap = new HashMap<>();
						categoryMap.put("label", categoryData.getCategoryName());
						categoryMap.put("value", categoryData.getCategoryId());
						categoryList.add(categoryMap);
					}
				}
							
				System.out.println("Offers "+careProviderList.getOfferings() +"  "+careProviderList.getSkills());
				careProviderRecord.put("id", careProviderList.getCareProviderId());
				careProviderRecord.put("name",  careProviderList.getCareProviderName());
				careProviderRecord.put("isactive", careProviderList.getActiveStatus());
				careProviderRecord.put("profile_pic", careProviderList.getUploadPhoto().getData());
				careProviderRecord.put("service", "");
				careProviderRecord.put("incharge_name", careProviderList.getInChargesName());
				careProviderRecord.put("mobile_no", careProviderList.getMobileNo());
				careProviderRecord.put("email", careProviderList.getEmailId());
				careProviderRecord.put("address", careProviderList.getAddress());
				careProviderRecord.put("product_list", "");
				careProviderRecord.put("offerings_list", careProviderList.getOfferings());
				careProviderRecord.put("care_giver_count", careProviderList.getCareGiversCount());
				careProviderRecord.put("skills", careProviderList.getSkills());
				careProviderRecord.put("category", categoryList);
		
				log.info("Get CareProvider Records By CareProviderId " + careProviderId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", careProviderRecord), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareProvider Found with Id : " + careProviderId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
						ResponseType.NOT_FOUND.getResponseCode(), "", careProviderRecord), HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At editCareProviderDetails : " + e.getMessage());

			response = new ResponseEntity<Object>(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping(value = "/updateCareProvider", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> updateCareGProvider(FormDataDTO formDataDTO) throws IOException {
		ResponseEntity response = null;
		if (formDataDTO.getCareProviderId() == 0) {
			log.info(ResponseConstants.Care_Provider_Id);
			response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
					ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
			return response;
		}
		try {
			CareProvider careProvider = careProviderService.updateCareProvider(formDataDTO);
			if (careProvider != null) {
				log.info("Updated Care Provider With Id : " + careProvider.getCareProviderId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareProvider Found with Id : " + formDataDTO.getCareProviderId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateCareProvider : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping("/updateClientsActiveStatus/{careProviderId}/{activeStatus}")
	public ResponseEntity<?> updateClientsActiveStatus(@PathVariable("careProviderId") long careProviderId,
			@PathVariable("activeStatus") Status activeStatus) {
		ResponseEntity response = null;
		try {
			CareProvider careProvider = careProviderService.updateClientsActiveStatus(careProviderId, activeStatus);
			if (careProvider != null) {
				log.info("Updated Availability Status Successfully With Id : " + careProvider.getCareProviderId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareProvider Found with Id : " + careProviderId);
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

	@PutMapping("/updateCareGiversCount/{careProviderId}/{careGiversCount}")
	public ResponseEntity<?> updateCareGiversCount(@PathVariable("careProviderId") long careProviderId,
			@PathVariable("careGiversCount") int careGiversCount) {
		ResponseEntity response = null;
		try {
			CareProvider careProvider = careProviderService.updateCareGiversCount(careProviderId, careGiversCount);
			if (careProvider != null) {
				log.info("Updated CareGiversCount Successfully With Id : " + careProvider.getCareProviderId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareProvider Found with Id : " + careProviderId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateCareGiversCount : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping("/updateProductsCount/{careProviderId}/{productsCount}")
	public ResponseEntity<?> updateProductsCount(@PathVariable("careProviderId") long careProviderId,
			@PathVariable("productsCount") int productsCount) {
		ResponseEntity response = null;
		try {
			CareProvider careProvider = careProviderService.updateProductsCount(careProviderId, productsCount);
			if (careProvider != null) {
				log.info("Updated ProductsCount Successfully With Id : " + careProvider.getCareProviderId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareProvider Found with Id : " + careProviderId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateProductsCount : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}
}
