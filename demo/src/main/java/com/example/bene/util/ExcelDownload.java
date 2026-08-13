package com.example.bene.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExcelDownload {


    public static Map<String, Object> buildExcelDocument(HashMap<String, Object> params) throws Exception {
        HashMap<String, Object> responseData = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook()) {

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) params.get("datalist");
            List<Map<String, Object>> columns = (List<Map<String, Object>>) params.get("columns");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss.SSSSSS");
            String fileName = LocalDateTime.now().format(formatter);

            Sheet sheet = workbook.createSheet(fileName);
            sheet.setDefaultColumnWidth(30);

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);

            Row headerRow = sheet.createRow(0);
            setHeaderRow(columns, headerStyle, headerRow);

            int rowCount = 1;
            int serialNo = 1;

            for (Map<String, Object> bene : dataList) {

                Row row = sheet.createRow(rowCount++);
                int cellIndex = 0;

                for (Map<String, Object> column : columns) {

                    String columnName = String.valueOf(column.get("name"));
                    Cell cell = row.createCell(cellIndex++);

                    if ("SNO".equals(columnName)) {
                        cell.setCellValue(serialNo);
                        continue;
                    }

                    if("PendingWith".equals(columnName)) {
                        cell.setCellValue("Authorizer");
                        continue;
                    }

                    preprocessDatesAndStatus(bene, columnName);
                    cell.setCellValue(resolveCellValue(bene, columnName));
                }
                serialNo++;
            }

            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            responseData.put("fileName", fileName + ".xlsx");
            responseData.put("docContent", out.toByteArray());
            responseData.put("Status", "Success");
            responseData.put("mimeType", "application/vnd.ms-excel");

        } catch (Exception e) {
          throw new RuntimeException(e);
        }

        return responseData;


    }

    private static String resolveCellValue(Map<String, Object> bene, String columnName) {

            if ("accountNumber".equalsIgnoreCase(columnName) || "IFSC".equalsIgnoreCase(columnName)) {

                Object accountObj = bene.get("account");
                if (accountObj instanceof List<?> accountList && !accountList.isEmpty()) {
                    Object first = accountList.get(0);

                    if (first instanceof Map<?, ?> account) {

                        if ("accountNumber".equalsIgnoreCase(columnName)) {
                            return String.valueOf(account.get("accountNumber"));
                        }

                        if ("IFSC".equalsIgnoreCase(columnName)) {
                            return String.valueOf(account.get("ifsc"));
                        }
                    }
                }
            }
            return safeString(bene.get(columnName));
    }


    private static String safeString(Object value) {
        if (value == null) {
            return "NA";
        }

        String str = String.valueOf(value).trim();
        return str.isEmpty() ? "NA" : str;
    }


    private static CellStyle createHeaderStyle(Workbook workbook) throws Exception {

        byte[] rgb = Hex.decodeHex("6088CB");
        XSSFColor color = new XSSFColor(rgb, new DefaultIndexedColorMap());

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibri");

        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    private static void setHeaderRow(List<Map<String, Object>> columns, CellStyle style, Row header) {

        int index = 0;
        for (Map<String, Object> column : columns) {
            Cell cell = header.createCell(index++);
            cell.setCellValue(String.valueOf(column.get("label")));
            cell.setCellStyle(style);
        }
    }

    private static void preprocessDatesAndStatus(Map<String, Object> bene, String columnName) {

        if (Set.of("createdDate","lastupdated").contains(columnName)) {
            bene.put(columnName, formatDateTime(MapUtils.getString(bene, columnName)));
        }
    }

    public static String formatDateTime(String input) {

        if (input == null || input.isBlank()) {
            return "NA";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            return LocalDate.parse(input, formatter).format(formatter);
        } catch (Exception e) {
            return "NA";
        }
    }
}
