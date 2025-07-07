package com.PahanaEdu.service;

import com.PahanaEdu.dto.PublisherDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PublisherService {
    PublisherDTO createPublisher(PublisherDTO publisherDTO);
    List<PublisherDTO> getAllPublishers();
    PublisherDTO getPublisherByCode(String code);
    PublisherDTO getPublisherByName(String name);
    PublisherDTO updatePublisher(Long id, PublisherDTO publisherDTO);
    void deletePublisher(Long id);
}
