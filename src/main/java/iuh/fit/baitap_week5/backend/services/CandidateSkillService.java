package iuh.fit.baitap_week5.backend.services;

import iuh.fit.baitap_week5.backend.models.CandidateSkill;
import iuh.fit.baitap_week5.backend.models.CandidateSkillId;
import iuh.fit.baitap_week5.backend.reponsitories.CandidateSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateSkillService {
    @Autowired
    private CandidateSkillRepository candidateSkillRepository;

    public CandidateSkill save(CandidateSkill candidateSkill) {
        return candidateSkillRepository.save(candidateSkill);
    }

    public List<CandidateSkill> findAllByCanId(Long canId) {
        return candidateSkillRepository.findAllByCanId(canId);
    }

    // Phương thức xóa 1 kỹ năng của một candidate
    public void deleteSkillByCanId(CandidateSkillId candidateSkillId) {
        CandidateSkill candidateSkill = candidateSkillRepository.findByCanIdAndSkillId(
                candidateSkillId.getCanId(), candidateSkillId.getSkillId());

        if (candidateSkill != null) {
            candidateSkillRepository.delete(candidateSkill);  // Xóa kỹ năng của candidate
        } else {
            throw new IllegalArgumentException("CandidateSkill not found for the provided ids.");
        }
    }
}
