package com.internship.management.service;

import com.internship.management.dto.CreateInternRequest;
import com.internship.management.dto.InternDTO;
import com.internship.management.entity.Encadreur;
import com.internship.management.entity.Intern;
import com.internship.management.entity.User;
import com.internship.management.repository.EncadreurRepository;
import com.internship.management.repository.InternRepository;
import com.internship.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternService {

    private final InternRepository internRepository;
    private final UserRepository userRepository;
    private final EncadreurRepository encadreurRepository;

    @Transactional
    public InternDTO createIntern(CreateInternRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(User.Role.STAGIAIRE)
                .accountStatus(User.AccountStatus.PENDING)
                .build();
        user = userRepository.save(user);

        Encadreur encadreur = null;
        if (request.getEncadreurId() != null) {
            encadreur = encadreurRepository.findById(request.getEncadreurId())
                    .orElseThrow(() -> new RuntimeException("Encadreur non trouvé"));
        }

        Intern intern = Intern.builder()
                .user(user)
                .encadreur(encadreur)
                .school(request.getSchool())
                .department(request.getDepartment())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(Intern.InternshipStatus.PENDING)
                .build();

        intern = internRepository.save(intern);
        return InternDTO.fromEntity(intern);
    }

    @Transactional(readOnly = true)
    public List<InternDTO> getAllInterns() {
        return internRepository.findAll().stream()
                .map(InternDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InternDTO getInternById(Long id) {
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        return InternDTO.fromEntity(intern);
    }

    @Transactional
    public void deleteIntern(Long id) {
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        userRepository.delete(intern.getUser());
        internRepository.delete(intern);
    }
}
