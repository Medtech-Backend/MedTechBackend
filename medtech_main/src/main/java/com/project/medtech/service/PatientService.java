package com.project.medtech.service;

import com.project.medtech.dto.CheckListInfoDto;
import com.project.medtech.dto.RegisterPatientDto;
import com.project.medtech.dto.RequestPatient;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.CheckListInfoDtoMapper;
import com.project.medtech.model.*;
import com.project.medtech.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    public Integer getCurrentWeekOfPregnancy(RequestPatient request) {

        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("No Patient with ID : " + request.getPatientId()));
        Pregnancy pregnancy = patient.getPregnancy();
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
        Patient patient = patientRepository.findById(reqPat.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("No Patient with ID : " + reqPat.getPatientId()));
        List<CheckList> list = checkListRepository.findAllByPatient(patient);
        List<CheckListInfoDto> listDto = new ArrayList<>();

        for (CheckList checkList : list) {
            listDto.add(CheckListInfoDtoMapper.EntityToDto(checkList));
        }

        return listDto;
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
        patient.setPhoneNumber(registerPatientDto.getPhoneNumber());
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

        patient.setPregnancy(pregnancy);

        userRepository.save(user);

        patientRepository.save(patient);

        addressRepository.save(address);

        insuranceRepository.save(insurance);

        return registerPatientDto;
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
