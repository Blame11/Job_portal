package com.jobportal.repository;

import com.jobportal.model.Application;
import com.jobportal.model.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    Optional<Application> findByApplicantIdAndJobId(String applicantId, String jobId);
    
    List<Application> findByApplicantId(String applicantId);
    
    Page<Application> findByRecruiterId(String recruiterId, Pageable pageable);
    
    List<Application> findByJobId(String jobId);
    
    List<Application> findByJobIdAndStatus(String jobId, ApplicationStatus status);
}
