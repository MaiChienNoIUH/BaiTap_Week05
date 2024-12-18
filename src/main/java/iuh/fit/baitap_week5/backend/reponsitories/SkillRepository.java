package iuh.fit.baitap_week5.backend.reponsitories;


import iuh.fit.baitap_week5.backend.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAll();

    Skill findById(long id);


}
