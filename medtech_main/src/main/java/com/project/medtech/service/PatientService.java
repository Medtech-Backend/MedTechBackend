package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.CheckListInfoDtoMapper;
import com.project.medtech.model.*;
import com.project.medtech.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PregnancyRepository pregnancyRepository;
    private final CheckListRepository checkListRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final EmailSenderService emailSenderService;
    private final AddressRepository addressRepository;
    private final InsuranceRepository insuranceRepository;


    public PatientDto getInfo(EmailDto emailDto) {
        User user = userRepository.findByEmail(emailDto.getEmail());
        if(user == null) {
            throw new ResourceNotFoundException("No User with email: " + emailDto.getEmail());
        }

        Patient patient = patientRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with user_id: " + user.getUserId()));

        PatientDto patientDto = new PatientDto();
        patientDto.setPatientId(patient.getId());
        patientDto.setEmail(emailDto.getEmail());
        patientDto.setBirthday(patient.getBirthday());
        patientDto.setAddress(patient.getAddress().getPatientAddress());
        patientDto.setFullName(user.getLastName() + " " + user.getFirstName() + " " + user.getMiddleName());
        patientDto.setPhoneNumber(user.getPhoneNumber());
        patientDto.setWeekOfPregnancy(getCurrentWeekOfPregnancy(new RequestPatient(patient.getId())));

        return patientDto;
    }

    public Integer getCurrentWeekOfPregnancy(RequestPatient request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with ID : " + request.getPatientId()));
        Pregnancy pregnancy = pregnancyRepository.findById(patient.getCurrentPregnancyId())
                .orElseThrow(() -> new ResourceNotFoundException("No Pregnancy with ID : " + request.getPatientId()));
        if(pregnancy.getRegistrationDate() == null || pregnancy.getFirstVisitWeekOfPregnancy() == null) {
            return 0;
        }
        LocalDate registrationDate = pregnancy.getRegistrationDate();
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        long diffInDays = ChronoUnit.DAYS.between(registrationDate, currentDate);
        long diffInWeeks = diffInDays / 7;
        return pregnancy.getFirstVisitWeekOfPregnancy() + (int) diffInWeeks;
    }

    public List<CheckListInfoDto> getAllPatientsCheckLists(RequestPatient reqPat) {
        Patient patient = patientRepository.findById(reqPat.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with ID : " + reqPat.getPatientId()));
        List<CheckList> list = checkListRepository.findAllByPatient(patient);
        List<CheckListInfoDto> listDto = new ArrayList<>();

        for (CheckList checkList : list) {
            listDto.add(CheckListInfoDtoMapper.EntityToDto(checkList));
        }

        return listDto;
    }

    public PhoneNumberDto changePhoneNumber(PhoneNumberDto phoneNumberDto) {
        User user = getAuthentication();
        user.setPhoneNumber(phoneNumberDto.getPhoneNumber());

        userRepository.save(user);

        return phoneNumberDto;
    }

    public AddressDto changeAddress(AddressDto addressDto) {
        User user = getAuthentication();

        Patient patient = patientRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with user_id: " + user.getUserId()));

        Address address = addressRepository.findByPatientId(patient.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No Address with patient_id: " + user.getUserId()));

        address.setPatientAddress(addressDto.getAddress());

        addressRepository.save(address);

        return addressDto;
    }

    public EmailDto changeEmail(EmailDto emailDto) {
        User user = getAuthentication();

        user.setEmail(emailDto.getEmail());

        userRepository.save(user);

        return emailDto;
    }

    public RegisterPatientDto registerPatient(RegisterPatientDto registerPatientDto) {
        User user = new User();
        user.setFirstName(registerPatientDto.getFirstName());
        user.setLastName(registerPatientDto.getLastName());
        user.setMiddleName(registerPatientDto.getMiddleName());
        user.setEmail(registerPatientDto.getEmail());
        user.setPhoneNumber(registerPatientDto.getPhoneNumber());
        user.setOtpUsed(false);
        user.setRole(Role.PATIENT);
        user.setStatus(Status.ACTIVE);
        String password = emailSenderService.send(registerPatientDto.getEmail(), "otp");
        user.setPassword(passwordEncoder().encode(password));

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setBirthday(registerPatientDto.getBirthday());
        patient.setAge(registerPatientDto.getAge());
        patient.setPin(registerPatientDto.getPin());
        patient.setCitizenship(registerPatientDto.getCitizenship());
        patient.setPatientCategory(registerPatientDto.getPatientCategory());
        patient.setWorkPlace(registerPatientDto.getWorkPlace());
        patient.setPosition(registerPatientDto.getPosition());
        patient.setWorkConditions(registerPatientDto.getWorkConditions());
        patient.setWorksNow(registerPatientDto.getWorksNow());
        patient.setHusbandFirstName(registerPatientDto.getHusbandFirstName());
        patient.setHusbandLastName(registerPatientDto.getHusbandLastName());
        patient.setHusbandMiddleName(registerPatientDto.getHusbandMiddleName());
        patient.setHusbandWorkPlace(registerPatientDto.getHusbandWorkPlace());
        patient.setHusbandPosition(registerPatientDto.getHusbandPosition());
        patient.setHusbandPhoneNumber(registerPatientDto.getHusbandPhoneNumber());
        patient.setMarried(registerPatientDto.getMarried());
        patient.setEducation(registerPatientDto.getEducation());
        patient.setUser(user);

        Address address = new Address();
        address.setPatientAddress(registerPatientDto.getPatientAddress());
        address.setPhoneNumber(registerPatientDto.getPatientHomePhoneNumber());
        address.setRelativeAddress(registerPatientDto.getRelativeAddress());
        address.setRelativePhoneNumber(registerPatientDto.getRelativePhoneNumber());
        address.setPatient(patient);

        Insurance insurance = new Insurance();
        insurance.setTerritoryName(registerPatientDto.getInsuranceTerritoryName());
        insurance.setNumber(registerPatientDto.getInsuranceNumber());
        insurance.setPatient(patient);

        User userDoctor = userRepository.findByEmail(registerPatientDto.getDoctorEmail());
        Doctor doctor = doctorRepository.findDoctorByUser(userDoctor.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor was not found with user_id: " + userDoctor.getUserId()));

        Pregnancy pregnancy = new Pregnancy();
        pregnancy.setDoctor(doctor);
        pregnancy.setBloodType(registerPatientDto.getBloodType());
        pregnancy.setRhFactorPregnant(registerPatientDto.getRhFactorPregnant());
        pregnancy.setRhFactorPartner(registerPatientDto.getRhFactorPartner());
        pregnancy.setTiterRhFactorInTwentyEightMonth(registerPatientDto.getTiterRhFactorInTwentyEightMonth());
        pregnancy.setBloodRw(registerPatientDto.getBloodRw());
        pregnancy.setBloodHiv(registerPatientDto.getBloodHiv());
        pregnancy.setBloodHivPartner(registerPatientDto.getBloodHivPartner());
        pregnancy.setRegistrationDate(registerPatientDto.getRegistrationDate());
        pregnancy.setFromAnotherMedOrganizationReason(registerPatientDto.getFromAnotherMedOrganizationReason());
        pregnancy.setNameOfAnotherMedOrganization(registerPatientDto.getNameOfAnotherMedOrganization());
        pregnancy.setPregnancyNumber(registerPatientDto.getPregnancyNumber());
        pregnancy.setChildbirthNumber(registerPatientDto.getChildbirthNumber());
        pregnancy.setGestationalAgeByLastMenstruation(registerPatientDto.getGestationalAgeByLastMenstruation());
        pregnancy.setGestationalAgeByUltrasound(registerPatientDto.getGestationalAgeByUltrasound());
        pregnancy.setEstimatedDateOfBirth(registerPatientDto.getEstimatedDateOfBirth());
        pregnancy.setLateRegistrationReason(registerPatientDto.getLateRegistrationReason());
        pregnancy.setFirstVisitWeekOfPregnancy(registerPatientDto.getFirstVisitWeekOfPregnancy());
        pregnancy.setFirstVisitComplaints(registerPatientDto.getFirstVisitComplaints());
        pregnancy.setFirstVisitGrowth(registerPatientDto.getFirstVisitGrowth());
        pregnancy.setFirstVisitWeight(registerPatientDto.getFirstVisitWeight());
        pregnancy.setSkinAndMucousMembranes(registerPatientDto.getSkinAndMucousMembranes());
        pregnancy.setThyroid(registerPatientDto.getThyroid());
        pregnancy.setMilkGlands(registerPatientDto.getMilkGlands());
        pregnancy.setPeripheralLymphNodes(registerPatientDto.getPeripheralLymphNodes());
        pregnancy.setRespiratorySystem(registerPatientDto.getRespiratorySystem());
        pregnancy.setCardiovascularSystem(registerPatientDto.getCardiovascularSystem());
        pregnancy.setArterialPressure(registerPatientDto.getArterialPressure());
        pregnancy.setDigestiveSystem(registerPatientDto.getDigestiveSystem());
        pregnancy.setUrinarySystem(registerPatientDto.getUrinarySystem());
        pregnancy.setEdema(registerPatientDto.getEdema());
        pregnancy.setBonePelvis(registerPatientDto.getBonePelvis());
        pregnancy.setUterineFundusHeight(registerPatientDto.getUterineFundusHeight());
        pregnancy.setFetalHeartbeat(registerPatientDto.getFetalHeartbeat());
        pregnancy.setExternalGenitalia(registerPatientDto.getExternalGenitalia());
        pregnancy.setExaminationOfCervixInMirrors(registerPatientDto.getExaminationOfCervixInMirrors());
        pregnancy.setBimanualStudy(registerPatientDto.getBimanualStudy());
        pregnancy.setVaginalDischarge(registerPatientDto.getVaginalDischarge());
        pregnancy.setProvisionalDiagnosis(registerPatientDto.getProvisionalDiagnosis());
        pregnancy.setVacationFromForPregnancy(registerPatientDto.getVacationFromForPregnancy());
        pregnancy.setVacationUntilForPregnancy(registerPatientDto.getVacationUntilForPregnancy());
        pregnancy.setAllergicToDrugs(registerPatientDto.getAllergicToDrugs());
        pregnancy.setPastIllnessesAndSurgeries(registerPatientDto.getPastIllnessesAndSurgeries());

        pregnancyRepository.save(pregnancy);

        patient.setCurrentPregnancyId(pregnancy.getId());

        patient.getPregnancy().add(pregnancy);

        userRepository.save(user);

        patientRepository.save(patient);

        addressRepository.save(address);

        insuranceRepository.save(insurance);

        return registerPatientDto;
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public User getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName());
    }

    public List<PatientDataDto> getAllPatients() {
        List<User> users = userRepository.findAll(Role.PATIENT);
        List<PatientDataDto> listDto = new ArrayList<>();

        for(User u : users ){
            PatientDataDto dto = new PatientDataDto();
            Patient patient = u.getPatient();
            Address address = patient.getAddress();
            dto.setPatientId(patient.getId());
            dto.setFIO(u.getLastName()+" "+u.getFirstName().substring(0, 1)+"."+u.getMiddleName().substring(0, 1)+".");
            dto.setPhoneNumber(u.getPhoneNumber());
            dto.setEmail(u.getEmail());
            dto.setCurrentWeekOfPregnancy(getCurrentWeekOfPregnancy(new RequestPatient(u.getUserId())));
            dto.setResidenceAddress(address.getRelativeAddress());
            dto.setStatus(u.getStatus().toString());
            listDto.add(dto);
        }
        return listDto;
    }

}
