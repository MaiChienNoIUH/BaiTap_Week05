package iuh.fit.baitap_week5.backend.reponsitories;

import iuh.fit.baitap_week5.backend.models.Candidate;
import iuh.fit.baitap_week5.backend.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository <Job, Long> {

    @Query("SELECT DISTINCT j FROM JobSkill js JOIN js.job j WHERE js.skill.id IN :skillIds")
    List<Job> findJobsBySkillIds(@Param("skillIds") List<Long> skillIds);

    Job findJobById(Long jobId);
}
