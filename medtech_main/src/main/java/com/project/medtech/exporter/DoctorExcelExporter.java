package com.project.medtech.exporter;

import com.project.medtech.model.DoctorEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.service.DoctorService;
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

public class DoctorExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<UserEntity> users;
    private final DoctorService doctorService;

    public DoctorExcelExporter(List<UserEntity> users, DoctorService doctorService) {
        this.users = users;
        this.doctorService = doctorService;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Doctors");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "№", style);
        createCell(row, 1, "ФИО пациента", style);
        createCell(row, 2, "Номер телефона", style);
        createCell(row, 3, "Электронная почта", style);
        createCell(row, 4, "Пациенты", style);
        createCell(row, 5, "График работы", style);
        createCell(row, 6, "Статус", style);

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

        for (UserEntity u : users) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            DoctorEntity doctor = u.getDoctorEntity();

            createCell(row, columnCount++, num, style);
            createCell(row, columnCount++, u.getFirstName()+" "+u.getLastName()+" "+u.getMiddleName(), style);
            createCell(row, columnCount++, u.getPhoneNumber(), style);
            createCell(row, columnCount++, u.getEmail(), style);
            createCell(row, columnCount++, doctorService.getNumberOfPatients(u.getEmail()) + " пациентов", style);
            createCell(row, columnCount++,  doctor.getId(), style); //график работы
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