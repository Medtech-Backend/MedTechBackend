package com.project.medtech.dto.enums;

import java.util.Arrays;
import java.util.List;

public enum AppointmentEnum {

    BLOOD_TEST_FOR_HEMOGLOBIN, // "Анализ крови на гемоглобин"
    BLOOD_TYPE_AND_RH_FACTOR, // "Группа крови и резус фактор"
    URINALYSIS_FOR_PROTEIN, // "Анализ мочи на белок"
    HAD_PRETEST_COUNSELING_FOR_HIV, // "Проведено дотестовое консультирование по ВИЧ"
    AGREES_ON_TESTING, // "Согласна на тестирование"
    BLOOD_TEST_FOR_HIV, // "Анализ крови на ВИЧ"
    BLOOD_TEST_FOR_SYPHILIS, // "Анализ крови на сифилис"
    BACTERIOLOGICAL_CULTURE_OF_URINE, // "Бактериологический посев мочи"
    ULTRASOUND_IN_EIGHTEEN_WEEK, // "УЗИ (в 18 недель)"
    FOLIC_ACID, // "Фолиевая кислота"
    POTASSIUM_IODIDE; // "Калия йодид"


    public static List<AppointmentEnum> getAppointments() {
        return Arrays.asList(AppointmentEnum.values());
    }

}
