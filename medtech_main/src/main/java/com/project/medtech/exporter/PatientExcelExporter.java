package com.project.medtech.exporter;

import com.project.medtech.dto.RequestPatient;
import com.project.medtech.model.Address;
import com.project.medtech.model.Patient;
import com.project.medtech.model.User;
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
    private List<User> users;

    private final PatientService patientService;


    public PatientExcelExporter(List<User> users, PatientService patientService) {
        this.users = users;
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
        createCell(row, 7, "Город", style);
        createCell(row, 8, "Район", style);
        createCell(row, 9, "Улица", style);
        createCell(row, 10, "Дом", style);
        createCell(row, 11, "Статус", style);

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

        for (User u : users) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            Patient patient = u.getPatient();
            Address address = patient.getAddress();

            createCell(row, columnCount++, num, style);
            createCell(row, columnCount++, u.getFirstName(), style);
            createCell(row, columnCount++, u.getLastName(), style);
            createCell(row, columnCount++, u.getMiddleName(), style);
            createCell(row, columnCount++, u.getPhoneNumber(), style);
            createCell(row, columnCount++, u.getEmail(), style);
            createCell(row, columnCount++, patientService.getCurrentWeekOfPregnancy(new RequestPatient(u.getUserId())), style);
            createCell(row, columnCount++,  address.getCity(), style);
            createCell(row, columnCount++, address.getVillage(), style);
            createCell(row, columnCount++, address.getStreetName(), style);
            createCell(row, columnCount++, address.getHouseNumber(), style);
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
