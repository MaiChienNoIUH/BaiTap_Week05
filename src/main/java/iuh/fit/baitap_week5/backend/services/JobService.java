package iuh.fit.baitap_week5.backend.services;

import iuh.fit.baitap_week5.backend.models.Candidate;
import iuh.fit.baitap_week5.backend.models.CandidateSkill;
import iuh.fit.baitap_week5.backend.models.Job;
import iuh.fit.baitap_week5.backend.models.JobSkill;
import iuh.fit.baitap_week5.backend.reponsitories.CandidateRepository;
import iuh.fit.baitap_week5.backend.reponsitories.CandidateSkillRepository;
import iuh.fit.baitap_week5.backend.reponsitories.JobRepository;
import iuh.fit.baitap_week5.backend.reponsitories.JobSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private CandidateRepository candidatelRepository;

    @Autowired
    private CandidateSkillRepository candidateSkillRepository;

    // Lấy danh sách công việc từ cơ sở dữ liệu
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    // Tìm công việc theo id
    public Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
    }

    // Câu 5: Tìm công việc gợi ý cho ứng viên
    public List<Job> findSuggestedJobsForCandidate(Candidate candidate) {
        // Lấy tất cả kỹ năng yêu cầu cho ứng viên này
        List<CandidateSkill> canSkills = candidateSkillRepository.findAllByCanId(candidate.getId());

        // Tạo danh sách công việc phù hợp
        List<Job> suitableJobs = new ArrayList<>();

        // Duyệt qua tất cả công việc
        List<Job> allJobs = jobRepository.findAll(); // Lấy tất cả job

        // Duyệt qua từng công việc
        for (Job job : allJobs) {
            boolean hasAllSkills = true; // Giả sử ứng viên có tất cả kỹ năng của công việc

            // Kiểm tra từng kỹ năng yêu cầu của công việc
            for (JobSkill jobSkill : job.getJobSkills()) {
                boolean hasRequiredSkill = false;

                // Kiểm tra nếu ứng viên có kỹ năng yêu cầu
                for (CandidateSkill canSkill : canSkills) {
                    if (canSkill.getSkill().getId().equals(jobSkill.getSkill().getId())) {
                        hasRequiredSkill = true;
                        break; // Dừng khi tìm thấy kỹ năng phù hợp
                    }
                }

                // Nếu ứng viên thiếu kỹ năng yêu cầu, set hasAllSkills = false và thoát khỏi vòng lặp
                if (!hasRequiredSkill) {
                    hasAllSkills = false;
                    break; // Dừng kiểm tra cho công việc này
                }
            }

            // Nếu ứng viên có đủ tất cả kỹ năng yêu cầu của công việc, thêm công việc vào danh sách
            if (hasAllSkills) {
                suitableJobs.add(job);
            }
        }

        // Trả về danh sách công việc phù hợp
        return suitableJobs;
    }

    // Câu 6: Tìm danh sách ứng viên phù hợp với công việc
    public List<Candidate> findCandidatesForJob(Long jobId) {

        // Lấy tất cả kỹ năng yêu cầu cho công việc này
        List<JobSkill> jobSkills = jobSkillRepository.findAllByJobId(jobId);

        // Tạo danh sách ứng viên phù hợp
        List<Candidate> suitableCandidates = new ArrayList<>();

        // Duyệt qua tất cả ứng viên
        List<Candidate> allCandidates = candidatelRepository.findAll(); // Lấy tất cả ứng viên

        // Duyệt qua từng ứng viên
        for (Candidate candidate : allCandidates) {
            // Duyệt qua tất cả kỹ năng yêu cầu của công việc
            boolean hasAllSkills = true;

            for (JobSkill jobSkill : jobSkills) {
                Long requiredSkillId = jobSkill.getSkill().getId();  // Kỹ năng yêu cầu từ job

                // Kiểm tra xem ứng viên có kỹ năng yêu cầu này không
                boolean hasRequiredSkill = false;
                for (CandidateSkill candidateSkill : candidate.getCandidateSkills()) {
                    if (candidateSkill.getSkill().getId().equals(requiredSkillId)) {
                        hasRequiredSkill = true;
                        break; // Nếu có, thoát ra khỏi vòng lặp kiểm tra kỹ năng
                    }
                }

                // Nếu ứng viên thiếu bất kỳ kỹ năng yêu cầu nào, đánh dấu là không phù hợp
                if (!hasRequiredSkill) {
                    hasAllSkills = false;
                    break; // Dừng kiểm tra cho ứng viên này
                }
            }

            // Nếu ứng viên có đủ tất cả kỹ năng yêu cầu (không thiếu kỹ năng nào), thêm vào danh sách
            if (hasAllSkills) {
                suitableCandidates.add(candidate);
            }
        }

        // Trả về danh sách ứng viên phù hợp
        return suitableCandidates;
    }
}
