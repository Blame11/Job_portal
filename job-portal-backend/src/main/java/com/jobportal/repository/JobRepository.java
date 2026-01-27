package com.jobportal.repository;

import com.jobportal.model.Job;
import com.jobportal.model.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findByCreatedBy(String createdBy);
    
    long countByJobStatus(JobStatus jobStatus);
    
    @Query("{ $or: [ { 'company': { $regex: ?0, $options: 'i' } }, { 'position': { $regex: ?0, $options: 'i' } }, { 'jobLocation': { $regex: ?0, $options: 'i' } } ] }")
    Page<Job> searchJobs(String search, Pageable pageable);
    
    @Query("{ $or: [ { 'company': { $regex: ?0, $options: 'i' } }, { 'position': { $regex: ?0, $options: 'i' } }, { 'jobLocation': { $regex: ?0, $options: 'i' } } ] }")
    long countSearchResults(String search);
}
