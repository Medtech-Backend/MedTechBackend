package com.project.medtech.exporter;

import com.project.medtech.model.AddressEntity;
import com.project.medtech.model.PatientEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.service.PatientService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class PatientExcelExporter {

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private List<UserEntity> userEntities;

    private final PatientService patientService;


    public PatientExcelExporter(List<UserEntity> userEntities, PatientService patientService) {
        this.userEntities = userEntities;
        this.patientService = patientService;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Patients");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "№", style);
        createCell(row, 1, "Фамилия", style);
        createCell(row, 2, "Имя", style);
        createCell(row, 3, "Отчество", style);
        createCell(row, 4, "Номер телефона", style);
        createCell(row, 5, "Электронная почта", style);
        createCell(row, 6, "Срок бер-ти", style);
        createCell(row, 7, "Адрес прописки", style);
        createCell(row, 8, "Статус", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        int num = 1;

        for (UserEntity u : userEntities) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            PatientEntity patientEntity = u.getPatientEntity();
            AddressEntity addressEntity = patientEntity.getAddressEntity();

            createCell(row, columnCount++, num, style);
            createCell(row, columnCount++, u.getFirstName(), style);
            createCell(row, columnCount++, u.getLastName(), style);
            createCell(row, columnCount++, u.getMiddleName(), style);
            createCell(row, columnCount++, u.getPhoneNumber(), style);
            createCell(row, columnCount++, u.getEmail(), style);
            createCell(row, columnCount++, patientService.calculateCurrentWeekOfPregnancy(u.getEmail()), style);
            createCell(row, columnCount++,  addressEntity.getRelativeAddress(), style);
            createCell(row, columnCount++, u.getStatus().toString(), style);

            num++;
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();


    }

}
