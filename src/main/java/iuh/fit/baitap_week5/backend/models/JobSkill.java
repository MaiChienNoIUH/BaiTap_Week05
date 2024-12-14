package iuh.fit.baitap_week5.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "job_skill")
public class JobSkill {
    @EmbeddedId
    private JobSkillId id;

    @MapsId("jobId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @MapsId("skillId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "more_infos", length = 1000)
    private String moreInfos;

    @Column(name = "skill_level", nullable = false)
    private Byte skillLevel;

    public JobSkill() {
    }

//    public JobSkill(Job job, Skill skill, String moreInfos, Byte skillLevel) {
//        this.job = job;
//        this.skill = skill;
//        this.moreInfos = moreInfos;
//        this.skillLevel = skillLevel;
//    }

    public JobSkill(Job job, Skill skill, String moreInfos, Byte skillLevel) {
        this.id = new JobSkillId(); // Khởi tạo JobSkillId
        this.id.setJobId(job.getId()); // Gắn jobId
        this.id.setSkillId(skill.getId()); // Gắn skillId
        this.job = job;
        this.skill = skill;
        this.moreInfos = moreInfos;
        this.skillLevel = skillLevel;
    }

}