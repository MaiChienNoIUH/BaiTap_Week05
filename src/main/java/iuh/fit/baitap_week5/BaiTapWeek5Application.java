package iuh.fit.baitap_week5;

import com.neovisionaries.i18n.CountryCode;
import iuh.fit.baitap_week5.backend.models.*;
import iuh.fit.baitap_week5.backend.reponsitories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.*;

@SpringBootApplication
public class BaiTapWeek5Application {

	public static void main(String[] args) {
		SpringApplication.run(BaiTapWeek5Application.class, args);
	}


	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private SkillRepository skillRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobSkillRepository jobSkillRepository;

	@Autowired
	private CandidateSkillRepository candidateSkillRepository;


	@Bean
	CommandLineRunner initData(){
		return args -> {
			Random rnd =new Random();

			// 1. Tạo Address cho Company
			Address companyAddress = new Address();
			companyAddress.setStreet("Nguyen Van Bao");
			companyAddress.setCity("Phuong 4 - Go Vap");
			companyAddress.setCountry(CountryCode.VN);
			companyAddress.setNumber("123");
			companyAddress.setZipcode("700000");
			addressRepository.save(companyAddress);

			// 2. Tạo Company
			Company company = new Company();
			company.setCompName("FSOFT");
			company.setPhone("0344387030");
			company.setEmail("nomai6789@gmail.com");
			company.setWebUrl("https://localhost:8080");
			company.setAddress(companyAddress);
			companyRepository.save(company);

			// 3. Tạo Skills với đầy đủ thuộc tính
			Map<String, Skill> skillMap = new HashMap<>();
			List<Skill> skills = List.of(
					new Skill("Develop applications using backend and frontend Java technologies.", "Backend-Frontend Java", (byte) 1),
					new Skill("Proficiency in English communication for technical and non-technical purposes.", "Know English", (byte) 2),
					new Skill("Work on distributed systems and applications.", "Distributed Programming", (byte) 1),
					new Skill("Proficient in object-oriented programming principles and practices.", "OOP Programming", (byte) 1),
					new Skill("Ability to solve complex technical problems.", "Problem-solving", (byte) 2),
					new Skill("Optimize code for better performance and scalability.", "Code Optimization", (byte) 1),
					new Skill("Create and manage project plans effectively.", "Planning Skills", (byte) 3),
					new Skill("Identify and mitigate potential risks in projects.", "Risk Assessment", (byte) 3),
					new Skill("Efficiently allocate resources to ensure project success.", "Resource Allocation", (byte) 3),
					new Skill("Lead and manage project teams effectively.", "Team Leadership", (byte) 3),
					new Skill("Ensure the quality of software through rigorous testing.", "Software Testing", (byte) 1),
					new Skill("Identify and document software bugs.", "Bug Detection", (byte) 2),
					new Skill("Ensure adherence to quality standards.", "Quality Assurance", (byte) 1),
					new Skill("Apply critical thinking in testing scenarios.", "Critical Thinking", (byte) 2),
					new Skill("Apply creativity to design challenges.", "Creative Thinking", (byte) 2),
					new Skill("Develop architectural solutions for software systems.", "System Architecture", (byte) 3),
					new Skill("Design intuitive and user-friendly interfaces.", "UI/UX Design", (byte) 1),
					new Skill("Create prototypes to visualize and test ideas.", "Prototyping Skills", (byte) 1),
					new Skill("Collect and analyze project requirements.", "Requirement Gathering", (byte) 3),
					new Skill("Analyze data to inform decisions.", "Data Analysis", (byte) 2),
					new Skill("Communicate effectively with stakeholders.", "Stakeholder Communication", (byte) 2),
					new Skill("Analyze business needs and requirements.", "Business Analysis", (byte) 3)
			);


			for (Skill skill : skills) {
				skillRepository.save(skill);
				skillMap.put(skill.getSkillName(), skill);
			}

			// 4. Tạo Jobs và liên kết với Skills
			List<Job> jobs = new ArrayList<>();
			Map<String, List<String>> jobSkills = Map.of(
					"Programming", List.of("Backend-Frontend Java", "Distributed Programming", "OOP Programming", "Know English", "Problem-solving", "Code Optimization"),
					"Project Management", List.of("Planning Skills", "Risk Assessment", "Resource Allocation", "Team Leadership", "Know English"),
					"Testing", List.of("Software Testing", "Bug Detection", "Quality Assurance", "Critical Thinking"),
					"Design", List.of("Creative Thinking", "System Architecture", "UI/UX Design", "Prototyping Skills", "Know English"),
					"Requirements Analysis", List.of("Requirement Gathering", "Data Analysis", "Stakeholder Communication", "Business Analysis")
			);

			for (String jobName : jobSkills.keySet()) {
				Job job = new Job();
				job.setJobName(jobName);
				job.setJobDesc("Description for " + jobName);
				job.setCompany(company);
				jobRepository.save(job);

				for (String skillName : jobSkills.get(jobName)) {
					Skill skill = skillMap.get(skillName);

					if (skill != null) {
						// Khởi tạo JobSkill với JobSkillId
						JobSkill jobSkill = new JobSkill(job, skill, "Required skill for " + jobName, (byte) rnd.nextInt(1, 5));
						jobSkillRepository.save(jobSkill);
					} else {
						throw new IllegalArgumentException("Skill not found: " + skillName);
					}
				}
				jobs.add(job);
			}


			for (int i = 1; i < 1000; i++) {
				Address add = new Address("Street #" + i,
						"City #" + i,
						CountryCode.VN,
						rnd.nextInt(1, 1000) + "",
						rnd.nextInt(1000000, 9999999) + "");
//				addressRepository.save(add);

				Candidate can = new Candidate("Name #" + i,
						LocalDate.of(1998, rnd.nextInt(1, 13), rnd.nextInt(1, 29)),
						add,
						rnd.nextLong(1111111111L, 9999999999L) + "",
						"email_" + i + "@gmail.com");
				candidateRepository.save(can);
//				System.out.println("Added: " + can);

				// Liên kết ứng viên với một số kỹ năng ngẫu nhiên
				// Gán kỹ năng ngẫu nhiên cho ứng viên
				List<Skill> randomSkills = new ArrayList<>(skillMap.values()).subList(0, rnd.nextInt(3, 17));
				for (Skill skill : randomSkills) {
					CandidateSkill candidateSkill = new CandidateSkill(
							can,
							skill,
							"Required skill for candidate - " + can.getFullName(),
							(byte) rnd.nextInt(1, 5) // Random từ 1-4
					);
					candidateSkillRepository.save(candidateSkill);
				}
			}
		};
	}

}
