package com.project.medtech.service;

import com.project.medtech.dto.*;
import com.project.medtech.dto.enums.*;
import com.project.medtech.exception.AlreadyExistsException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.mapper.CheckListInfoDtoMapper;
import com.project.medtech.model.*;
import com.project.medtech.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


    public Integer calculateCurrentWeekOfPregnancy(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new ResourceNotFoundException("User was not found with email: " + email);
        }

        PatientEntity patientEntity = userEntity.getPatientEntity();

        PregnancyEntity pregnancyEntity = patientEntity.getPregnancy();

        if (pregnancyEntity.getFirstVisitDate() == null || pregnancyEntity.getFirstVisitWeekOfPregnancy() == null) {
            return 0;
        }

        LocalDate firstVisitDate = pregnancyEntity.getFirstVisitDate();
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        long diffInDays = ChronoUnit.DAYS.between(firstVisitDate, currentDate);
        long diffInWeeks = diffInDays / 7;
        return pregnancyEntity.getFirstVisitWeekOfPregnancy() + (int) diffInWeeks;
    }

    public List<CheckListInfoDto> getAllPatientsCheckLists(Long reqPat) {
        PatientEntity patientEntity = patientRepository.findById(reqPat)
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with ID : " + reqPat));
        List<CheckListEntity> list = checkListRepository.findAllByPatientEntity(patientEntity);
        List<CheckListInfoDto> listDto = new ArrayList<>();

        for (CheckListEntity checkListEntity : list) {
            listDto.add(CheckListInfoDtoMapper.EntityToDto(checkListEntity));
        }

        return listDto;
    }

    public PhoneNumberDto changePhoneNumber(PhoneNumberDto phoneNumberDto) {
        UserEntity userEntity = getAuthentication();
        userEntity.setPhoneNumber(phoneNumberDto.getPhoneNumber());

        userRepository.save(userEntity);

        return phoneNumberDto;
    }

    public AddressDto changeAddress(AddressDto addressDto) {
        UserEntity userEntity = getAuthentication();

        PatientEntity patientEntity = patientRepository.findByUserEntityUserId(userEntity.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with user_id: " + userEntity.getUserId()));

        AddressEntity addressEntity = addressRepository.findByPatientEntityId(patientEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No Address with patient_id: " + userEntity.getUserId()));

        addressEntity.setPatientAddress(addressDto.getAddress());

        addressRepository.save(addressEntity);

        return addressDto;
    }

    @Transactional
    public MedCardDto registerPatient(MedCardDto registerPatientDto) {
        if (userRepository.existsByEmail(registerPatientDto.getEmail())) {
            throw new AlreadyExistsException("The given email is already used. Enter another email.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(registerPatientDto.getFirstName());
        userEntity.setLastName(registerPatientDto.getLastName());
        userEntity.setMiddleName(registerPatientDto.getMiddleName());
        userEntity.setEmail(registerPatientDto.getEmail());
        userEntity.setPhoneNumber(registerPatientDto.getPhoneNumber());
        userEntity.setOtpUsed(false);
        userEntity.setImageUrl(DefaultImageUrl.DEFAULT_IMAGE_THREE.getUrl());
        RoleEntity roleEntity = roleRepository.findByName("PATIENT")
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No role was found with name: PATIENT")
                );
        userEntity.setRoleEntity(roleEntity);
        userEntity.setStatus(Status.ACTIVE);
        String password = emailSenderService.send(registerPatientDto.getEmail(), "otp");
        userEntity.setPassword(passwordEncoder.encode(password));

        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setUserEntity(userEntity);
        patientEntity.setBirthday(registerPatientDto.getBirthday());
        patientEntity.setAge(calculateAge(registerPatientDto.getBirthday()));
        patientEntity.setCitizenship(registerPatientDto.getCitizenship());
        patientEntity.setPatientCategory(registerPatientDto.getPatientCategory());
        patientEntity.setWorkPlace(registerPatientDto.getWorkPlace());
        patientEntity.setPosition(registerPatientDto.getPosition());
        patientEntity.setWorkConditions(registerPatientDto.getWorkConditions());
        patientEntity.setWorksNow(registerPatientDto.getWorksNow());
        patientEntity.setHusbandFirstName(registerPatientDto.getHusbandFirstName());
        patientEntity.setHusbandLastName(registerPatientDto.getHusbandLastName());
        patientEntity.setHusbandMiddleName(registerPatientDto.getHusbandMiddleName());
        patientEntity.setHusbandWorkPlace(registerPatientDto.getHusbandWorkPlace());
        patientEntity.setHusbandPosition(registerPatientDto.getHusbandPosition());
        patientEntity.setHusbandPhoneNumber(registerPatientDto.getHusbandPhoneNumber());
        patientEntity.setMarried(registerPatientDto.getMarried());
        patientEntity.setEducation(registerPatientDto.getEducation());
        patientEntity.setUserEntity(userEntity);

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setPatientAddress(registerPatientDto.getPatientAddress());
        addressEntity.setPhoneNumber(registerPatientDto.getPatientHomePhoneNumber());
        addressEntity.setRelativeAddress(registerPatientDto.getRelativeAddress());
        addressEntity.setRelativePhoneNumber(registerPatientDto.getRelativePhoneNumber());
        addressEntity.setPatientEntity(patientEntity);

        InsuranceEntity insuranceEntity = new InsuranceEntity();
        insuranceEntity.setTerritoryName(registerPatientDto.getInsuranceTerritoryName());
        insuranceEntity.setNumber(registerPatientDto.getInsuranceNumber());
        insuranceEntity.setPatientEntity(patientEntity);

        UserEntity userEntityDoctor = userRepository.findByEmail(registerPatientDto.getDoctor());
        if (userEntityDoctor == null) {
            throw new ResourceNotFoundException("User was not found with email: " + registerPatientDto.getDoctor());
        }
        DoctorEntity doctorEntity = doctorRepository.findDoctorByUser(userEntityDoctor.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor was not found with user_id: " + userEntityDoctor.getUserId()));

        PregnancyEntity pregnancyEntity = new PregnancyEntity();
        pregnancyEntity.setDoctorEntity(doctorEntity);
        pregnancyEntity.setPatientEntity(patientEntity);
        pregnancyEntity.setBloodType(registerPatientDto.getBloodType());
        pregnancyEntity.setRhFactorPregnant(registerPatientDto.getRhFactorPregnant());
        pregnancyEntity.setRhFactorPartner(registerPatientDto.getRhFactorPartner());
        pregnancyEntity.setTiterRhFactorInTwentyEightMonth(registerPatientDto.getTiterRhFactorInTwentyEightMonth());
        pregnancyEntity.setBloodRw(registerPatientDto.getBloodRw());
        pregnancyEntity.setBloodHiv(registerPatientDto.getBloodHiv());
        pregnancyEntity.setBloodHivPartner(registerPatientDto.getBloodHivPartner());
        pregnancyEntity.setRegistrationDate(registerPatientDto.getRegistrationDate());
        pregnancyEntity.setFirstVisitDate(registerPatientDto.getFirstVisitDate());
        pregnancyEntity.setFromAnotherMedOrganizationReason(registerPatientDto.getFromAnotherMedOrganizationReason());
        pregnancyEntity.setNameOfAnotherMedOrganization(registerPatientDto.getNameOfAnotherMedOrganization());
        pregnancyEntity.setPregnancyNumber(registerPatientDto.getPregnancyNumber());
        pregnancyEntity.setChildbirthNumber(registerPatientDto.getChildbirthNumber());
        pregnancyEntity.setGestationalAgeByLastMenstruation(registerPatientDto.getGestationalAgeByLastMenstruation());
        pregnancyEntity.setGestationalAgeByUltrasound(registerPatientDto.getGestationalAgeByUltrasound());
        pregnancyEntity.setEstimatedDateOfBirth(registerPatientDto.getEstimatedDateOfBirth());
        pregnancyEntity.setLateRegistrationReason(registerPatientDto.getLateRegistrationReason());
        pregnancyEntity.setFirstVisitWeekOfPregnancy(registerPatientDto.getFirstVisitWeekOfPregnancy());
        pregnancyEntity.setFirstVisitComplaints(registerPatientDto.getFirstVisitComplaints());
        pregnancyEntity.setFirstVisitGrowth(registerPatientDto.getFirstVisitGrowth());
        pregnancyEntity.setFirstVisitWeight(registerPatientDto.getFirstVisitWeight());
        if (registerPatientDto.getFirstVisitGrowth() != null && registerPatientDto.getFirstVisitWeight() != null) {
            pregnancyEntity.setBodyMassIndex(calculateBmx(registerPatientDto.getFirstVisitWeight(), registerPatientDto.getFirstVisitGrowth()));
        }
        pregnancyEntity.setSkinAndMucousMembranes(registerPatientDto.getSkinAndMucousMembranes());
        pregnancyEntity.setThyroid(registerPatientDto.getThyroid());
        pregnancyEntity.setMilkGlands(registerPatientDto.getMilkGlands());
        pregnancyEntity.setPeripheralLymphNodes(registerPatientDto.getPeripheralLymphNodes());
        pregnancyEntity.setRespiratorySystem(registerPatientDto.getRespiratorySystem());
        pregnancyEntity.setCardiovascularSystem(registerPatientDto.getCardiovascularSystem());
        pregnancyEntity.setArterialPressure(registerPatientDto.getArterialPressure());
        pregnancyEntity.setDigestiveSystem(registerPatientDto.getDigestiveSystem());
        pregnancyEntity.setUrinarySystem(registerPatientDto.getUrinarySystem());
        pregnancyEntity.setEdema(registerPatientDto.getEdema());
        pregnancyEntity.setBonePelvis(registerPatientDto.getBonePelvis());
        pregnancyEntity.setUterineFundusHeight(registerPatientDto.getUterineFundusHeight());
        pregnancyEntity.setFetalHeartbeat(registerPatientDto.getFetalHeartbeat());
        pregnancyEntity.setExternalGenitalia(registerPatientDto.getExternalGenitalia());
        pregnancyEntity.setExaminationOfCervixInMirrors(registerPatientDto.getExaminationOfCervixInMirrors());
        pregnancyEntity.setBimanualStudy(registerPatientDto.getBimanualStudy());
        pregnancyEntity.setVaginalDischarge(registerPatientDto.getVaginalDischarge());
        pregnancyEntity.setProvisionalDiagnosis(registerPatientDto.getProvisionalDiagnosis());
        pregnancyEntity.setVacationFromForPregnancy(registerPatientDto.getVacationFromForPregnancy());
        pregnancyEntity.setVacationUntilForPregnancy(registerPatientDto.getVacationUntilForPregnancy());
        pregnancyEntity.setAllergicToDrugs(registerPatientDto.getAllergicToDrugs());
        pregnancyEntity.setPastIllnessesAndSurgeries(registerPatientDto.getPastIllnessesAndSurgeries());
        pregnancyEntity.setDisabilityListNumber(registerPatientDto.getDisabilityListNumber());

        List<AppointmentTypeEntity> appointmentTypeEntities = appointmentTypeRepository.findAll();

        HashMap<String, String> map = (HashMap<String, String>) registerPatientDto.getTypeResultAppointments();

        appointmentTypeEntities.forEach(
                a -> {
                    AppointmentEntity appointmentEntity = new AppointmentEntity();
                    if (registerPatientDto.getTypeResultAppointments() != null && map.containsKey(a.getName())) {
                        appointmentEntity.setAppointmentTypeEntity(a);
                        appointmentEntity.setResult(map.get(a.getName()));
                        appointmentEntity.setPregnancyEntity(pregnancyEntity);
                    } else {
                        appointmentEntity.setAppointmentTypeEntity(a);
                        appointmentEntity.setResult("");
                        appointmentEntity.setPregnancyEntity(pregnancyEntity);
                    }
                    appointmentRepository.save(appointmentEntity);
                }
        );

        pregnancyRepository.save(pregnancyEntity);

        patientRepository.save(patientEntity);

        userRepository.save(userEntity);

        addressRepository.save(addressEntity);

        insuranceRepository.save(insuranceEntity);

        return registerPatientDto;
    }

    public MedCardDto getPatientMedCardInfo(Long patientId) {
        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient was not found with id: " + patientId)
                );

        UserEntity userEntity = patientEntity.getUserEntity();

        PregnancyEntity pregnancyEntity = patientEntity.getPregnancy();

        DoctorEntity doctorEntity = pregnancyEntity.getDoctorEntity();

        MedCardDto medCardDto = new MedCardDto();

        medCardDto.setEmail(userEntity.getEmail());
        medCardDto.setFirstName(userEntity.getFirstName());
        medCardDto.setLastName(userEntity.getLastName());
        medCardDto.setMiddleName(userEntity.getMiddleName());
        medCardDto.setPhoneNumber(userEntity.getPhoneNumber());

        medCardDto.setDoctor(String.format("%s %s %s", doctorEntity.getUserEntity().getLastName(),
                doctorEntity.getUserEntity().getFirstName(), doctorEntity.getUserEntity().getMiddleName()));

        medCardDto.setBirthday(patientEntity.getBirthday());
        medCardDto.setAge(patientEntity.getAge());
        medCardDto.setCitizenship(patientEntity.getCitizenship());
        medCardDto.setPatientCategory(patientEntity.getPatientCategory());
        medCardDto.setWorkPlace(patientEntity.getWorkPlace());
        medCardDto.setPosition(patientEntity.getPosition());
        medCardDto.setWorkConditions(patientEntity.getWorkConditions());
        medCardDto.setWorksNow(patientEntity.getWorksNow());
        medCardDto.setHusbandFirstName(patientEntity.getHusbandFirstName());
        medCardDto.setHusbandLastName(patientEntity.getHusbandLastName());
        medCardDto.setHusbandMiddleName(patientEntity.getHusbandMiddleName());
        medCardDto.setHusbandWorkPlace(patientEntity.getHusbandWorkPlace());
        medCardDto.setHusbandPosition(patientEntity.getHusbandPosition());
        medCardDto.setHusbandPhoneNumber(patientEntity.getHusbandPhoneNumber());

        medCardDto.setMarried(patientEntity.getMarried());
        medCardDto.setEducation(patientEntity.getEducation());

        medCardDto.setPatientAddress(patientEntity.getAddressEntity().getPatientAddress());
        medCardDto.setPatientHomePhoneNumber(patientEntity.getAddressEntity().getPhoneNumber());
        medCardDto.setRelativeAddress(patientEntity.getAddressEntity().getRelativeAddress());
        medCardDto.setRelativePhoneNumber(patientEntity.getAddressEntity().getRelativePhoneNumber());

        medCardDto.setInsuranceTerritoryName(patientEntity.getInsuranceEntity().getTerritoryName());
        medCardDto.setInsuranceNumber(patientEntity.getInsuranceEntity().getNumber());

        medCardDto.setBloodType(pregnancyEntity.getBloodType());
        medCardDto.setRhFactorPregnant(pregnancyEntity.getRhFactorPregnant());
        medCardDto.setRhFactorPartner(pregnancyEntity.getRhFactorPartner());
        medCardDto.setTiterRhFactorInTwentyEightMonth(pregnancyEntity.getTiterRhFactorInTwentyEightMonth());
        medCardDto.setBloodRw(pregnancyEntity.getBloodRw());
        medCardDto.setBloodHiv(pregnancyEntity.getBloodHiv());
        medCardDto.setBloodHivPartner(pregnancyEntity.getBloodHivPartner());
        medCardDto.setRegistrationDate(pregnancyEntity.getRegistrationDate());
        medCardDto.setFirstVisitDate(pregnancyEntity.getFirstVisitDate());
        medCardDto.setFirstVisitWeekOfPregnancy(pregnancyEntity.getFirstVisitWeekOfPregnancy());
        medCardDto.setFromAnotherMedOrganizationReason(pregnancyEntity.getFromAnotherMedOrganizationReason());
        medCardDto.setNameOfAnotherMedOrganization(pregnancyEntity.getNameOfAnotherMedOrganization());
        medCardDto.setPregnancyNumber(pregnancyEntity.getPregnancyNumber());
        medCardDto.setChildbirthNumber(pregnancyEntity.getChildbirthNumber());
        medCardDto.setGestationalAgeByLastMenstruation(pregnancyEntity.getGestationalAgeByLastMenstruation());
        medCardDto.setGestationalAgeByUltrasound(pregnancyEntity.getGestationalAgeByUltrasound());
        medCardDto.setEstimatedDateOfBirth(pregnancyEntity.getEstimatedDateOfBirth());
        medCardDto.setLateRegistrationReason(pregnancyEntity.getLateRegistrationReason());
        medCardDto.setFirstVisitComplaints(pregnancyEntity.getFirstVisitComplaints());
        medCardDto.setFirstVisitGrowth(pregnancyEntity.getFirstVisitGrowth());
        medCardDto.setFirstVisitWeight(pregnancyEntity.getFirstVisitWeight());
        medCardDto.setBodyMassIndex(pregnancyEntity.getBodyMassIndex());
        medCardDto.setSkinAndMucousMembranes(pregnancyEntity.getSkinAndMucousMembranes());
        medCardDto.setThyroid(pregnancyEntity.getThyroid());
        medCardDto.setMilkGlands(pregnancyEntity.getMilkGlands());
        medCardDto.setPeripheralLymphNodes(pregnancyEntity.getPeripheralLymphNodes());
        medCardDto.setRespiratorySystem(pregnancyEntity.getRespiratorySystem());
        medCardDto.setCardiovascularSystem(pregnancyEntity.getCardiovascularSystem());
        medCardDto.setArterialPressure(pregnancyEntity.getArterialPressure());
        medCardDto.setDigestiveSystem(pregnancyEntity.getDigestiveSystem());
        medCardDto.setUrinarySystem(pregnancyEntity.getUrinarySystem());
        medCardDto.setEdema(pregnancyEntity.getEdema());
        medCardDto.setBonePelvis(pregnancyEntity.getBonePelvis());
        medCardDto.setUterineFundusHeight(pregnancyEntity.getUterineFundusHeight());
        medCardDto.setFetalHeartbeat(pregnancyEntity.getFetalHeartbeat());
        medCardDto.setExternalGenitalia(pregnancyEntity.getExternalGenitalia());
        medCardDto.setExaminationOfCervixInMirrors(pregnancyEntity.getExaminationOfCervixInMirrors());
        medCardDto.setBimanualStudy(pregnancyEntity.getBimanualStudy());
        medCardDto.setVaginalDischarge(pregnancyEntity.getVaginalDischarge());
        medCardDto.setProvisionalDiagnosis(pregnancyEntity.getProvisionalDiagnosis());
        medCardDto.setVacationFromForPregnancy(pregnancyEntity.getVacationFromForPregnancy());
        medCardDto.setVacationUntilForPregnancy(pregnancyEntity.getVacationUntilForPregnancy());
        medCardDto.setDisabilityListNumber(pregnancyEntity.getDisabilityListNumber());

        medCardDto.setAllergicToDrugs(pregnancyEntity.getAllergicToDrugs());
        medCardDto.setPastIllnessesAndSurgeries(pregnancyEntity.getPastIllnessesAndSurgeries());

        List<AppointmentEntity> appointmentEntities = pregnancyEntity.getAppointmentEntities();
        HashMap<String, String> appointmentsMap = new HashMap<>();
        appointmentEntities.forEach(
                a -> appointmentsMap.put(a.getAppointmentTypeEntity().getName(), a.getResult())
        );

        medCardDto.setTypeResultAppointments(appointmentsMap);

        return medCardDto;
    }

    @Transactional
    public UpdateMedCard updateMedCard(UpdateMedCard updateMedCard) {
        PatientEntity patientEntity = patientRepository.findById(updateMedCard.getPatientId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Patient was not found with id: " + updateMedCard.getPatientId())
                );

        UserEntity userEntity = patientEntity.getUserEntity();

        userEntity.setFirstName(updateMedCard.getFirstName());
        userEntity.setLastName(updateMedCard.getLastName());
        userEntity.setMiddleName(updateMedCard.getMiddleName());

        if (!updateMedCard.getEmail().equals(userEntity.getEmail())) {
            if (userRepository.existsByEmail(updateMedCard.getEmail())) {
                throw new AlreadyExistsException("The given email is already used. Enter another email.");
            }

            userEntity.setEmail(updateMedCard.getEmail());
        }

        userEntity.setPhoneNumber(updateMedCard.getPhoneNumber());

        patientEntity.setBirthday(updateMedCard.getBirthday());
        patientEntity.setAge(calculateAge(updateMedCard.getBirthday()));
        patientEntity.setCitizenship(updateMedCard.getCitizenship());
        patientEntity.setPatientCategory(updateMedCard.getPatientCategory());
        patientEntity.setWorkPlace(updateMedCard.getWorkPlace());
        patientEntity.setPosition(updateMedCard.getPosition());
        patientEntity.setWorkConditions(updateMedCard.getWorkConditions());
        patientEntity.setWorksNow(updateMedCard.getWorksNow());
        patientEntity.setHusbandFirstName(updateMedCard.getHusbandFirstName());
        patientEntity.setHusbandLastName(updateMedCard.getHusbandLastName());
        patientEntity.setHusbandMiddleName(updateMedCard.getHusbandMiddleName());
        patientEntity.setHusbandWorkPlace(updateMedCard.getHusbandWorkPlace());
        patientEntity.setHusbandPosition(updateMedCard.getHusbandPosition());
        patientEntity.setHusbandPhoneNumber(updateMedCard.getHusbandPhoneNumber());
        patientEntity.setMarried(updateMedCard.getMarried());
        patientEntity.setEducation(updateMedCard.getEducation());

        AddressEntity addressEntity = patientEntity.getAddressEntity();
        addressEntity.setPatientAddress(updateMedCard.getPatientAddress());
        addressEntity.setPhoneNumber(updateMedCard.getPatientHomePhoneNumber());
        addressEntity.setRelativeAddress(updateMedCard.getRelativeAddress());
        addressEntity.setRelativePhoneNumber(updateMedCard.getRelativePhoneNumber());

        InsuranceEntity insuranceEntity = patientEntity.getInsuranceEntity();
        insuranceEntity.setTerritoryName(updateMedCard.getInsuranceTerritoryName());
        insuranceEntity.setNumber(updateMedCard.getInsuranceNumber());

        UserEntity userEntityDoctor = userRepository.findByEmail(updateMedCard.getDoctor());

        DoctorEntity doctorEntity = doctorRepository.findDoctorByUser(userEntityDoctor.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor was not found with user_id: " + userEntityDoctor.getUserId()));

        PregnancyEntity pregnancyEntity = patientEntity.getPregnancy();

        pregnancyEntity.setDoctorEntity(doctorEntity);
        pregnancyEntity.setBloodType(updateMedCard.getBloodType());
        pregnancyEntity.setRhFactorPregnant(updateMedCard.getRhFactorPregnant());
        pregnancyEntity.setRhFactorPartner(updateMedCard.getRhFactorPartner());
        pregnancyEntity.setTiterRhFactorInTwentyEightMonth(updateMedCard.getTiterRhFactorInTwentyEightMonth());
        pregnancyEntity.setBloodRw(updateMedCard.getBloodRw());
        pregnancyEntity.setBloodHiv(updateMedCard.getBloodHiv());
        pregnancyEntity.setBloodHivPartner(updateMedCard.getBloodHivPartner());
        pregnancyEntity.setRegistrationDate(updateMedCard.getRegistrationDate());
        pregnancyEntity.setFirstVisitDate(updateMedCard.getFirstVisitDate());
        pregnancyEntity.setFromAnotherMedOrganizationReason(updateMedCard.getFromAnotherMedOrganizationReason());
        pregnancyEntity.setNameOfAnotherMedOrganization(updateMedCard.getNameOfAnotherMedOrganization());
        pregnancyEntity.setPregnancyNumber(updateMedCard.getPregnancyNumber());
        pregnancyEntity.setChildbirthNumber(updateMedCard.getChildbirthNumber());
        pregnancyEntity.setGestationalAgeByLastMenstruation(updateMedCard.getGestationalAgeByLastMenstruation());
        pregnancyEntity.setGestationalAgeByUltrasound(updateMedCard.getGestationalAgeByUltrasound());
        pregnancyEntity.setEstimatedDateOfBirth(updateMedCard.getEstimatedDateOfBirth());
        pregnancyEntity.setLateRegistrationReason(updateMedCard.getLateRegistrationReason());
        pregnancyEntity.setFirstVisitWeekOfPregnancy(updateMedCard.getFirstVisitWeekOfPregnancy());
        pregnancyEntity.setFirstVisitComplaints(updateMedCard.getFirstVisitComplaints());
        pregnancyEntity.setFirstVisitGrowth(updateMedCard.getFirstVisitGrowth());
        pregnancyEntity.setFirstVisitWeight(updateMedCard.getFirstVisitWeight());
        if (updateMedCard.getFirstVisitGrowth() != null && updateMedCard.getFirstVisitWeight() != null) {
            pregnancyEntity.setBodyMassIndex
                    (calculateBmx(updateMedCard.getFirstVisitWeight(), updateMedCard.getFirstVisitGrowth()));
        }
        pregnancyEntity.setSkinAndMucousMembranes(updateMedCard.getSkinAndMucousMembranes());
        pregnancyEntity.setThyroid(updateMedCard.getThyroid());
        pregnancyEntity.setMilkGlands(updateMedCard.getMilkGlands());
        pregnancyEntity.setPeripheralLymphNodes(updateMedCard.getPeripheralLymphNodes());
        pregnancyEntity.setRespiratorySystem(updateMedCard.getRespiratorySystem());
        pregnancyEntity.setCardiovascularSystem(updateMedCard.getCardiovascularSystem());
        pregnancyEntity.setArterialPressure(updateMedCard.getArterialPressure());
        pregnancyEntity.setDigestiveSystem(updateMedCard.getDigestiveSystem());
        pregnancyEntity.setUrinarySystem(updateMedCard.getUrinarySystem());
        pregnancyEntity.setEdema(updateMedCard.getEdema());
        pregnancyEntity.setBonePelvis(updateMedCard.getBonePelvis());
        pregnancyEntity.setUterineFundusHeight(updateMedCard.getUterineFundusHeight());
        pregnancyEntity.setFetalHeartbeat(updateMedCard.getFetalHeartbeat());
        pregnancyEntity.setExternalGenitalia(updateMedCard.getExternalGenitalia());
        pregnancyEntity.setExaminationOfCervixInMirrors(updateMedCard.getExaminationOfCervixInMirrors());
        pregnancyEntity.setBimanualStudy(updateMedCard.getBimanualStudy());
        pregnancyEntity.setVaginalDischarge(updateMedCard.getVaginalDischarge());
        pregnancyEntity.setProvisionalDiagnosis(updateMedCard.getProvisionalDiagnosis());
        pregnancyEntity.setVacationFromForPregnancy(updateMedCard.getVacationFromForPregnancy());
        pregnancyEntity.setVacationUntilForPregnancy(updateMedCard.getVacationUntilForPregnancy());
        pregnancyEntity.setAllergicToDrugs(updateMedCard.getAllergicToDrugs());
        pregnancyEntity.setPastIllnessesAndSurgeries(updateMedCard.getPastIllnessesAndSurgeries());
        pregnancyEntity.setDisabilityListNumber(updateMedCard.getDisabilityListNumber());

        List<AppointmentEntity> appointmentEntities = pregnancyEntity.getAppointmentEntities();

        HashMap<String, String> map = (HashMap<String, String>) updateMedCard.getTypeResultAppointments();

        for (String key : map.keySet()) {
            for (AppointmentEntity a1 : appointmentEntities) {
                if (a1.getAppointmentTypeEntity().getName().equals(key)) {
                    a1.setResult(map.get(key));
                }
            }
        }

        pregnancyRepository.save(pregnancyEntity);

        userRepository.save(userEntity);

        patientRepository.save(patientEntity);

        addressRepository.save(addressEntity);

        insuranceRepository.save(insuranceEntity);

        return updateMedCard;
    }

    public PatientDto getPatientProfileMob() {
        UserEntity userEntity = getAuthentication();

        PatientEntity patientEntity = patientRepository.findByUserEntityUserId(userEntity.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No Patient with user_id: " + userEntity.getUserId()));

        PatientDto patientDto = new PatientDto();
        patientDto.setEmail(userEntity.getEmail());
        patientDto.setBirthday(patientEntity.getBirthday());
        patientDto.setAddress(patientEntity.getAddressEntity().getPatientAddress());
        patientDto.setFullName(userEntity.getLastName() + " " + userEntity.getFirstName() + " " + userEntity.getMiddleName());
        patientDto.setPhoneNumber(userEntity.getPhoneNumber());
        patientDto.setPatientId(patientEntity.getId());
        patientDto.setWeekOfPregnancy(calculateCurrentWeekOfPregnancy(userEntity.getEmail()));
        patientDto.setImageUrl(userEntity.getImageUrl());

        return patientDto;
    }

    public List<PatientDataDto> getAllPatients() {
        List<UserEntity> userEntities = userRepository.findAllByRoleEntityName(Role.PATIENT.name());

        List<PatientDataDto> listDto = new ArrayList<>();

        for (UserEntity u : userEntities) {
            PatientDataDto dto = new PatientDataDto();

            PatientEntity patientEntity = u.getPatientEntity();

            AddressEntity address = patientEntity.getAddressEntity();

            dto.setPatientId(patientEntity.getId());
            dto.setFIO(getFullName(u));
            dto.setPhoneNumber(u.getPhoneNumber());
            dto.setEmail(u.getEmail());
            dto.setCurrentWeekOfPregnancy(calculateCurrentWeekOfPregnancy(u.getEmail()));
            dto.setResidenceAddress(address.getPatientAddress());
            dto.setStatus(u.getStatus().toString());

            listDto.add(dto);
        }

        return listDto;
    }

    public PatientProfileDto getPatientProfileWeb(Long patientId) {
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Patient was not found with id: " + patientId)
                );

        UserEntity user = patient.getUserEntity();

        PregnancyEntity pregnancyEntity = patient.getPregnancy();

        PatientProfileDto patientProfileDto = new PatientProfileDto();

        patientProfileDto.setLastName(user.getLastName());
        patientProfileDto.setFirstName(user.getFirstName());
        patientProfileDto.setMiddleName(user.getMiddleName());
        patientProfileDto.setPhoneNumber(user.getPhoneNumber());
        patientProfileDto.setImageUrl(user.getImageUrl());
        patientProfileDto.setEmail(user.getEmail());
        patientProfileDto.setDoctor(getFullName(pregnancyEntity.getDoctorEntity().getUserEntity()));

        return patientProfileDto;
    }

    public String getFullName(UserEntity userEntity) {
        String name = "";
        if (!userEntity.getLastName().isEmpty()) {
            name += userEntity.getLastName();
        }
        if (!userEntity.getFirstName().isEmpty()) {
            name += " " + userEntity.getFirstName().charAt(0) + ".";
        }
        if (!userEntity.getMiddleName().isEmpty()) {
            name += " " + userEntity.getMiddleName().charAt(0) + ".";
        }
        return name;
    }

    public UserEntity getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName());
    }

    public String calculateBmx(Double weight, double height) {
        height /= 100;
        double result = weight / (height * height);
        return String.format("%.1f", result);
    }

    public int calculateAge(LocalDate dob) {
        return dob != null ? Period.between(dob, LocalDate.now()).getYears() : 0;
    }

    public List<EducationDto> getEducationTypes() {
        List<EducationDto> result = new ArrayList<>();

        Education[] educationEnums = Education.values();

        for (Education e : educationEnums) {
            result.add(new EducationDto(e.name(), e.getType()));
        }

        return result;
    }

    public List<MarriedDto> getMarriedTypes() {
        List<MarriedDto> result = new ArrayList<>();

        Married[] marriedEnums = Married.values();

        for (Married m : marriedEnums) {
            result.add(new MarriedDto(m.name(), m.getType()));
        }

        return result;
    }

}