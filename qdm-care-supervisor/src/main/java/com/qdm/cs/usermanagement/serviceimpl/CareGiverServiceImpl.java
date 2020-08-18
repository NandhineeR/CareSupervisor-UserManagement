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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareGiver;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.repository.CareGiverRepository;
import com.qdm.cs.usermanagement.repository.CategoryRepository;
import com.qdm.cs.usermanagement.repository.UploadProfileRepository;
import com.qdm.cs.usermanagement.service.CareGiverService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CareGiverServiceImpl implements CareGiverService {

	CategoryRepository categoryRepository;
	CareGiverRepository careGiverRepository;
	ModelMapper modelMapper;
	UploadProfileRepository uploadProfileRepository;

	@Autowired
	public CareGiverServiceImpl(CategoryRepository categoryRepository, CareGiverRepository careGiverRepository,
			ModelMapper modelMapper, UploadProfileRepository uploadProfileRepository) {
		super();
		this.categoryRepository = categoryRepository;
		this.careGiverRepository = careGiverRepository;
		this.modelMapper = modelMapper;
		this.uploadProfileRepository = uploadProfileRepository;
	}

	@Override
	public List<CareGiver> getCareGiver(Integer pageNo, Integer pageSize) {
	//	Pageable paging = PageRequest.of(pageNo, pageSize,Sort.by("careGiverName"));
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<CareGiver> pagedResult = careGiverRepository.findAll(paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareGiver>();
	}

	@Override
	public Optional<CareGiver> getCareGiverById(int careGiverId) {
		return careGiverRepository.findByCareGiverId(careGiverId);
	}

	@Override
	public CareGiver updateClientsCount(long careGiverId, int clientsCount) {
		Optional<CareGiver> careGiverUpdateDate = careGiverRepository.findById(careGiverId);
		if (careGiverUpdateDate.isPresent()) {
			careGiverUpdateDate.get().setCareGiverId(careGiverId);
			careGiverUpdateDate.get().setClientsCount(clientsCount);
			return careGiverRepository.save(careGiverUpdateDate.get());
		}
		return careGiverUpdateDate.get();
	}

	@Override
	public CareGiver updateClientsActiveStatus(long careGiverId, Status activeStatus) {
		Optional<CareGiver> careGiverUpdateDate = careGiverRepository.findById(careGiverId);
		if (careGiverUpdateDate.isPresent()) {
			careGiverUpdateDate.get().setCareGiverId(careGiverId);
			careGiverUpdateDate.get().setActiveStatus(activeStatus);
			return careGiverRepository.save(careGiverUpdateDate.get());
		}
		return careGiverUpdateDate.get();
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
	public CareGiver addCareGiver(FormDataDTO formDataDTO) {
		CareGiver careGiver = modelMapper.map(formDataDTO, CareGiver.class);
		String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
		UploadProfile uploadProfile = null;
		try {
			uploadProfile = UploadProfile.builder().fileName(fileName)
					.fileType(formDataDTO.getUploadPhoto().getContentType())
					.data(formDataDTO.getUploadPhoto().getBytes()).size(formDataDTO.getUploadPhoto().getSize()).build();
		} catch (IOException e) {
			log.error("Error Occured In Care Givers Service Update Care Giver With Id " + careGiver.getCareGiverId());
		}
		careGiver.setUploadPhoto(uploadProfile);
		return careGiverRepository.save(careGiver);
	}

	@Override
	public CareGiver updateCareGiver(FormDataDTO formDataDTO) {
		Optional<CareGiver> careGiverUpdateDate = careGiverRepository.findById(formDataDTO.getCareGiverId());
		int uploadProfileId = careGiverUpdateDate.get().getUploadPhoto().getId();
		if (careGiverUpdateDate.isPresent()) {
			CareGiver careGiver = modelMapper.map(formDataDTO, CareGiver.class);
			try {
				careGiver.getUploadPhoto().setFileName(formDataDTO.getUploadPhoto().getOriginalFilename());
				careGiver.getUploadPhoto().setFileType(formDataDTO.getUploadPhoto().getContentType());
				careGiver.getUploadPhoto().setData(formDataDTO.getUploadPhoto().getBytes());
				careGiver.getUploadPhoto().setSize(formDataDTO.getUploadPhoto().getSize());
			} catch (IOException e) {
				log.error(
						"Error Occured In Care Givers Service Update Care Giver With Id " + careGiver.getCareGiverId());
			}
			CareGiver savedCareGiver = careGiverRepository.save(careGiver);
			uploadProfileRepository.deleteById(uploadProfileId);
			return savedCareGiver;
		}
		return careGiverUpdateDate.get();
	}

}
