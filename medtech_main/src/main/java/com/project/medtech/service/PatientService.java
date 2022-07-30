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
import java.util.Set;

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
    private final AppointmentTypeRepository appointmentTypeRepository;

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

    public MedCardDto registerPatient(MedCardDto medCardDto) {
        User user = new User();
        user.setFirstName(medCardDto.getFirstName());
        user.setLastName(medCardDto.getLastName());
        user.setMiddleName(medCardDto.getMiddleName());
        user.setEmail(medCardDto.getEmail());
        user.setPhoneNumber(medCardDto.getPhoneNumber());
        user.setOtpUsed(false);
        user.setRole(Role.PATIENT);
        user.setStatus(Status.ACTIVE);
        String password = emailSenderService.send(medCardDto.getEmail(), "otp");
        user.setPassword(passwordEncoder().encode(password));

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setBirthday(medCardDto.getBirthday());
        patient.setAge(medCardDto.getAge());
        patient.setPin(medCardDto.getPin());
        patient.setCitizenship(medCardDto.getCitizenship());
        patient.setPatientCategory(medCardDto.getPatientCategory());
        patient.setWorkPlace(medCardDto.getWorkPlace());
        patient.setPosition(medCardDto.getPosition());
        patient.setWorkConditions(medCardDto.getWorkConditions());
        patient.setWorksNow(medCardDto.getWorksNow());
        patient.setHusbandFirstName(medCardDto.getHusbandFirstName());
        patient.setHusbandLastName(medCardDto.getHusbandLastName());
        patient.setHusbandMiddleName(medCardDto.getHusbandMiddleName());
        patient.setHusbandWorkPlace(medCardDto.getHusbandWorkPlace());
        patient.setHusbandPosition(medCardDto.getHusbandPosition());
        patient.setHusbandPhoneNumber(medCardDto.getHusbandPhoneNumber());
        patient.setMarried(medCardDto.getMarried());
        patient.setEducation(medCardDto.getEducation());
        patient.setUser(user);

        Address address = new Address();
        address.setPatientAddress(medCardDto.getPatientAddress());
        address.setPhoneNumber(medCardDto.getPatientHomePhoneNumber());
        address.setRelativeAddress(medCardDto.getRelativeAddress());
        address.setRelativePhoneNumber(medCardDto.getRelativePhoneNumber());
        address.setPatient(patient);

        Insurance insurance = new Insurance();
        insurance.setTerritoryName(medCardDto.getInsuranceTerritoryName());
        insurance.setNumber(medCardDto.getInsuranceNumber());
        insurance.setPatient(patient);

        User userDoctor = userRepository.findByEmail(medCardDto.getDoctor());
        Doctor doctor = doctorRepository.findDoctorByUser(userDoctor.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor was not found with user_id: " + userDoctor.getUserId()));

        Pregnancy pregnancy = new Pregnancy();
        pregnancy.setDoctor(doctor);
        pregnancy.setBloodType(medCardDto.getBloodType());
        pregnancy.setRhFactorPregnant(medCardDto.getRhFactorPregnant());
        pregnancy.setRhFactorPartner(medCardDto.getRhFactorPartner());
        pregnancy.setTiterRhFactorInTwentyEightMonth(medCardDto.getTiterRhFactorInTwentyEightMonth());
        pregnancy.setBloodRw(medCardDto.getBloodRw());
        pregnancy.setBloodHiv(medCardDto.getBloodHiv());
        pregnancy.setBloodHivPartner(medCardDto.getBloodHivPartner());
        pregnancy.setRegistrationDate(medCardDto.getRegistrationDate());
        pregnancy.setFromAnotherMedOrganizationReason(medCardDto.getFromAnotherMedOrganizationReason());
        pregnancy.setNameOfAnotherMedOrganization(medCardDto.getNameOfAnotherMedOrganization());
        pregnancy.setPregnancyNumber(medCardDto.getPregnancyNumber());
        pregnancy.setChildbirthNumber(medCardDto.getChildbirthNumber());
        pregnancy.setGestationalAgeByLastMenstruation(medCardDto.getGestationalAgeByLastMenstruation());
        pregnancy.setGestationalAgeByUltrasound(medCardDto.getGestationalAgeByUltrasound());
        pregnancy.setEstimatedDateOfBirth(medCardDto.getEstimatedDateOfBirth());
        pregnancy.setLateRegistrationReason(medCardDto.getLateRegistrationReason());
        pregnancy.setFirstVisitWeekOfPregnancy(medCardDto.getFirstVisitWeekOfPregnancy());
        pregnancy.setFirstVisitComplaints(medCardDto.getFirstVisitComplaints());
        pregnancy.setFirstVisitGrowth(medCardDto.getFirstVisitGrowth());
        pregnancy.setFirstVisitWeight(medCardDto.getFirstVisitWeight());
        pregnancy.setBodyMassIndex(calculateBmx(medCardDto.getFirstVisitWeight(), medCardDto.getFirstVisitGrowth()));
        pregnancy.setSkinAndMucousMembranes(medCardDto.getSkinAndMucousMembranes());
        pregnancy.setThyroid(medCardDto.getThyroid());
        pregnancy.setMilkGlands(medCardDto.getMilkGlands());
        pregnancy.setPeripheralLymphNodes(medCardDto.getPeripheralLymphNodes());
        pregnancy.setRespiratorySystem(medCardDto.getRespiratorySystem());
        pregnancy.setCardiovascularSystem(medCardDto.getCardiovascularSystem());
        pregnancy.setArterialPressure(medCardDto.getArterialPressure());
        pregnancy.setDigestiveSystem(medCardDto.getDigestiveSystem());
        pregnancy.setUrinarySystem(medCardDto.getUrinarySystem());
        pregnancy.setEdema(medCardDto.getEdema());
        pregnancy.setBonePelvis(medCardDto.getBonePelvis());
        pregnancy.setUterineFundusHeight(medCardDto.getUterineFundusHeight());
        pregnancy.setFetalHeartbeat(medCardDto.getFetalHeartbeat());
        pregnancy.setExternalGenitalia(medCardDto.getExternalGenitalia());
        pregnancy.setExaminationOfCervixInMirrors(medCardDto.getExaminationOfCervixInMirrors());
        pregnancy.setBimanualStudy(medCardDto.getBimanualStudy());
        pregnancy.setVaginalDischarge(medCardDto.getVaginalDischarge());
        pregnancy.setProvisionalDiagnosis(medCardDto.getProvisionalDiagnosis());
        pregnancy.setVacationFromForPregnancy(medCardDto.getVacationFromForPregnancy());
        pregnancy.setVacationUntilForPregnancy(medCardDto.getVacationUntilForPregnancy());
        pregnancy.setAllergicToDrugs(medCardDto.getAllergicToDrugs());
        pregnancy.setPastIllnessesAndSurgeries(medCardDto.getPastIllnessesAndSurgeries());

        HashMap<String, String> map = medCardDto.getTypeResultAppointments();
        Set<String> keys = map.keySet();
        for(String type: keys) {
            AppointmentType appointmentType = appointmentTypeRepository.findByName(type)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Appointment type was not found with name: " + type)
                    );
            Appointment appointment = new Appointment();
            appointment.setAppointmentType(appointmentType);
            appointment.setPregnancy(pregnancy);
            appointment.setResult(map.get(type));
            appointmentRepository.save(appointment);
        }

        pregnancyRepository.save(pregnancy);

        patient.setCurrentPregnancyId(pregnancy.getId());

        patient.getPregnancy().add(pregnancy);

        userRepository.save(user);

        patientRepository.save(patient);

        addressRepository.save(address);

        insuranceRepository.save(insurance);

        return medCardDto;
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
