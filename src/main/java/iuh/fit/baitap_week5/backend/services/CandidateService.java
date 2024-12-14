package iuh.fit.baitap_week5.backend.services;

import iuh.fit.baitap_week5.backend.models.Candidate;
import iuh.fit.baitap_week5.backend.models.Skill;
import iuh.fit.baitap_week5.backend.reponsitories.AddressRepository;
import iuh.fit.baitap_week5.backend.reponsitories.CandidateRepository;
import iuh.fit.baitap_week5.backend.reponsitories.CandidateSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateSkillRepository candidateSkillRepository;

    public Candidate save(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    //findall không phân trang
    public List<Candidate> findAllCandidates(){
        return candidateRepository.findAll();
    }

    //findall có phân trang
    public Page<Candidate> findAll(int pageNo,  int Pagesize, String sortBy, String sortDir){
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, Pagesize, sort);
        return candidateRepository.findAll(pageable);
    }

    public Candidate findByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }

    public Candidate findById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + id));
    }

    // Phương thức xóa ứng viên, xóa cả Address và Skill
    @Transactional
    public void deleteCandidate(Long id) {
        // Tìm ứng viên theo id
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + id));

        // Xóa tất cả các CandidateSkill của ứng viên
        candidateSkillRepository.deleteSkillByCanId(id);

        // Xóa ứng viên
        candidateRepository.delete(candidate);
    }

    public List<Skill> getSkills_No_Candidate(String email) {
        return candidateRepository.getSkills_No_Candidate(email);
    }

    public List<Candidate> findCandidatesBySkill(String skillName) {
        return candidateRepository.findCandidatesBySkill(skillName);
    }
}
