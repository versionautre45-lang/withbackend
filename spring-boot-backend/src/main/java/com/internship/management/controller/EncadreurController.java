package com.internship.management.controller;

import com.internship.management.dto.InternDTO;
import com.internship.management.dto.UserDTO;
import com.internship.management.service.EncadreurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encadreurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EncadreurController {

    private final EncadreurService encadreurService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllEncadreurs() {
        return ResponseEntity.ok(encadreurService.getAllEncadreurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getEncadreurById(@PathVariable Long id) {
        try {
            UserDTO encadreur = encadreurService.getEncadreurById(id);
            return ResponseEntity.ok(encadreur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/interns")
    public ResponseEntity<List<InternDTO>> getEncadreurInterns(@PathVariable Long id) {
        try {
            List<InternDTO> interns = encadreurService.getEncadreurInterns(id);
            return ResponseEntity.ok(interns);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/intern-count")
    public ResponseEntity<Long> getEncadreurInternCount(@PathVariable Long id) {
        try {
            Long count = encadreurService.getEncadreurInternCount(id);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UserDTO> createEncadreur(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdEncadreur = encadreurService.createEncadreur(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEncadreur);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateEncadreur(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO
    ) {
        try {
            UserDTO updatedEncadreur = encadreurService.updateEncadreur(id, userDTO);
            return ResponseEntity.ok(updatedEncadreur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncadreur(@PathVariable Long id) {
        try {
            encadreurService.deleteEncadreur(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
