package iuh.fit.baitap_week5.backend.services;

import iuh.fit.baitap_week5.backend.models.Company;
import iuh.fit.baitap_week5.backend.reponsitories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Companyservice {
    @Autowired
    private CompanyRepository companyRepository;

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public Company findByName(String compName) {
        return companyRepository.findByCompName (compName)
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setCompName(compName);
                    return companyRepository.save(newCompany);
                });
    }

    public List<Company> getAllCompanys() {
        return companyRepository.findAll();
    }
}
