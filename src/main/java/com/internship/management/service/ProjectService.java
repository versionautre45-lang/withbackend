package com.internship.management.service;

import com.internship.management.dto.ProjectDTO;
import com.internship.management.entity.Encadreur;
import com.internship.management.entity.Intern;
import com.internship.management.entity.Project;
import com.internship.management.repository.EncadreurRepository;
import com.internship.management.repository.InternRepository;
import com.internship.management.repository.ProjectRepository;
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
    private final EncadreurRepository encadreurRepository;

    /**
     * Récupérer tous les projets
     */
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les projets par encadreur
     */
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByEncadreur(Long encadreurId) {
        Encadreur encadreur = encadreurRepository.findById(encadreurId)
                .orElseThrow(() -> new RuntimeException("Encadreur not found"));

        return projectRepository.findByEncadreur(encadreur).stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les projets d’un stagiaire
     */
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByStagiaire(Long stagiaireId) {
        Intern intern = internRepository.findById(stagiaireId)
                .orElseThrow(() -> new RuntimeException("Stagiaire not found"));

        return projectRepository.findByInternsContaining(intern.getId()).stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un projet par ID
     */
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return ProjectDTO.fromEntity(project);
    }

    /**
     * Créer un projet
     */
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setDepartment(projectDTO.getDepartment());
        project.setProgress(projectDTO.getProgress() != null ? projectDTO.getProgress() : 0);
        project.setStatus(projectDTO.getStatus() != null ?
                Project.ProjectStatus.valueOf(projectDTO.getStatus()) : Project.ProjectStatus.PLANNING);

        // Associer encadreur si fourni
        if (projectDTO.getEncadreurId() != null) {
            Encadreur encadreur = encadreurRepository.findById(projectDTO.getEncadreurId())
                    .orElseThrow(() -> new RuntimeException("Encadreur not found"));
            project.setEncadreur(encadreur);
        }

        // Associer stagiaires si fournis
        if (projectDTO.getAssignedInternIds() != null) {
            List<Intern> interns = internRepository.findAllById(projectDTO.getAssignedInternIds());
            project.setInterns(interns);
        }

        Project savedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(savedProject);
    }

    /**
     * Mettre à jour un projet
     */
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (projectDTO.getTitle() != null) project.setTitle(projectDTO.getTitle());
        if (projectDTO.getDescription() != null) project.setDescription(projectDTO.getDescription());
        if (projectDTO.getStartDate() != null) project.setStartDate(projectDTO.getStartDate());
        if (projectDTO.getEndDate() != null) project.setEndDate(projectDTO.getEndDate());
        if (projectDTO.getDepartment() != null) project.setDepartment(projectDTO.getDepartment());
        if (projectDTO.getProgress() != null) project.setProgress(projectDTO.getProgress());
        if (projectDTO.getStatus() != null)
            project.setStatus(Project.ProjectStatus.valueOf(projectDTO.getStatus()));

        // Mise à jour encadreur
        if (projectDTO.getEncadreurId() != null) {
            Encadreur encadreur = encadreurRepository.findById(projectDTO.getEncadreurId())
                    .orElseThrow(() -> new RuntimeException("Encadreur not found"));
            project.setEncadreur(encadreur);
        }

        // Mise à jour stagiaires
        if (projectDTO.getAssignedInternIds() != null) {
            List<Intern> interns = internRepository.findAllById(projectDTO.getAssignedInternIds());
            project.setInterns(interns);
        }

        Project updatedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(updatedProject);
    }

    /**
     * Supprimer un projet
     */
    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    /**
     * Assigner des stagiaires à un projet
     */
    @Transactional
    public ProjectDTO assignInterns(Long projectId, List<Long> internIds) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Intern> interns = internRepository.findAllById(internIds);
        if (interns.size() != internIds.size()) {
            throw new RuntimeException("One or more interns not found");
        }

        project.setInterns(interns);
        Project updatedProject = projectRepository.save(project);

        return ProjectDTO.fromEntity(updatedProject);
    }
}
