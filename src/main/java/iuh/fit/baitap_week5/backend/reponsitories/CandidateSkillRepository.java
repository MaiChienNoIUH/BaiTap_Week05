package iuh.fit.baitap_week5.backend.reponsitories;

import iuh.fit.baitap_week5.backend.models.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, Long> {
    List<CandidateSkill> findAllByCanId(Long canId);

    void deleteSkillByCanId(Long canId);

    // Phương thức tìm CandidateSkill theo CandidateSkillId
    CandidateSkill findByCanIdAndSkillId(Long canId, Long skillId);

}
