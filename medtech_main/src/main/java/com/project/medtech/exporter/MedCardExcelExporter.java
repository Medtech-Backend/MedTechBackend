package com.project.medtech.exporter;

import com.project.medtech.dto.enums.AppointmentEnum;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.*;
import com.project.medtech.repository.PregnancyRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MedCardExcelExporter {

    private final PregnancyRepository pregnancyRepository;

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private PatientEntity patientEntity;


    public MedCardExcelExporter(PregnancyRepository pregnancyRepository, PatientEntity patientEntity) {
        this.pregnancyRepository = pregnancyRepository;
        this.patientEntity = patientEntity;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Медицинская карта");
    }

    private void writeHeaderRow() {
        String[] columns =
                {
                        "Дата взятия на учет", "Почта", "Имя", "Фамилия", "Отчество", "Номер телефона", "Гинеколог", "Дата рождения",
                        "Возраст", "ИНН", "Гражданство", "Категория пациента", "Место работы", "Должность",
                        "Условия труда", "Работает в данное время", "Имя мужа", "Фамилия мужа",
                        "Отчество мужа", "Место работы мужа", "Должность мужа", "Номер телефона мужа",
                        "Состоит в браке", "Образование", "Постоянное место жительства", "Номер телефона",
                        "Адрес родственников", "Номер телефона родственников", "Территория страхования",
                        "Номер удостоверения соц. защиты", "Дата первого осмотра",
                        "Неделя беременности на первом осмотре", "Прибыла из другой мед. организации (причина)",
                        "Название старой мед. организации", "Беременность (которая)", "Роды (которые)",
                        "Срок беременности по последним месячным (нед.)", "Срок беременности по последнему УЗИ (нед.)",
                        "Предполагаемая дата родов", "Если взята на учет в сроке беременности свыше 12-недель (указать причины)",
                        "Дан отпуск по беременности с", "Дан отпуск по беременности по", "Группа крови",
                        "Резус-принадлежность беременной", "Резус-принадлежность партнера/мужа",
                        "Титр резус-антител в 28 нед. беременности", "Кровь на RW", "Кровь на ВИЧ",
                        "Кровь на ВИЧ партнера", "Жалобы при первичном осмотре", "Аллергия на препараты",
                        "Перенесенные заболевания и операции", "Рост на первом осмотре", "Вес на первом осмотре", "ИМТ",
                        "Кожные покровы и слизистые", "Щитовидная железа", "Молочные железы",
                        "Периферические лимфатические узлы", "Дыхательная система",
                        "Сердечно-сосудистая система", "Артериальное давление", "Пищеварительная система",
                        "Мочевыделительная система", "Отеки", "Костный таз", "Высота дна матки (см.)",
                        "Сердцебиение плода", "Наружные половые органы", "Осмотр шейки матки в зеркалах",
                        "Бимануальное исследование", "Выделения из влагалища", "Предварительный диагноз",
                        "Анализ крови на гемоглобин", "Группа крови и резус фактор", "Анализ мочи на белок",
                        "Проведено дотестовое консультирование по ВИЧ", "Согласна на тестирование", "Анализ крови на ВИЧ",
                        "Анализ крови на сифилис", "Бактериологический посев мочи", "УЗИ (в 18 недель)",
                        "Фолиевая кислота", "Калия йодид"
                };

        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        Cell cell;
        for (int i = 0; i < columns.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeDataRows() {
        UserEntity userEntity = patientEntity.getUserEntity();

        PregnancyEntity pregnancyEntity = pregnancyRepository.findById(patientEntity.getCurrentPregnancyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pregnancy was not found with ID: " + patientEntity.getCurrentPregnancyId()));

        AddressEntity addressEntity = patientEntity.getAddressEntity();

        InsuranceEntity insuranceEntity = patientEntity.getInsuranceEntity();

        DoctorEntity doctorEntity = pregnancyEntity.getDoctorEntity();

        List<AppointmentEntity> appointmentEntities = pregnancyEntity.getAppointmentEntities();

        List<String> appointmentResults = new ArrayList<>();

        List<AppointmentEnum> appointmentEnums = AppointmentEnum.getAppointments();

        for (AppointmentEnum s : appointmentEnums) {
            for (AppointmentEntity a : appointmentEntities) {
                if (a.getAppointmentTypeEntity().getName().equals(s.name())) {
                    appointmentResults.add(a.getResult());
                }
            }
        }

        ArrayList<Object> answers = new ArrayList<>();

        answers.add(pregnancyEntity.getRegistrationDate());
        answers.add(userEntity.getEmail());
        answers.add(userEntity.getFirstName());
        answers.add(userEntity.getLastName());
        answers.add(userEntity.getMiddleName());
        answers.add(userEntity.getPhoneNumber());
        answers.add(doctorEntity.getUserEntity().getLastName() + doctorEntity.getUserEntity().getFirstName() + doctorEntity.getUserEntity().getMiddleName());
        answers.add(patientEntity.getBirthday());
        answers.add(patientEntity.getAge());
        answers.add(patientEntity.getPin());
        answers.add(patientEntity.getCitizenship());
        answers.add(patientEntity.getPatientCategory());
        answers.add(patientEntity.getWorkPlace());
        answers.add(patientEntity.getPosition());
        answers.add(patientEntity.getWorkConditions());
        answers.add(patientEntity.getWorksNow());
        answers.add(patientEntity.getHusbandLastName());
        answers.add(patientEntity.getHusbandFirstName());
        answers.add(patientEntity.getHusbandMiddleName());
        answers.add(patientEntity.getHusbandWorkPlace());
        answers.add(patientEntity.getHusbandPosition());
        answers.add(patientEntity.getHusbandPhoneNumber());
        answers.add(patientEntity.getMarried());
        answers.add(patientEntity.getEducation());
        answers.add(addressEntity.getPatientAddress());
        answers.add(addressEntity.getPhoneNumber());
        answers.add(addressEntity.getRelativeAddress());
        answers.add(addressEntity.getRelativePhoneNumber());
        answers.add(insuranceEntity.getTerritoryName());
        answers.add(insuranceEntity.getNumber());
        answers.add(pregnancyEntity.getFirstVisitDate()); // Дата первого осмотра
        answers.add(pregnancyEntity.getFirstVisitWeekOfPregnancy());
        answers.add(pregnancyEntity.getFromAnotherMedOrganizationReason());
        answers.add(pregnancyEntity.getNameOfAnotherMedOrganization());
        answers.add(pregnancyEntity.getPregnancyNumber());
        answers.add(pregnancyEntity.getChildbirthNumber());
        answers.add(pregnancyEntity.getGestationalAgeByLastMenstruation());
        answers.add(pregnancyEntity.getGestationalAgeByUltrasound());
        answers.add(pregnancyEntity.getEstimatedDateOfBirth());
        answers.add(pregnancyEntity.getLateRegistrationReason());
        answers.add(pregnancyEntity.getVacationFromForPregnancy());
        answers.add(pregnancyEntity.getVacationUntilForPregnancy());
        answers.add(pregnancyEntity.getBloodType());
        answers.add(pregnancyEntity.getRhFactorPregnant());
        answers.add(pregnancyEntity.getRhFactorPartner());
        answers.add(pregnancyEntity.getTiterRhFactorInTwentyEightMonth());
        answers.add(pregnancyEntity.getBloodRw());
        answers.add(pregnancyEntity.getBloodHiv());
        answers.add(pregnancyEntity.getBloodHivPartner());
        answers.add(pregnancyEntity.getFirstVisitComplaints());
        answers.add(pregnancyEntity.getAllergicToDrugs());
        answers.add(pregnancyEntity.getPastIllnessesAndSurgeries());
        answers.add(pregnancyEntity.getFirstVisitGrowth());
        answers.add(pregnancyEntity.getFirstVisitWeight());
        answers.add(pregnancyEntity.getBodyMassIndex());
        answers.add(pregnancyEntity.getSkinAndMucousMembranes());
        answers.add(pregnancyEntity.getThyroid());
        answers.add(pregnancyEntity.getMilkGlands());
        answers.add(pregnancyEntity.getPeripheralLymphNodes());
        answers.add(pregnancyEntity.getRespiratorySystem());
        answers.add(pregnancyEntity.getCardiovascularSystem());
        answers.add(pregnancyEntity.getArterialPressure());
        answers.add(pregnancyEntity.getDigestiveSystem());
        answers.add(pregnancyEntity.getUrinarySystem());
        answers.add(pregnancyEntity.getEdema());
        answers.add(pregnancyEntity.getBonePelvis());
        answers.add(pregnancyEntity.getUterineFundusHeight());
        answers.add(pregnancyEntity.getFetalHeartbeat());
        answers.add(pregnancyEntity.getExternalGenitalia());
        answers.add(pregnancyEntity.getExaminationOfCervixInMirrors());
        answers.add(pregnancyEntity.getBimanualStudy());
        answers.add(pregnancyEntity.getVaginalDischarge());
        answers.add(pregnancyEntity.getProvisionalDiagnosis());
        answers.addAll(appointmentResults);

        Row row = sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeight(14);
        style.setFont(font);

        Cell cell;

        for (int i = 0; i < answers.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(String.valueOf(answers.get(i)));
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
        }

    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow();
        writeDataRows();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
