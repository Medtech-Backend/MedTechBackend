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
import java.util.HashMap;
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
    private final AppointmentRepository appointmentRepository;

    public PatientDto getInfo(EmailDto emailDto) {
        User user = userRepository.findByEmail(emailDto.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("No User with email: " + emailDto.getEmail());
        }

        Patient patient = patientRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with user_id: " + user.getUserId()));

        PatientDto patientDto = new PatientDto();
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
        if (pregnancy.getRegistrationDate() == null || pregnancy.getFirstVisitWeekOfPregnancy() == null) {
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

    public MedCardDto registerPatient(MedCardDto registerPatientDto) {
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

        User userDoctor = userRepository.findByEmail(registerPatientDto.getDoctor());
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

        patientRepository.save(patient);

        userRepository.save(user);

        addressRepository.save(address);

        insuranceRepository.save(insurance);

        return registerPatientDto;
    }

    public MedCardDto getPatientMedCardInfo(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("User was not found with email: " + email);
        }

        Patient patient = patientRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient was not found with user_id: " + user.getUserId())
                );

        Pregnancy pregnancy = pregnancyRepository.findById(patient.getCurrentPregnancyId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Pregnancy was not found with id: " + patient.getCurrentPregnancyId())
                );

        Doctor doctor = pregnancy.getDoctor();

        MedCardDto medCardDto = new MedCardDto();

        medCardDto.setEmail(user.getEmail());
        medCardDto.setFirstName(user.getFirstName());
        medCardDto.setLastName(user.getLastName());
        medCardDto.setMiddleName(user.getMiddleName());
        medCardDto.setPhoneNumber(user.getPhoneNumber());

        medCardDto.setDoctor(doctor.getUser().getLastName() + doctor.getUser().getFirstName() + doctor.getUser().getMiddleName());

        medCardDto.setBirthday(patient.getBirthday());
        medCardDto.setAge(patient.getAge());
        medCardDto.setPin(patient.getPin());
        medCardDto.setCitizenship(patient.getCitizenship());
        medCardDto.setPatientCategory(patient.getPatientCategory());
        medCardDto.setWorkPlace(patient.getWorkPlace());
        medCardDto.setPosition(patient.getPosition());
        medCardDto.setWorkConditions(patient.getWorkConditions());
        medCardDto.setWorksNow(patient.getWorksNow());
        medCardDto.setHusbandFirstName(patient.getHusbandFirstName());
        medCardDto.setHusbandLastName(patient.getHusbandLastName());
        medCardDto.setHusbandMiddleName(patient.getHusbandMiddleName());
        medCardDto.setHusbandWorkPlace(patient.getHusbandWorkPlace());
        medCardDto.setHusbandPosition(patient.getHusbandPosition());
        medCardDto.setHusbandPhoneNumber(patient.getHusbandPhoneNumber());

        medCardDto.setMarried(patient.getMarried());
        medCardDto.setEducation(patient.getEducation());

        medCardDto.setPatientAddress(patient.getAddress().getPatientAddress());
        medCardDto.setPatientHomePhoneNumber(patient.getAddress().getPhoneNumber());
        medCardDto.setRelativeAddress(patient.getAddress().getRelativeAddress());
        medCardDto.setRelativePhoneNumber(patient.getAddress().getRelativePhoneNumber());

        medCardDto.setInsuranceTerritoryName(patient.getInsurance().getTerritoryName());
        medCardDto.setInsuranceNumber(patient.getInsurance().getNumber());

        medCardDto.setBloodType(pregnancy.getBloodType());
        medCardDto.setRhFactorPregnant(pregnancy.getRhFactorPregnant());
        medCardDto.setRhFactorPartner(pregnancy.getRhFactorPartner());
        medCardDto.setTiterRhFactorInTwentyEightMonth(pregnancy.getTiterRhFactorInTwentyEightMonth());
        medCardDto.setBloodRw(pregnancy.getBloodRw());
        medCardDto.setBloodHiv(pregnancy.getBloodHiv());
        medCardDto.setBloodHivPartner(pregnancy.getBloodHivPartner());
        medCardDto.setRegistrationDate(pregnancy.getRegistrationDate());
        medCardDto.setFirstVisitWeekOfPregnancy(pregnancy.getFirstVisitWeekOfPregnancy());
        medCardDto.setFromAnotherMedOrganizationReason(pregnancy.getFromAnotherMedOrganizationReason());
        medCardDto.setNameOfAnotherMedOrganization(pregnancy.getNameOfAnotherMedOrganization());
        medCardDto.setPregnancyNumber(pregnancy.getPregnancyNumber());
        medCardDto.setChildbirthNumber(pregnancy.getChildbirthNumber());
        medCardDto.setGestationalAgeByLastMenstruation(pregnancy.getGestationalAgeByLastMenstruation());
        medCardDto.setGestationalAgeByUltrasound(pregnancy.getGestationalAgeByUltrasound());
        medCardDto.setEstimatedDateOfBirth(pregnancy.getEstimatedDateOfBirth());
        medCardDto.setLateRegistrationReason(pregnancy.getLateRegistrationReason());
        medCardDto.setFirstVisitComplaints(pregnancy.getFirstVisitComplaints());
        medCardDto.setFirstVisitGrowth(pregnancy.getFirstVisitGrowth());
        medCardDto.setFirstVisitWeight(pregnancy.getFirstVisitWeight());
        medCardDto.setBodyMassIndex(pregnancy.getBodyMassIndex());
        medCardDto.setSkinAndMucousMembranes(pregnancy.getSkinAndMucousMembranes());
        medCardDto.setThyroid(pregnancy.getThyroid());
        medCardDto.setMilkGlands(pregnancy.getMilkGlands());
        medCardDto.setPeripheralLymphNodes(pregnancy.getPeripheralLymphNodes());
        medCardDto.setRespiratorySystem(pregnancy.getRespiratorySystem());
        medCardDto.setCardiovascularSystem(pregnancy.getCardiovascularSystem());
        medCardDto.setArterialPressure(pregnancy.getArterialPressure());
        medCardDto.setDigestiveSystem(pregnancy.getDigestiveSystem());
        medCardDto.setUrinarySystem(pregnancy.getUrinarySystem());
        medCardDto.setEdema(pregnancy.getEdema());
        medCardDto.setBonePelvis(pregnancy.getBonePelvis());
        medCardDto.setUterineFundusHeight(pregnancy.getUterineFundusHeight());
        medCardDto.setFetalHeartbeat(pregnancy.getFetalHeartbeat());
        medCardDto.setExternalGenitalia(pregnancy.getExternalGenitalia());
        medCardDto.setExaminationOfCervixInMirrors(pregnancy.getExaminationOfCervixInMirrors());
        medCardDto.setBimanualStudy(pregnancy.getBimanualStudy());
        medCardDto.setVaginalDischarge(pregnancy.getVaginalDischarge());
        medCardDto.setProvisionalDiagnosis(pregnancy.getProvisionalDiagnosis());
        medCardDto.setVacationFromForPregnancy(pregnancy.getVacationFromForPregnancy());
        medCardDto.setVacationUntilForPregnancy(pregnancy.getVacationUntilForPregnancy());

        medCardDto.setAllergicToDrugs(pregnancy.getAllergicToDrugs());
        medCardDto.setPastIllnessesAndSurgeries(pregnancy.getPastIllnessesAndSurgeries());

        List<Appointment> appointments = appointmentRepository.findAll();
        HashMap<String, String> appointmentsMap = new HashMap<>();
        appointments.forEach(
                a -> appointmentsMap.put(a.getAppointmentType().getName(), a.getResult())
        );

        medCardDto.setTypeResultAppointments(appointmentsMap);

        return medCardDto;
    }

    public UpdateMedCard updateMedCard(UpdateMedCard updateMedCard) {
        User user = userRepository.findById(updateMedCard.getUser_id())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User was not found with id: " + updateMedCard.getUser_id())
                );

        user.setFirstName(updateMedCard.getFirstName());
        user.setLastName(updateMedCard.getLastName());
        user.setMiddleName(updateMedCard.getMiddleName());
        user.setEmail(updateMedCard.getEmail());
        user.setPhoneNumber(updateMedCard.getPhoneNumber());

        Patient patient = patientRepository.findByUserUserId(user.getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Patient was not found with user_id: " + user.getUserId())
                );

        patient.setBirthday(updateMedCard.getBirthday());
        patient.setAge(updateMedCard.getAge());
        patient.setPin(updateMedCard.getPin());
        patient.setCitizenship(updateMedCard.getCitizenship());
        patient.setPatientCategory(updateMedCard.getPatientCategory());
        patient.setWorkPlace(updateMedCard.getWorkPlace());
        patient.setPosition(updateMedCard.getPosition());
        patient.setWorkConditions(updateMedCard.getWorkConditions());
        patient.setWorksNow(updateMedCard.getWorksNow());
        patient.setHusbandFirstName(updateMedCard.getHusbandFirstName());
        patient.setHusbandLastName(updateMedCard.getHusbandLastName());
        patient.setHusbandMiddleName(updateMedCard.getHusbandMiddleName());
        patient.setHusbandWorkPlace(updateMedCard.getHusbandWorkPlace());
        patient.setHusbandPosition(updateMedCard.getHusbandPosition());
        patient.setHusbandPhoneNumber(updateMedCard.getHusbandPhoneNumber());
        patient.setMarried(updateMedCard.getMarried());
        patient.setEducation(updateMedCard.getEducation());

        Address address = patient.getAddress();
        address.setPatientAddress(updateMedCard.getPatientAddress());
        address.setPhoneNumber(updateMedCard.getPatientHomePhoneNumber());
        address.setRelativeAddress(updateMedCard.getRelativeAddress());
        address.setRelativePhoneNumber(updateMedCard.getRelativePhoneNumber());

        Insurance insurance = patient.getInsurance();
        insurance.setTerritoryName(updateMedCard.getInsuranceTerritoryName());
        insurance.setNumber(updateMedCard.getInsuranceNumber());

        User userDoctor = userRepository.findByEmail(updateMedCard.getDoctor());
        Doctor doctor = doctorRepository.findDoctorByUser(userDoctor.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor was not found with user_id: " + userDoctor.getUserId()));

        Pregnancy pregnancy = pregnancyRepository.findById(patient.getCurrentPregnancyId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Pregnancy was not found with id: " + patient.getCurrentPregnancyId())
                );

        pregnancy.setDoctor(doctor);
        pregnancy.setBloodType(updateMedCard.getBloodType());
        pregnancy.setRhFactorPregnant(updateMedCard.getRhFactorPregnant());
        pregnancy.setRhFactorPartner(updateMedCard.getRhFactorPartner());
        pregnancy.setTiterRhFactorInTwentyEightMonth(updateMedCard.getTiterRhFactorInTwentyEightMonth());
        pregnancy.setBloodRw(updateMedCard.getBloodRw());
        pregnancy.setBloodHiv(updateMedCard.getBloodHiv());
        pregnancy.setBloodHivPartner(updateMedCard.getBloodHivPartner());
        pregnancy.setRegistrationDate(updateMedCard.getRegistrationDate());
        pregnancy.setFromAnotherMedOrganizationReason(updateMedCard.getFromAnotherMedOrganizationReason());
        pregnancy.setNameOfAnotherMedOrganization(updateMedCard.getNameOfAnotherMedOrganization());
        pregnancy.setPregnancyNumber(updateMedCard.getPregnancyNumber());
        pregnancy.setChildbirthNumber(updateMedCard.getChildbirthNumber());
        pregnancy.setGestationalAgeByLastMenstruation(updateMedCard.getGestationalAgeByLastMenstruation());
        pregnancy.setGestationalAgeByUltrasound(updateMedCard.getGestationalAgeByUltrasound());
        pregnancy.setEstimatedDateOfBirth(updateMedCard.getEstimatedDateOfBirth());
        pregnancy.setLateRegistrationReason(updateMedCard.getLateRegistrationReason());
        pregnancy.setFirstVisitWeekOfPregnancy(updateMedCard.getFirstVisitWeekOfPregnancy());
        pregnancy.setFirstVisitComplaints(updateMedCard.getFirstVisitComplaints());
        pregnancy.setFirstVisitGrowth(updateMedCard.getFirstVisitGrowth());
        pregnancy.setFirstVisitWeight(updateMedCard.getFirstVisitWeight());
        pregnancy.setBodyMassIndex(calculateBmx(updateMedCard.getFirstVisitWeight(), updateMedCard.getFirstVisitGrowth()));
        pregnancy.setSkinAndMucousMembranes(updateMedCard.getSkinAndMucousMembranes());
        pregnancy.setThyroid(updateMedCard.getThyroid());
        pregnancy.setMilkGlands(updateMedCard.getMilkGlands());
        pregnancy.setPeripheralLymphNodes(updateMedCard.getPeripheralLymphNodes());
        pregnancy.setRespiratorySystem(updateMedCard.getRespiratorySystem());
        pregnancy.setCardiovascularSystem(updateMedCard.getCardiovascularSystem());
        pregnancy.setArterialPressure(updateMedCard.getArterialPressure());
        pregnancy.setDigestiveSystem(updateMedCard.getDigestiveSystem());
        pregnancy.setUrinarySystem(updateMedCard.getUrinarySystem());
        pregnancy.setEdema(updateMedCard.getEdema());
        pregnancy.setBonePelvis(updateMedCard.getBonePelvis());
        pregnancy.setUterineFundusHeight(updateMedCard.getUterineFundusHeight());
        pregnancy.setFetalHeartbeat(updateMedCard.getFetalHeartbeat());
        pregnancy.setExternalGenitalia(updateMedCard.getExternalGenitalia());
        pregnancy.setExaminationOfCervixInMirrors(updateMedCard.getExaminationOfCervixInMirrors());
        pregnancy.setBimanualStudy(updateMedCard.getBimanualStudy());
        pregnancy.setVaginalDischarge(updateMedCard.getVaginalDischarge());
        pregnancy.setProvisionalDiagnosis(updateMedCard.getProvisionalDiagnosis());
        pregnancy.setVacationFromForPregnancy(updateMedCard.getVacationFromForPregnancy());
        pregnancy.setVacationUntilForPregnancy(updateMedCard.getVacationUntilForPregnancy());
        pregnancy.setAllergicToDrugs(updateMedCard.getAllergicToDrugs());
        pregnancy.setPastIllnessesAndSurgeries(updateMedCard.getPastIllnessesAndSurgeries());

        pregnancyRepository.save(pregnancy);

        userRepository.save(user);

        patientRepository.save(patient);

        addressRepository.save(address);

        insuranceRepository.save(insurance);

        return updateMedCard;
    }

    public List<PatientDataDto> getAllPatients() {
        List<User> users = userRepository.findAll(Role.PATIENT);
        List<PatientDataDto> listDto = new ArrayList<>();

        for (User u : users) {
            PatientDataDto dto = new PatientDataDto();
            Patient patient = u.getPatient();
            Address address = patient.getAddress();
            dto.setPatientId(patient.getId());
            dto.setFIO(u.getLastName() + " " + u.getFirstName().substring(0, 1) + "." + u.getMiddleName().substring(0, 1) + ".");
            dto.setPhoneNumber(u.getPhoneNumber());
            dto.setEmail(u.getEmail());
            dto.setCurrentWeekOfPregnancy(getCurrentWeekOfPregnancy(new RequestPatient(u.getUserId())));
            dto.setResidenceAddress(address.getRelativeAddress());
            dto.setStatus(u.getStatus().toString());
            listDto.add(dto);
        }
        return listDto;
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public User getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName());
    }

    public String calculateBmx(Double weight, double height) {
        double result = weight / (height * height);
        return String.format("%.1f", result);
    }

}
