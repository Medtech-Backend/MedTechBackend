package com.project.medtech.exporter;

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

    private Patient patient;


    public MedCardExcelExporter(PregnancyRepository pregnancyRepository, Patient patient) {
        this.pregnancyRepository = pregnancyRepository;
        this.patient = patient;
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
        for(int i = 0; i < columns.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeDataRows() {
        User user = patient.getUser();

        Pregnancy pregnancy = pregnancyRepository.findById(patient.getCurrentPregnancyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pregnancy was not found with ID: " + patient.getCurrentPregnancyId()));

        Address address = patient.getAddress();

        Insurance insurance = patient.getInsurance();

        Doctor doctor = pregnancy.getDoctor();

        List<Appointment> appointments = pregnancy.getAppointments();

        List<String> appointmentResults = new ArrayList<>();

        String[] appointmentsOrdered =
                {
                        "blood_test_for_hemoglobin","blood_type_and_Rh_factor","urinalysis_for_protein",
                        "had_pretest_counseling_for_HIV", "agrees_on_testing","blood_test_for_hiv",
                        "blood_test_for_syphilis", "bacteriological_culture_of_urine",
                        "ultrasound_in_eighteen_week", "folic_acid","potassium_iodide"
                };

        for(String s: appointmentsOrdered) {
            for (Appointment a : appointments) {
                if(a.getAppointmentType().getName().equals(s)) {
                    appointmentResults.add(a.getResult());
                }
            }
        }

        ArrayList<Object> answers = new ArrayList<>();

        answers.add(pregnancy.getRegistrationDate());
        answers.add(user.getEmail());
        answers.add(user.getFirstName());
        answers.add(user.getLastName());
        answers.add(user.getMiddleName());
        answers.add(user.getPhoneNumber());
        answers.add(doctor.getUser().getLastName() + doctor.getUser().getFirstName() + doctor.getUser().getMiddleName());
        answers.add(patient.getBirthday());
        answers.add(patient.getAge());
        answers.add(patient.getPin());
        answers.add(patient.getCitizenship());
        answers.add(patient.getPatientCategory());
        answers.add(patient.getWorkPlace());
        answers.add(patient.getPosition());
        answers.add(patient.getWorkConditions());
        answers.add(patient.getWorksNow());
        answers.add(patient.getHusbandLastName());
        answers.add(patient.getHusbandFirstName());
        answers.add(patient.getHusbandMiddleName());
        answers.add(patient.getHusbandWorkPlace());
        answers.add(patient.getHusbandPosition());
        answers.add(patient.getHusbandPhoneNumber());
        answers.add(patient.getMarried());
        answers.add(patient.getEducation());
        answers.add(address.getPatientAddress());
        answers.add(address.getPhoneNumber());
        answers.add(address.getRelativeAddress());
        answers.add(address.getRelativePhoneNumber());
        answers.add(insurance.getTerritoryName());
        answers.add(insurance.getNumber());
        answers.add(pregnancy.getFirstVisitDate()); // Дата первого осмотра
        answers.add(pregnancy.getFirstVisitWeekOfPregnancy());
        answers.add(pregnancy.getFromAnotherMedOrganizationReason());
        answers.add(pregnancy.getNameOfAnotherMedOrganization());
        answers.add(pregnancy.getPregnancyNumber());
        answers.add(pregnancy.getChildbirthNumber());
        answers.add(pregnancy.getGestationalAgeByLastMenstruation());
        answers.add(pregnancy.getGestationalAgeByUltrasound());
        answers.add(pregnancy.getEstimatedDateOfBirth());
        answers.add(pregnancy.getLateRegistrationReason());
        answers.add(pregnancy.getVacationFromForPregnancy());
        answers.add(pregnancy.getVacationUntilForPregnancy());
        answers.add(pregnancy.getBloodType());
        answers.add(pregnancy.getRhFactorPregnant());
        answers.add(pregnancy.getRhFactorPartner());
        answers.add(pregnancy.getTiterRhFactorInTwentyEightMonth());
        answers.add(pregnancy.getBloodRw());
        answers.add(pregnancy.getBloodHiv());
        answers.add(pregnancy.getBloodHivPartner());
        answers.add(pregnancy.getFirstVisitComplaints());
        answers.add(pregnancy.getAllergicToDrugs());
        answers.add(pregnancy.getPastIllnessesAndSurgeries());
        answers.add(pregnancy.getFirstVisitGrowth());
        answers.add(pregnancy.getFirstVisitWeight());
        answers.add(pregnancy.getBodyMassIndex());
        answers.add(pregnancy.getSkinAndMucousMembranes());
        answers.add(pregnancy.getThyroid());
        answers.add(pregnancy.getMilkGlands());
        answers.add(pregnancy.getPeripheralLymphNodes());
        answers.add(pregnancy.getRespiratorySystem());
        answers.add(pregnancy.getCardiovascularSystem());
        answers.add(pregnancy.getArterialPressure());
        answers.add(pregnancy.getDigestiveSystem());
        answers.add(pregnancy.getUrinarySystem());
        answers.add(pregnancy.getEdema());
        answers.add(pregnancy.getBonePelvis());
        answers.add(pregnancy.getUterineFundusHeight());
        answers.add(pregnancy.getFetalHeartbeat());
        answers.add(pregnancy.getExternalGenitalia());
        answers.add(pregnancy.getExaminationOfCervixInMirrors());
        answers.add(pregnancy.getBimanualStudy());
        answers.add(pregnancy.getVaginalDischarge());
        answers.add(pregnancy.getProvisionalDiagnosis());
        answers.addAll(appointmentResults);

        Row row = sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeight(14);
        style.setFont(font);

        Cell cell;

        for(int i = 0; i < answers.size(); i++) {
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
