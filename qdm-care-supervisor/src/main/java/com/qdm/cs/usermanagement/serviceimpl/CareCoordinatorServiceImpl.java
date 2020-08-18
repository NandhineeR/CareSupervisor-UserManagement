package com.qdm.cs.usermanagement.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareCoordinator;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.repository.CareCoordinatorRepository;
import com.qdm.cs.usermanagement.repository.CategoryRepository;
import com.qdm.cs.usermanagement.repository.UploadProfileRepository;
import com.qdm.cs.usermanagement.service.CareCoordinatorService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CareCoordinatorServiceImpl implements CareCoordinatorService {

	CareCoordinatorRepository careCoordinatorRepository;
	CategoryRepository categoryRepository;
	ModelMapper modelMapper;
	UploadProfileRepository uploadProfileRepository;

	@Autowired
	public CareCoordinatorServiceImpl(CareCoordinatorRepository careCoordinatorRepository,
			CategoryRepository categoryRepository, ModelMapper modelMapper,
			UploadProfileRepository uploadProfileRepository) {
		super();
		this.careCoordinatorRepository = careCoordinatorRepository;
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
		this.uploadProfileRepository = uploadProfileRepository;
	}

	@Override
	public List<CareCoordinator> getCareCoordinator(Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<CareCoordinator> pagedResult = careCoordinatorRepository.findAll(paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareCoordinator>();
	}

	@Override
	public Optional<CareCoordinator> getCareCoordinatorById(long careCoordinatorId) {
		return careCoordinatorRepository.findById(careCoordinatorId);
	}

	@Override
	public CareCoordinator updateClientsCount(long careCoordinatorId, int clientsCount) {
		Optional<CareCoordinator> careGiverUpdateClientsCount = careCoordinatorRepository.findById(careCoordinatorId);
		if (careGiverUpdateClientsCount.isPresent()) {
			careGiverUpdateClientsCount.get().setClientsCount(clientsCount);
			return careCoordinatorRepository.save(careGiverUpdateClientsCount.get());
		}
		return careGiverUpdateClientsCount.get();
	}

	@Override
	public CareCoordinator updateClientsActiveStatus(long careCoordinatorId, Status activeStatus) {
		Optional<CareCoordinator> careGiverUpdateData = careCoordinatorRepository.findById(careCoordinatorId);
		if (careGiverUpdateData.isPresent()) {
			careGiverUpdateData.get().setCareCoordinatorId(careCoordinatorId);
			careGiverUpdateData.get().setActiveStatus(activeStatus);
			return careCoordinatorRepository.save(careGiverUpdateData.get());
		}
		return careGiverUpdateData.get();
	}

	@Override
	public CareCoordinator updateCareGiversCount(long careCoordinatorId, int careGiversCount) {
		Optional<CareCoordinator> careGiverUpdateClientsCount = careCoordinatorRepository.findById(careCoordinatorId);
		if (careGiverUpdateClientsCount.isPresent()) {
			careGiverUpdateClientsCount.get().setCareCoordinatorId(careCoordinatorId);
			careGiverUpdateClientsCount.get().setCareGiversCount(careGiversCount);
			return careCoordinatorRepository.save(careGiverUpdateClientsCount.get());
		}
		return careGiverUpdateClientsCount.get();
	}

	@Override
	public List<Category> getCategoryListById(Collection<Integer> category) {
		List<Category> data = new ArrayList<>();
		for (Integer categoryData : category) {
			Category categoryList = categoryRepository.findByCategoryId(categoryData);
			data.add(categoryList);
		}
		return data;
	}

	@Override
	public CareCoordinator addCareCoordinator(FormDataDTO formDataDTO) throws IOException {
		CareCoordinator careCoordinator = modelMapper.map(formDataDTO, CareCoordinator.class);
		String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
		UploadProfile uploadProfile = UploadProfile.builder().fileName(fileName)
				.fileType(formDataDTO.getUploadPhoto().getContentType()).data(formDataDTO.getUploadPhoto().getBytes())
				.size(formDataDTO.getUploadPhoto().getSize()).build();
		careCoordinator.setUploadPhoto(uploadProfile);
		return careCoordinatorRepository.save(careCoordinator);
	}

	@Override
	public CareCoordinator updateCareCoordinator(FormDataDTO formDataDTO) {
		Optional<CareCoordinator> careCoordinatorUpdateData = careCoordinatorRepository
				.findById(formDataDTO.getCareCoordinatorId());
		int uploadProfileId = careCoordinatorUpdateData.get().getUploadPhoto().getId();
		if (careCoordinatorUpdateData.isPresent()) {
			CareCoordinator careCoordinator = modelMapper.map(formDataDTO, CareCoordinator.class);
			try {
				careCoordinator.getUploadPhoto().setFileName(formDataDTO.getUploadPhoto().getOriginalFilename());
				careCoordinator.getUploadPhoto().setFileType(formDataDTO.getUploadPhoto().getContentType());
				careCoordinator.getUploadPhoto().setData(formDataDTO.getUploadPhoto().getBytes());
				careCoordinator.getUploadPhoto().setSize(formDataDTO.getUploadPhoto().getSize());
			} catch (IOException e) {
				log.error("Error Occured In Care Coordinator Service updateCareCoordinator With Id "
						+ careCoordinator.getCareCoordinatorId());
			}
			CareCoordinator careCoordinatorUpdated = careCoordinatorRepository.save(careCoordinator);
			uploadProfileRepository.deleteById(uploadProfileId);
			return careCoordinatorUpdated;
		}
		return careCoordinatorUpdateData.get();
	}
}
