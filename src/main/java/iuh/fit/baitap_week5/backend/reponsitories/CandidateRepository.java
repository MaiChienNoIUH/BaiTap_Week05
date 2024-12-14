package iuh.fit.baitap_week5.backend.reponsitories;

import iuh.fit.baitap_week5.backend.models.Candidate;
import iuh.fit.baitap_week5.backend.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Candidate findByEmail(String email);

    //Lấy danh sách tất cả các skills mà ứng viên chưa có
    @Query("SELECT s FROM Skill s LEFT JOIN CandidateSkill cs ON s = cs.skill AND cs.can.email = :email WHERE cs.skill IS NULL")
    List<Skill> getSkills_No_Candidate(@Param("email") String email);

    @Query("SELECT c FROM Candidate c JOIN c.candidateSkills cs WHERE cs.skill.skillName LIKE %:skillName%")
    List<Candidate> findCandidatesBySkill(String skillName);

    Candidate findCandidateById(Long id);

}