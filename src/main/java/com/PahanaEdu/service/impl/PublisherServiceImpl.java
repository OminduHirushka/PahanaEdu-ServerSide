package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.PublisherDTO;
import com.PahanaEdu.exception.DuplicateResourceException;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.Publisher;
import com.PahanaEdu.repository.PublisherRepository;
import com.PahanaEdu.service.PublisherService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PublisherDTO createPublisher(PublisherDTO publisherDTO) {
        if (publisherRepository.findByCode(publisherDTO.getCode()).isPresent()) {
            throw new DuplicateResourceException("Publisher already exists");
        }

        Publisher publisher = new Publisher();
        publisher.setCode(publisherDTO.getCode());
        publisher.setName(publisherDTO.getName());

        Publisher savedPublisher = publisherRepository.save(publisher);
        return modelMapper.map(savedPublisher, PublisherDTO.class);
    }

    @Override
    public List<PublisherDTO> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(x -> modelMapper.map(x, PublisherDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PublisherDTO getPublisherByCode(String code) {
        Publisher publisher = publisherRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        return modelMapper.map(publisher, PublisherDTO.class);
    }

    @Override
    public PublisherDTO getPublisherByName(String name) {
        Publisher publisher = publisherRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        return modelMapper.map(publisher, PublisherDTO.class);
    }

    @Override
    public PublisherDTO updatePublisher(Long id, PublisherDTO publisherDTO) {
        Publisher existingPublisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        existingPublisher.setName(publisherDTO.getName());

        Publisher updatedPublisher = publisherRepository.save(existingPublisher);
        return modelMapper.map(updatedPublisher, PublisherDTO.class);
    }

    @Override
    public void deletePublisher(Long id) {
        Publisher existingPublisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        publisherRepository.delete(existingPublisher);
    }
}
