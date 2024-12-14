package iuh.fit.baitap_week5.backend.services;

import iuh.fit.baitap_week5.backend.models.JobSkill;
import iuh.fit.baitap_week5.backend.models.JobSkillId;
import iuh.fit.baitap_week5.backend.reponsitories.JobSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSkillService {
    @Autowired
    private  JobSkillRepository jobSkillRepository;

    public JobSkill save(JobSkill jobSkills) {
        return jobSkillRepository.save(jobSkills);
    }

    public List<JobSkill> findAllByJobId(Long jobId) {
        return jobSkillRepository.findAllByJobId(jobId);
    }

    //Phương thức xóa một kỹ năng của một job
    public void deleteSkillByJobId(JobSkillId jobSkillId) {
        JobSkill jobSkill = jobSkillRepository.findByJobIdAndSkillId(
                jobSkillId.getJobId(), jobSkillId.getSkillId());

        if (jobSkill != null) {
            jobSkillRepository.delete(jobSkill);  // Xóa kỹ năng của job
        } else {
            throw new IllegalArgumentException("JobSkill not found for the provided ids.");
        }
    }
}
