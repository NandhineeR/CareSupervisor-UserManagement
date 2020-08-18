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
import com.qdm.cs.usermanagement.entity.CareProvider;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.repository.CareProviderRepository;
import com.qdm.cs.usermanagement.repository.CategoryRepository;
import com.qdm.cs.usermanagement.repository.UploadProfileRepository;
import com.qdm.cs.usermanagement.service.CareProviderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CareProviderServiceImpl implements CareProviderService {

	CareProviderRepository careProviderRepository;
	CategoryRepository categoryRepository;
	ModelMapper modelMapper;
	UploadProfileRepository uploadProfileRepository;

	@Autowired
	public CareProviderServiceImpl(CareProviderRepository careProviderRepository, CategoryRepository categoryRepository,
			ModelMapper modelMapper, UploadProfileRepository uploadProfileRepository) {
		super();
		this.careProviderRepository = careProviderRepository;
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
		this.uploadProfileRepository = uploadProfileRepository;
	}

	@Override
	public List<CareProvider> getCareProvider(Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<CareProvider> pagedResult = careProviderRepository.findAll(paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareProvider>();
	}

	@Override
	public Optional<CareProvider> getCareProviderById(long careProviderId) {
		return careProviderRepository.findById(careProviderId);
	}

	@Override
	public CareProvider updateClientsActiveStatus(long careProviderId, Status activeStatus) {
		Optional<CareProvider> careGiverUpdateData = careProviderRepository.findById(careProviderId);
		if (careGiverUpdateData.isPresent()) {
			careGiverUpdateData.get().setCareProviderId(careProviderId);
			careGiverUpdateData.get().setActiveStatus(activeStatus);
			return careProviderRepository.save(careGiverUpdateData.get());
		}
		return careGiverUpdateData.get();
	}

	@Override
	public CareProvider updateCareGiversCount(long careProviderId, int careGiversCount) {
		Optional<CareProvider> careGiverUpdateClientsCount = careProviderRepository.findById(careProviderId);
		if (careGiverUpdateClientsCount.isPresent()) {
			careGiverUpdateClientsCount.get().setCareProviderId(careProviderId);
			careGiverUpdateClientsCount.get().setCareGiversCount(careGiversCount);
			return careProviderRepository.save(careGiverUpdateClientsCount.get());
		}
		return careGiverUpdateClientsCount.get();
	}

	@Override
	public CareProvider updateProductsCount(long careProviderId, int productsCount) {
		Optional<CareProvider> ProductUpdateCount = careProviderRepository.findById(careProviderId);
		if (ProductUpdateCount.isPresent()) {
			ProductUpdateCount.get().setCareProviderId(careProviderId);
			ProductUpdateCount.get().setProductsCount(productsCount);
			return careProviderRepository.save(ProductUpdateCount.get());
		}
		return ProductUpdateCount.get();
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
	public CareProvider addCareProvider(FormDataDTO formDataDTO) throws IOException {
		CareProvider careProvider = modelMapper.map(formDataDTO, CareProvider.class);
		String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
		UploadProfile uploadProfile = UploadProfile.builder().fileName(fileName)
				.fileType(formDataDTO.getUploadPhoto().getContentType()).data(formDataDTO.getUploadPhoto().getBytes())
				.size(formDataDTO.getUploadPhoto().getSize()).build();
		careProvider.setUploadPhoto(uploadProfile);
		return careProviderRepository.save(careProvider);
	}

	@Override
	public CareProvider updateCareProvider(FormDataDTO formDataDTO) {
		Optional<CareProvider> careProviderUpdateDate = careProviderRepository
				.findById(formDataDTO.getCareProviderId());
		int uploadProfileId = careProviderUpdateDate.get().getUploadPhoto().getId();
		if (careProviderUpdateDate.isPresent()) {
			CareProvider careProvider = modelMapper.map(formDataDTO, CareProvider.class);
			try {
				careProvider.getUploadPhoto().setFileName(formDataDTO.getUploadPhoto().getOriginalFilename());
				careProvider.getUploadPhoto().setFileType(formDataDTO.getUploadPhoto().getContentType());
				careProvider.getUploadPhoto().setData(formDataDTO.getUploadPhoto().getBytes());
				careProvider.getUploadPhoto().setSize(formDataDTO.getUploadPhoto().getSize());
			} catch (IOException e) {
				log.error("Error Occured In Care Provider Service updateCareProvider With Id "
						+ careProvider.getCareProviderId());
			}
			CareProvider careProviderUpdated = careProviderRepository.save(careProvider);
			uploadProfileRepository.deleteById(uploadProfileId);
			return careProviderUpdated;
		}
		return careProviderUpdateDate.get();
	}
}
