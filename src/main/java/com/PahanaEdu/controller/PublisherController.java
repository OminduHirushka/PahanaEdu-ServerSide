package com.PahanaEdu.controller;

import com.PahanaEdu.dto.PublisherDTO;
import com.PahanaEdu.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/publisher")
public class PublisherController {
    @Autowired
    private PublisherService publisherService;

    @PostMapping("/create-publisher")
    public ResponseEntity<Map<String, Object>> createPublisher(@Valid @RequestBody PublisherDTO publisherDTO) {
        PublisherDTO createdPublisher = publisherService.createPublisher(publisherDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Publisher created successfully");
        response.put("createdPublisher", createdPublisher);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllPublishers() {
        List<PublisherDTO> allPublishers = publisherService.getAllPublishers();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All publishers found");
        response.put("allPublishers", allPublishers);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Map<String, Object>> getPublisherByCode(@PathVariable String code) {
        PublisherDTO publisherDTO = publisherService.getPublisherByCode(code);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Publisher found");
        response.put("publisherDTO", publisherDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getPublisherByName(@PathVariable String name) {
        PublisherDTO publisherDTO = publisherService.getPublisherByName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Publisher found");
        response.put("publisherDTO", publisherDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-publisher/{id}")
    public ResponseEntity<Map<String, Object>> updatePublisher(@PathVariable Long id, @Valid @RequestBody PublisherDTO publisherDTO) {
        PublisherDTO updatedPublisher = publisherService.updatePublisher(id, publisherDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Publisher updated successfully");
        response.put("updatedPublisher", updatedPublisher);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-publisher/{id}")
    public ResponseEntity<Map<String, Object>> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Publisher deleted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
