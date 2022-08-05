package com.project.medtech.dto.enums;

import java.util.Arrays;
import java.util.List;

public enum AppointmentEnum {

    BLOOD_TEST_FOR_HEMOGLOBIN("blood_test_for_hemoglobin"), // "Анализ крови на гемоглобин"
    BLOOD_TYPE_AND_RH_FACTOR("blood_type_and_Rh_factor"), // "Группа крови и резус фактор"
    URINALYSIS_FOR_PROTEIN("urinalysis_for_protein"), // "Анализ мочи на белок"
    HAD_PRETEST_COUNSELING_FOR_HIV("had_pretest_counseling_for_HIV"), // "Проведено дотестовое консультирование по ВИЧ"
    AGREES_ON_TESTING("agrees_on_testing"), // "Согласна на тестирование"
    BLOOD_TEST_FOR_HIV("blood_test_for_hiv"), // "Анализ крови на ВИЧ"
    BLOOD_TEST_FOR_SYPHILIS("blood_test_for_syphilis"), // "Анализ крови на сифилис"
    BACTERIOLOGICAL_CULTURE_OF_URINE("bacteriological_culture_of_urine"), // "Бактериологический посев мочи"
    ULTRASOUND_IN_EIGHTEEN_WEEK("ultrasound_in_eighteen_week"), // "УЗИ (в 18 недель)"
    FOLIC_ACID("folic_acid"), // "Фолиевая кислота"
    POTASSIUM_IODIDE("potassium_iodide"); // "Калия йодид"

    private final String name;

    AppointmentEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<AppointmentEnum> getAppointments() {
        return Arrays.asList(AppointmentEnum.values());
    }

}
