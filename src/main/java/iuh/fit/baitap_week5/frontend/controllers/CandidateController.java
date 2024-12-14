package iuh.fit.baitap_week5.frontend.controllers;

import iuh.fit.baitap_week5.backend.models.*;
import iuh.fit.baitap_week5.backend.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/candidate")
public class CandidateController {
    @Autowired
    private CandidateService candidateServices;

    @Autowired
    private SkillService skillServices;

    @Autowired
    private JobService jobServices;

    @Autowired
    private JobSkillService jobSkillServices;

    @Autowired
    private AddressService addressServices;

    @Autowired
    private CandidateSkillService candidateSkillServices;

    // Danh sách không phân trang
    @GetMapping("/listNoPage")
    public String showCandidateList(Model model, HttpSession session, HttpServletRequest request) {
        // Lưu URL của trang danh sách vào session
        String currentUrl = request.getRequestURL().toString();
        session.setAttribute("previousPage", currentUrl);

        model.addAttribute("candidates", candidateServices.findAllCandidates());
        return "candidates/candidates";
    }


    //Danh sách có phân trang
    @GetMapping("/list")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size,
                                          HttpSession session, HttpServletRequest request) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Candidate> candidatePage = candidateServices.findAll(currentPage - 1, pageSize, "id", "asc");

        model.addAttribute("candidatePage", candidatePage);
        int totalPages = candidatePage.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Lưu URL của trang danh sách vào session
        String currentUrl = request.getRequestURL().toString();
        session.setAttribute("previousPage", currentUrl);

        return "candidates/candidates-paging";
    }

    // Xử lý hiện form add candidate
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("candidate", new Candidate());
        return "candidates/add-candidates";
    }

    @PostMapping("/add")
    public String addCandidate(@ModelAttribute Candidate candidate, @ModelAttribute Address address, HttpSession session) {
        addressServices.save(address);
        candidate.setAddress(address);

        candidateServices.save(candidate);

        // Lấy trang trước đó từ session
        String previousPage = (String) session.getAttribute("previousPage");

        // Chuyển hướng về trang truy cập trước đó (list hoặc listNoPage)
        if (previousPage != null) {
            return "redirect:" + previousPage;
        }
        return null;
    }


    // Xử lý hiển thị form edit candidate
    @GetMapping("/edit/{id}")
    public String showEditCandidateForm(@PathVariable("id") Long id, Model model) {
        Candidate candidate = candidateServices.findById(id); // Lấy candidate từ database
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate not found with id: " + id);
        }
        model.addAttribute("candidate", candidate); // Gửi dữ liệu sang view
        return "candidates/edit-candidates"; // Trả về view edit-candidates.html
    }

    @PostMapping("/edit")
    public String editCandidate(@ModelAttribute Candidate candidate, HttpSession session) {
        Address address = candidate.getAddress();

        if (address != null && address.getId() != null) {
            addressServices.save(address);
        }else{
            throw new IllegalArgumentException("Address ID is required.");
        }

        // Lưu ứng viên
        candidateServices.save(candidate);

        // Lấy trang trước đó từ session
        String previousPage = (String) session.getAttribute("previousPage");

        // Chuyển hướng về trang truy cập trước đó (list hoặc listNoPage)
        if (previousPage != null) {
            return "redirect:" + previousPage;
        }
        return null;
    }

    // Xử lý xóa ứng viên
    @PostMapping("/delete/{id}")
    public String deleteCandidate(@PathVariable("id") Long id) {
        candidateServices.deleteCandidate(id);
        return "redirect:/candidate/list"; // Quay lại danh sách sau khi xóa
    }

    //Xử lý hiển thị Skills của Candidate
    @GetMapping("/skill/{id}")
    public String showCandidateSkills(@PathVariable Long id, Model model, HttpServletRequest request) {
        Candidate candidate = candidateServices.findById(id);
        if (candidate != null) {
            List<Skill> skills = skillServices.findAll();  // Get all available skills
            List<CandidateSkill> candidateSkills = candidateSkillServices.findAllByCanId(id); // Get skills of the candidate

            model.addAttribute("candidate", candidate);
            model.addAttribute("skills", skills);
            model.addAttribute("candidateSkills", candidateSkills);
        }

        // Truyền URL của trang trước vào model (Referer)
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);

        return "candidates/candidate-skills";
    }

    // Xử lý thêm kỹ năng cho một candidate
    @PostMapping("/skill/add/{id}")
    public String addCandidateSkill(@PathVariable Long id, @RequestParam Long skillId,
                                    @RequestParam Byte skillLevel, @RequestParam(required = false) String moreInfos) {
        Candidate candidate = candidateServices.findById(id);
        Skill skill = skillServices.findById(skillId);

        if (candidate != null && skill != null) {
            CandidateSkill candidateSkill = new CandidateSkill(candidate, skill, moreInfos, skillLevel);
            candidateSkillServices.save(candidateSkill);
        }

        return "redirect:/candidate/skill/" + id;
    }

    // Xử lý xóa kỹ năng của một candidate
    @PostMapping("/skill/delete/{canId}/{skillId}")
    public String deleteCandidateSkill(@PathVariable Long canId, @PathVariable Long skillId) {
        CandidateSkillId candidateSkillId = new CandidateSkillId();
        candidateSkillId.setCanId(canId);
        candidateSkillId.setSkillId(skillId);

        candidateSkillServices.deleteSkillByCanId(candidateSkillId);  // Gọi service để xóa kỹ năng
        return "redirect:/candidate/skill/" + canId;
    }

    //Câu 5: Đề xuất một số công việc phù hợp với ứng viên
    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "candidates/login-to-recomendJob";
    }

    // Xử lý đăng nhập để tìm công việc phù hợp
    @PostMapping("/login-SuggestedJobs")
    public String loginCandidate(@RequestParam("email") String email, Model model) {
        Candidate candidate = candidateServices.findByEmail(email);
        if (candidate == null) {
            model.addAttribute("error", "Email không tồn tại. Vui lòng thử lại.");
            return "candidates/login-to-recomendJob";
        }

        // Lấy danh sách công việc gợi ý dựa trên kỹ năng của ứng viên
        List<Job> suggestedJobs = jobServices.findSuggestedJobsForCandidate(candidate);
        model.addAttribute("candidate", candidate);
        model.addAttribute("suggestedJobs", suggestedJobs);

        return "candidates/Recommend-jobs"; // Trang hiển thị danh sách công việc
    }


    //Câu 7: Đề xuất một số skill mà ứng viên chưa có để học.
    // Hiển thị form đăng nhập để tìm skill để học
    @GetMapping("/login_learnSkill")
    public String showLoginForm_Learn(Model model) {
        List<Job> jobs = jobServices.getAllJobs();
        model.addAttribute("jobs", jobs);
        return "login-to-learnSkill/login-to-learnSkill";
    }

    // Xử lý đăng nhập để tìm skill để học
    @PostMapping("/login_learnSkill")
    public String loginCandidate_learn(@RequestParam("email") String email, @RequestParam("jobId") Long jobId, Model model, HttpServletRequest request) {
        Candidate candidate = candidateServices.findByEmail(email);
        if (candidate == null) {
            model.addAttribute("error", "Email không tồn tại. Vui lòng thử lại.");
            return "login-to-learnSkill/login-to-learnSkill";
        }

        //Lấy danh sách tất cả các skill mà ứng viên chưa có
        List<Skill> SkillsNotCan = candidateServices.getSkills_No_Candidate(email);

        // Lấy danh sácch Skill của Job
        List<JobSkill> jobSkills = jobSkillServices.findAllByJobId(jobId);

        //Liệt kê (lấy) các skill của 1 công việc cụ thể mà ứng viên chưa có
        List<Skill> suggestedSkills = new ArrayList<>();

        for (JobSkill jobSkill : jobSkills) {
            for (Skill skill : SkillsNotCan) {
                // So sánh nếu skill từ SkillsNotCan trùng với jobSkill, giả sử so sánh bằng id hoặc tên
                if (jobSkill.getSkill().getId().equals(skill.getId())) {
                    suggestedSkills.add(skill);
                }
            }
        }
        model.addAttribute("suggestedSkills", suggestedSkills);
        model.addAttribute("candidate", candidate);

        // Truyền URL của trang trước vào model (Referer)
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        return "login-to-learnSkill/suggested-learn-skill";
    }


    //Mở rộng
    // Câu 8: Tìm các ứng viên có skill đặc biệt - đặc thù, mà công ty cần
    @GetMapping("/skill-special-search")
    public String showSkillSearchForm(Model model) {
        model.addAttribute("skills", skillServices.findAll());
        return "search-skill-Special/skill-special-search";
    }

    // Hiển thị danh sách ứng viên có skill đặc biệt
    @PostMapping("/recommendCandidate-for-skill-special")
    public String searchCandidateBySkill(@RequestParam("skillName") String skillName, Model model, HttpServletRequest request) {
        List<Candidate> candidates = candidateServices.findCandidatesBySkill(skillName);
        model.addAttribute("UngVienPhuHop", candidates);
        model.addAttribute("skillName", skillName);

        // Truyền URL của trang trước vào model (Referer)
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        return "search-skill-Special/recommendCandidate-for-skill-special";
    }
}
