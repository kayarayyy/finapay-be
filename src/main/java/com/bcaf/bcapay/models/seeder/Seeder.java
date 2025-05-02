package com.bcaf.bcapay.models.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bcaf.bcapay.models.Branch;
import com.bcaf.bcapay.models.CustomerDetails;
import com.bcaf.bcapay.models.EmployeeDetails;
import com.bcaf.bcapay.models.Feature;
import com.bcaf.bcapay.models.LoanRequest;
import com.bcaf.bcapay.models.Plafond;
import com.bcaf.bcapay.models.Role;
import com.bcaf.bcapay.models.RoleFeature;
import com.bcaf.bcapay.models.User;
import com.bcaf.bcapay.models.enums.City;
import com.bcaf.bcapay.models.enums.Plan;
import com.bcaf.bcapay.repositories.BranchRepository;
import com.bcaf.bcapay.repositories.CustomerDetailsRepository;
import com.bcaf.bcapay.repositories.EmployeeDetailsRepoitory;
import com.bcaf.bcapay.repositories.FeatureRepository;
import com.bcaf.bcapay.repositories.LoanRequestRepository;
import com.bcaf.bcapay.repositories.PlafondRepository;
import com.bcaf.bcapay.repositories.RoleFeatureRepository;
import com.bcaf.bcapay.repositories.RoleRepository;
import com.bcaf.bcapay.repositories.UserRepository;
import com.bcaf.bcapay.services.BranchService;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class Seeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private RoleFeatureRepository roleFeatureRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Autowired
    private PlafondRepository plafondRepository;

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private EmployeeDetailsRepoitory employeeDetailsRepoitory;

    @Autowired
    private BranchService branchService;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedFeatures();
        seedRoleFeatures();
        seedUsers();
        seedBranches();
        seedPlafond();
        seedEmployeeDetails();
        seedCustomerDetails();
        seedLoanRequests();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "SUPERADMIN", null));
            roleRepository.save(new Role(null, "CUSTOMER", null));
            roleRepository.save(new Role(null, "MARKETING", null));
            roleRepository.save(new Role(null, "BRANCH_MANAGER", null));
            roleRepository.save(new Role(null, "BACK_OFFICE", null));
        }
    }

    private void seedFeatures() {
        if (featureRepository.count() == 0) {
            featureRepository.save(new Feature(null, "MANAGE_USERS", null));
            featureRepository.save(new Feature(null, "MANAGE_ROLES", null));
            featureRepository.save(new Feature(null, "MANAGE_ROLE_FEATURES", null));
            featureRepository.save(new Feature(null, "MANAGE_FEATURES", null));
            featureRepository.save(new Feature(null, "MANAGE_PROFILE", null));
            featureRepository.save(new Feature(null, "MANAGE_LOAN_REQUESTS", null));
            featureRepository.save(new Feature(null, "MANAGE_BRANCHES", null));

            // ACCESS LOAN REQUEST
            featureRepository.save(new Feature(null, "CREATE_LOAN_REQUEST", null));

            // ACCESS LOAN REQUEST REVIEW
            featureRepository.save(new Feature(null, "GET_ALL_LOAN_REQUEST_REVIEW", null));
            featureRepository.save(new Feature(null, "GET_LOAN_REQUEST_BY_ID_REVIEW", null));
            featureRepository.save(new Feature(null, "UPDATE_LOAN_REQUEST_REVIEW", null));
            // ACCESS LOAN REQUEST APPROVAL
            featureRepository.save(new Feature(null, "GET_ALL_LOAN_REQUEST_APPROVAL", null));
            featureRepository.save(new Feature(null, "GET_LOAN_REQUEST_BY_ID_APPROVAL", null));
            featureRepository.save(new Feature(null, "UPDATE_LOAN_REQUEST_APPROVAL", null));
            // ACCESS LOAN REQUEST DISBURSEMENT
            featureRepository.save(new Feature(null, "GET_LOAN_REQUEST_DISBURSEMENT", null));
            featureRepository.save(new Feature(null, "GET_LOAN_REQUEST_BY_ID_DISBURSEMENT", null));
            featureRepository.save(new Feature(null, "GET_ALL_LOAN_REQUEST_DISBURSEMENT_ONGOING", null));
            featureRepository.save(new Feature(null, "UPDATE_LOAN_REQUEST_DISBURSEMENT", null));
        }
    }

    private void seedRoleFeatures() {
        if (roleFeatureRepository.count() == 0) {

            Role superAdmin = roleRepository.findByName("SUPERADMIN").orElse(null);
            Feature manageUsers = featureRepository.findByName("MANAGE_USERS").orElse(null);
            Feature manageRoles = featureRepository.findByName("MANAGE_ROLES").orElse(null);
            Feature manageFeatures = featureRepository.findByName("MANAGE_FEATURES").orElse(null);
            Feature manageRoleFeatures = featureRepository.findByName("MANAGE_ROLE_FEATURES").orElse(null);
            Feature manageLoanRequests = featureRepository.findByName("MANAGE_LOAN_REQUESTS").orElse(null);
            Feature manageBranches = featureRepository.findByName("MANAGE_BRANCHES").orElse(null);
            
            Role customer = roleRepository.findByName("CUSTOMER").orElse(null);
            Feature createLoanRequest = featureRepository.findByName("CREATE_LOAN_REQUEST").orElse(null);

            Role marketing = roleRepository.findByName("MARKETING").orElse(null);
            Feature getAllLoanRequestReview = featureRepository.findByName("GET_ALL_LOAN_REQUEST_REVIEW")
                    .orElse(null);
            Feature getByIdLoanRequestReview = featureRepository.findByName("GET_LOAN_REQUEST_BY_ID_REVIEW")
                    .orElse(null);
            Feature updateLoanRequestReview = featureRepository.findByName("UPDATE_LOAN_REQUEST_REVIEW")
                    .orElse(null);

            Role branchManager = roleRepository.findByName("BRANCH_MANAGER").orElse(null);
            Feature getAllLoanRequestApproval = featureRepository.findByName("GET_ALL_LOAN_REQUEST_APPROVAL")
                    .orElse(null);
            Feature getByIdLoanRequestApproval = featureRepository.findByName("GET_LOAN_REQUEST_BY_ID_APPROVAL")
                    .orElse(null);
            Feature updateLoanRequestApproval = featureRepository.findByName("UPDATE_LOAN_REQUEST_APPROVAL")
                    .orElse(null);

            Role backOffice = roleRepository.findByName("BACK_OFFICE").orElse(null);
            Feature getLoanRequestDisbursement = featureRepository.findByName("GET_LOAN_REQUEST_DISBURSEMENT")
                    .orElse(null);
            Feature getLoanRequestByIdDisbursement = featureRepository.findByName("GET_LOAN_REQUEST_BY_ID_DISBURSEMENT")
                    .orElse(null);
            Feature getAllLoanRequestDisbursement = featureRepository.findByName("GET_ALL_LOAN_REQUEST_DISBURSEMENT_ONGOING")
                    .orElse(null);
            Feature updateLoanRequestDisbursement = featureRepository.findByName("UPDATE_LOAN_REQUEST_DISBURSEMENT")
                    .orElse(null);

            if (superAdmin != null) {
                if (manageRoles != null) {
                    roleFeatureRepository.save(new RoleFeature(null, superAdmin, manageRoles));
                }
                if (manageUsers != null) {
                    roleFeatureRepository.save(new RoleFeature(null, superAdmin, manageUsers));
                }
                if (manageFeatures != null) {
                    roleFeatureRepository.save(new RoleFeature(null, superAdmin, manageFeatures));
                }
                if (manageRoleFeatures != null) {
                    roleFeatureRepository.save(new RoleFeature(null, superAdmin, manageRoleFeatures));
                }
                if (manageLoanRequests != null) {
                    roleFeatureRepository.save(new RoleFeature(null, superAdmin, manageLoanRequests));
                }
                if (manageBranches != null) {
                    roleFeatureRepository.save(new RoleFeature(null, superAdmin, manageBranches));
                }
            }
            if (customer != null) {
                if (createLoanRequest != null) {
                    roleFeatureRepository.save(new RoleFeature(null, customer, createLoanRequest));
                }
            }

            if (marketing != null) {
                if (getAllLoanRequestReview != null) {
                    roleFeatureRepository.save(new RoleFeature(null, marketing, getAllLoanRequestReview));
                }
                if (getByIdLoanRequestReview != null) {
                    roleFeatureRepository.save(new RoleFeature(null, marketing, getByIdLoanRequestReview));
                }
                if (updateLoanRequestReview != null) {
                    roleFeatureRepository.save(new RoleFeature(null, marketing, updateLoanRequestReview));
                }
            }

            if (branchManager != null) {
                if (getAllLoanRequestApproval != null) {
                    roleFeatureRepository.save(new RoleFeature(null, branchManager, getAllLoanRequestApproval));
                }
                if (getByIdLoanRequestApproval != null) {
                    roleFeatureRepository.save(new RoleFeature(null, branchManager, getByIdLoanRequestApproval));
                }
                if (updateLoanRequestApproval != null) {
                    roleFeatureRepository.save(new RoleFeature(null, branchManager, updateLoanRequestApproval));
                }
            }

            if (backOffice != null) {
                if (getLoanRequestDisbursement != null) {
                    roleFeatureRepository.save(new RoleFeature(null, backOffice, getLoanRequestDisbursement));
                }
                if (getLoanRequestByIdDisbursement != null) {
                    roleFeatureRepository.save(new RoleFeature(null, backOffice, getLoanRequestByIdDisbursement));
                }
                if (getAllLoanRequestDisbursement != null) {
                    roleFeatureRepository.save(new RoleFeature(null, backOffice, getAllLoanRequestDisbursement));
                }
                if (updateLoanRequestDisbursement != null) {
                    roleFeatureRepository.save(new RoleFeature(null, backOffice, updateLoanRequestDisbursement));
                }
            }
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            createUser("Superadmin", "superadmin@gmail.com", "superadmin123", "SUPERADMIN", "20242753", null);
            createUser("Marketing", "rayrizkyfawzy@gmail.com", "marketing123", "MARKETING", "2025111", "REF2025111");
            createUser("Marketing 1", "marketing1@gmail.com", "marketing123", "MARKETING", "2025112", "REF2025112");
            createUser("Marketing 2", "marketing2@gmail.com", "marketing123", "MARKETING", "2025113", "REF2025113");
            createUser("Customer", "customer@gmail.com", "customer123", "CUSTOMER", null, null);
            createUser("Customer 1", "customer1@gmail.com", "customer123", "CUSTOMER", null, null);
            createUser("Branch Manager", "branchmanager@gmail.com", "branchmanager123", "BRANCH_MANAGER", "2025121",
                    null);
            createUser("Branch Manager 1", "branchmanager1@gmail.com", "branchmanager123", "BRANCH_MANAGER", "2025122",
                    null);
            createUser("Back Office", "backoffice@gmail.com", "backoffice123", "BACK_OFFICE", "2025131", null);
        }
    }

    private void createUser(String name, String email, String password, String roleName, String nip, String refferal) {
        User user = new User();
        user.setName(name);
        user.setActive(true);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(roleRepository.findByName(roleName).orElse(null));
        user.setNip(nip);
        user.setRefferal(refferal);
        userRepository.save(user);
    }

    private void seedBranches() {
        if (branchRepository.count() == 0) {
            List<Branch> branches = Arrays.asList(
                    new Branch(null, "Jakarta 1", City.JAKARTA, -6.2088, 106.8456, null, new ArrayList<>()),
                    new Branch(null, "Bandung 1", City.BANDUNG, -6.9175, 107.6191, null, new ArrayList<>()),
                    new Branch(null, "Surabaya 1", City.SURABAYA, -7.2575, 112.7521, null, new ArrayList<>()),
                    new Branch(null, "Medan 1", City.MEDAN, 3.5952, 98.6722, null, new ArrayList<>()),
                    new Branch(null, "Denpasar 1", City.DENPASAR, -8.6705, 115.2126, null, new ArrayList<>()));

            branchRepository.saveAll(branches);
        }

        // Tambahkan Branch Manager & Marketing untuk Jakarta
        Branch jakartaBranch = branchRepository.findByName("Jakarta 1").orElse(null);
        User managerJakarta = userRepository.findByEmail("branchmanager@gmail.com").orElse(null);
        User marketingJakarta = userRepository.findByEmail("rayrizkyfawzy@gmail.com").orElse(null);

        if (jakartaBranch != null) {
            if (managerJakarta != null) {
                jakartaBranch.setBranchManager(managerJakarta);
            }

            if (marketingJakarta != null) {
                marketingJakarta.setBranch(jakartaBranch); // Set branch untuk user marketing
                jakartaBranch.getMarketing().add(marketingJakarta);
            }

            branchRepository.save(jakartaBranch); // Simpan perubahan
            if (marketingJakarta != null) {
                userRepository.save(marketingJakarta); // Simpan perubahan pada user
            }
        }

        Branch bandungBranch = branchRepository.findByName("Bandung 1").orElse(null);
        User managerBandung = userRepository.findByEmail("branchmanager1@gmail.com").orElse(null);
        User marketingBandung = userRepository.findByEmail("marketing1@gmail.com").orElse(null);
        User marketingBandung1 = userRepository.findByEmail("marketing2@gmail.com").orElse(null);

        if (bandungBranch != null) {
            if (managerBandung != null) {
                bandungBranch.setBranchManager(managerBandung);
            }

            if (marketingBandung != null) {
                marketingBandung.setBranch(bandungBranch); // Set branch untuk user marketing
                bandungBranch.getMarketing().add(marketingBandung);
            }
            if (marketingBandung1 != null) {
                marketingBandung1.setBranch(bandungBranch); // Set branch untuk user marketing
                bandungBranch.getMarketing().add(marketingBandung1);
            }

            branchRepository.save(bandungBranch); // Simpan perubahan
            if (marketingBandung != null) {
                userRepository.save(marketingBandung); // Simpan perubahan pada user
            }
            if (marketingBandung1 != null) {
                userRepository.save(marketingBandung1); // Simpan perubahan pada user
            }
        }
    }

    private void seedLoanRequests() {
        if (loanRequestRepository.count() == 0) {
            // Ambil customer dengan email customer@gmail.com
            Optional<CustomerDetails> customerDetails = customerDetailsRepository.findByUserEmail("customer@gmail.com");
            User customer = customerDetails.get().getUser();
            if (customer != null) {
                // Membuat LoanRequest dengan customer yang ditemukan
                LoanRequest loanRequest = new LoanRequest();
                loanRequest.setAmount(1000000.00); // 1 juta
                loanRequest.setCustomer(customer);
                loanRequest.setLatitude(-6.2870583);
                loanRequest.setLongitude(106.7820784);
                Branch branch = branchService.findNearestBranch(loanRequest.getLatitude(), loanRequest.getLongitude());
                loanRequest.setBranch(branch);
                loanRequest.setBranchManager(branch.getBranchManager());
                loanRequest.setMarketing(branch.getMarketing().getFirst());
                // Simpan LoanRequest ke dalam database
                loanRequestRepository.save(loanRequest);
            }
        }
    }

    private void seedPlafond() {
        if (plafondRepository.count() == 0) {
            plafondRepository.save(new Plafond(null, 1000000.00, Plan.BRONZE, 0.05));
            plafondRepository.save(new Plafond(null, 5000000.00, Plan.SILVER, 0.047));
            plafondRepository.save(new Plafond(null, 10000000.00, Plan.GOLD, 0.04));
            plafondRepository.save(new Plafond(null, 25000000.00, Plan.PLATINUM, 0.035));
        }
    }

    private void seedCustomerDetails() {
        if (userRepository.existsByEmail("customer@gmail.com") &&
                plafondRepository.existsByPlan(Plan.BRONZE)) {

            User customer = userRepository.findByEmail("customer@gmail.com").orElse(null);
            Plafond bronzePlafond = plafondRepository.findByPlan(Plan.BRONZE).orElse(null);

            if (customer != null && bronzePlafond != null) {
                // Cek apakah data customer detail sudah pernah dibuat
                boolean exists = customerDetailsRepository.findByUserEmail(customer.getEmail()).isPresent();
                if (!exists) {
                    CustomerDetails details = new CustomerDetails();
                    details.setUser(customer);
                    details.setPlafondPlan(bronzePlafond);
                    details.setAvailablePlafond(bronzePlafond.getAmount());
                    details.setStreet("Jl. Raya Sukamahi");
                    details.setDistrict("Bekasi");
                    details.setProvince("Jawa Barat");
                    details.setPostalCode("17530");
                    customerDetailsRepository.save(details);
                }
            }
        }
        if (userRepository.existsByEmail("customer1@gmail.com") &&
                plafondRepository.existsByPlan(Plan.BRONZE)) {

            User customer = userRepository.findByEmail("customer1@gmail.com").orElse(null);
            Plafond bronzePlafond = plafondRepository.findByPlan(Plan.BRONZE).orElse(null);

            if (customer != null && bronzePlafond != null) {
                // Cek apakah data customer detail sudah pernah dibuat
                boolean exists = customerDetailsRepository.findByUserEmail(customer.getEmail()).isPresent();
                if (!exists) {
                    CustomerDetails details = new CustomerDetails();
                    details.setUser(customer);
                    details.setPlafondPlan(bronzePlafond);
                    details.setAvailablePlafond(bronzePlafond.getAmount());

                    customerDetailsRepository.save(details);
                }
            }
        }
    }

    private void seedEmployeeDetails() {
        if (userRepository.existsByEmail("rayrizkyfawzy@gmail.com")) {

            User marketing = userRepository.findByEmail("rayrizkyfawzy@gmail.com").orElse(null);

            if (marketing != null) {
                // Cek apakah data marketing detail sudah pernah dibuat
                boolean exists = employeeDetailsRepoitory.findByUserEmail(marketing.getEmail()).isPresent();
                if (!exists) {
                    EmployeeDetails details = new EmployeeDetails();
                    details.setStreet("Jl. Kebon Jeruk Raya");
                    details.setDistrict("Kebon Jeruk");
                    details.setProvince("DKI Jakarta");
                    details.setPostalCode("11530");
                    details.setUser(marketing);

                    employeeDetailsRepoitory.save(details);
                }
            }
        }
    }

}
