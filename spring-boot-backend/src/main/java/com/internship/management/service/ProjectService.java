package com.internship.management.service;

import com.internship.management.dto.ProjectDTO;
import com.internship.management.entity.Intern;
import com.internship.management.entity.Project;
import com.internship.management.entity.User;
import com.internship.management.repository.InternRepository;
import com.internship.management.repository.ProjectRepository;
import com.internship.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final InternRepository internRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByEncadreur(Long encadreurId) {
        List<Intern> encadreurInterns = internRepository.findByEncadreurId(encadreurId);
        List<Long> internIds = encadreurInterns.stream()
                .map(Intern::getId)
                .collect(Collectors.toList());

        return projectRepository.findAll().stream()
                .filter(project -> project.getAssignedInterns().stream()
                        .anyMatch(internIds::contains))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByStagiaire(Long stagiaireId) {
        Intern intern = internRepository.findById(stagiaireId)
                .orElseThrow(() -> new RuntimeException("Stagiaire not found"));

        return projectRepository.findByAssignedInternsContaining(intern.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return convertToDTO(project);
    }

    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setCompletion(projectDTO.getCompletion() != null ? projectDTO.getCompletion() : 0);
        project.setStartDate(projectDTO.getStartDate());
        project.setDueDate(projectDTO.getDueDate());

        if (projectDTO.getAssignedInternIds() != null) {
            project.setAssignedInterns(projectDTO.getAssignedInternIds());
        }

        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }

    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (projectDTO.getTitle() != null) {
            project.setTitle(projectDTO.getTitle());
        }
        if (projectDTO.getDescription() != null) {
            project.setDescription(projectDTO.getDescription());
        }
        if (projectDTO.getStatus() != null) {
            project.setStatus(projectDTO.getStatus());
        }
        if (projectDTO.getCompletion() != null) {
            project.setCompletion(projectDTO.getCompletion());
        }
        if (projectDTO.getStartDate() != null) {
            project.setStartDate(projectDTO.getStartDate());
        }
        if (projectDTO.getDueDate() != null) {
            project.setDueDate(projectDTO.getDueDate());
        }
        if (projectDTO.getAssignedInternIds() != null) {
            project.setAssignedInterns(projectDTO.getAssignedInternIds());
        }

        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectDTO assignInterns(Long projectId, List<Long> internIds) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        for (Long internId : internIds) {
            if (!internRepository.existsById(internId)) {
                throw new RuntimeException("Intern not found: " + internId);
            }
        }

        project.setAssignedInterns(internIds);
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    private ProjectDTO convertToDTO(Project project) {
        List<Long> internIds = project.getAssignedInterns();

        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus())
                .completion(project.getCompletion())
                .startDate(project.getStartDate())
                .dueDate(project.getDueDate())
                .assignedInternIds(internIds)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
