package iuh.fit.baitap_week5.frontend.controllers;

import iuh.fit.baitap_week5.backend.models.*;
import iuh.fit.baitap_week5.backend.services.Companyservice;
import iuh.fit.baitap_week5.backend.services.JobService;
import iuh.fit.baitap_week5.backend.services.JobSkillService;
import iuh.fit.baitap_week5.backend.services.SkillService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private Companyservice companyService;

    @Autowired
    private JobSkillService jobSkillServices;

    @GetMapping("/")
    public String showJobs(Model model) {
        // Lấy danh sách công việc từ service
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);

        List<Company> company = companyService.getAllCompanys();
        model.addAttribute("company", company);

        return "index";
    }

    //Xử lý hiển thị Skills của Job
    @GetMapping("/job/skill/{id}")
    public String showCandidateSkills(@PathVariable Long id, Model model, HttpServletRequest request) {
        Job job = jobService.findJobById(id);
        if (job != null) {
            List<Skill> skills = skillService.findAll();  // Get all available skills
            List<JobSkill> jobSkills = jobSkillServices.findAllByJobId(id); // Get skills of the candidate

            model.addAttribute("job", job);
            model.addAttribute("skills", skills);
            model.addAttribute("jobSkills", jobSkills);
        }

        // Truyền URL của trang trước vào model (Referer)
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);

        return "jobs/job-skills";
    }

    // Xử lý thêm kỹ năng cho một job
    @PostMapping("/job/skill/add/{id}")
    public String addCandidateSkill(@PathVariable Long id, @RequestParam Long skillId,
                                    @RequestParam Byte skillLevel, @RequestParam(required = false) String moreInfos) {
        Job job = jobService.findJobById(id);
        Skill skill = skillService.findById(skillId);

        if (job != null && skill != null) {
            JobSkill jobSkill = new JobSkill(job, skill, moreInfos, skillLevel);
            jobSkillServices.save(jobSkill);
        }

        return "redirect:/job/skill/" + id;
    }

    // Xử lý xóa kỹ năng của một job
    @PostMapping("/job/skill/delete/{jobId}/{skillId}")
    public String deleteCandidateSkill(@PathVariable Long jobId, @PathVariable Long skillId) {
        JobSkillId jobSkillId = new JobSkillId();
        jobSkillId.setJobId(jobId);
        jobSkillId.setSkillId(skillId);

        jobSkillServices.deleteSkillByJobId(jobSkillId);  // Gọi service để xóa kỹ năng
        return "redirect:/job/skill/" + jobId;
    }

    //Hiển thị trang tạo công việc (đăng bài tuyển dụng)
    @GetMapping("/job/post-job")
    public String showPostJobPage() {
        return "jobs/post-job";
    }

    //Câu 4: Xử lý đăng bài tuyển dụng
    @PostMapping("/job/create")
    public String createJob(@RequestParam("jobName") String jobName,
                            @RequestParam("jobDesc") String jobDesc,
                            @RequestParam("companyName") String companyName,
                            Model model) {

        // Tìm hoặc tạo công ty dựa trên tên
        Company company = companyService.findByName(companyName);

        // Tạo và lưu công việc mới
        Job job = new Job();
        job.setJobName(jobName);
        job.setJobDesc(jobDesc);
        job.setCompany(company);
        jobService.saveJob(job);

        List<Job> jobList = jobService.getAllJobs();
        model.addAttribute("jobs", jobList);
        model.addAttribute("message", "Job created successfully!");
        return "index";
    }

    // Câu 6: Tìm các ứng viên phù hợp cho một công việc
    @GetMapping("/job/{jobId}/rightCandidates")
    public String getCandidatesForJob(@PathVariable Long jobId, Model model) {
        Job job = jobService.findJobById(jobId);
        model.addAttribute("job", job);

        // Tìm các ứng viên phù hợp cho job với ID
        List<Candidate> suitableCandidates = jobService.findCandidatesForJob(jobId);

        model.addAttribute("UngVienPhuHop", suitableCandidates);

        return "jobs/recommend-Candidate-ForJob";
    }
}
