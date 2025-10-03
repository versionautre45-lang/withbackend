package com.internship.management.controller;

import com.internship.management.dto.CreateInternRequest;
import com.internship.management.dto.InternDTO;
import com.internship.management.entity.Intern;
import com.internship.management.service.InternService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InternController {

    private final InternService internService;

    @PostMapping
    public ResponseEntity<InternDTO> createIntern(@RequestBody CreateInternRequest request) {
        try {
            InternDTO intern = internService.createIntern(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(intern);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<InternDTO>> getAllInterns(
            @RequestParam(required = false) Long encadreurId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status
    ) {
        if (encadreurId != null) {
            return ResponseEntity.ok(internService.getInternsByEncadreur(encadreurId));
        }
        if (department != null) {
            return ResponseEntity.ok(internService.getInternsByDepartment(department));
        }
        if (status != null) {
            Intern.InternshipStatus internStatus = Intern.InternshipStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(internService.getInternsByStatus(internStatus));
        }
        return ResponseEntity.ok(internService.getAllInterns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternDTO> getInternById(@PathVariable Long id) {
        try {
            InternDTO intern = internService.getInternById(id);
            return ResponseEntity.ok(intern);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternDTO> updateIntern(
            @PathVariable Long id,
            @RequestBody CreateInternRequest request
    ) {
        try {
            InternDTO intern = internService.updateIntern(id, request);
            return ResponseEntity.ok(intern);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InternDTO> updateInternStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        try {
            String statusStr = request.get("status");
            Intern.InternshipStatus status = Intern.InternshipStatus.valueOf(statusStr.toUpperCase());
            InternDTO intern = internService.updateInternStatus(id, status);
            return ResponseEntity.ok(intern);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntern(@PathVariable Long id) {
        try {
            internService.deleteIntern(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
