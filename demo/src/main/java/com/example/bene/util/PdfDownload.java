package com.example.bene.util;

import org.springframework.stereotype.Component;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Component
public class PdfDownload {


    public static HashMap<String, Object> buildPdfDocument(HashMap<String, Object> requestParam) {

        HashMap<String, Object> responseData = new HashMap<>();

        try {

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) requestParam.get("datalist");
            List<Map<String, Object>> columns = (List<Map<String, Object>>) requestParam.get("columns");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 40);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new FooterPageEvent());
            document.open();

            PdfPTable table = new PdfPTable(columns.size());
            table.setWidthPercentage(100);
            table.setWidths(calculateColumnWidths(columns, dataList));
            table.setHeaderRows(1);

            addHeader(table, columns);
            addRows(table, columns, dataList);

            document.add(table);
            document.close();

            responseData.put("fileName", LocalDateTime.now() + ".pdf");
            responseData.put("docContent", out.toByteArray());
            responseData.put("mimeType", "application/pdf");
            responseData.put("Status", "Success");

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

      return responseData;
    }


    private static class FooterPageEvent extends PdfPageEventHelper {

        private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase("Page " + writer.getPageNumber(), footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.getPageSize().getLeft() + document.getPageSize().getRight()) / 2,
                    document.getPageSize().getBottom() + 15, 0
            );
        }
    }

    private static void addHeader(PdfPTable table, List<Map<String, Object>> columns) {

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

        for (Map<String, Object> col : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(col.get("label")), headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setMinimumHeight(30f);
            cell.setPadding(6f);
            table.addCell(cell);
        }
    }

    private static void addRows(PdfPTable table, List<Map<String, Object>> columns, List<Map<String, Object>> dataList) {

        int serialNo = 1;

        for (Map<String, Object> bene : dataList) {
            for (Map<String, Object> column : columns) {

                String columnName = String.valueOf(column.get("name"));

                if ("SNO".equals(columnName)) {
                    table.addCell(String.valueOf(serialNo));
                    continue;
                }
                preprocessDatesAndStatus(bene, columnName);

                table.addCell(createCell(resolveBeneValue(bene, columnName)));
            }

            serialNo++;
        }
    }

    private static PdfPCell createCell(String value) {

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Chunk chunk = new Chunk(value, font);

        chunk.setSplitCharacter((start, current, end, cc, ck) -> cc[current] == ' ');

        PdfPCell cell = new PdfPCell(new Phrase(chunk));
        cell.setNoWrap(false);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setPadding(5f);
        return cell;
    }

    private static String resolveBeneValue(Map<String, Object> bene, String columnName) {

        if ("accountNumber".equalsIgnoreCase(columnName)) {
            String actualName = safeString(bene.get("beneActualName"));

            return !"NA".equalsIgnoreCase(actualName) ? actualName : safeString(bene.get("accountNickname"));
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


    private static float[] calculateColumnWidths(List<Map<String, Object>> columns, List<Map<String, Object>> dataList) {

        float[] widths = new float[columns.size()];

        for (int i = 0; i < columns.size(); i++) {

            String columnName = String.valueOf(columns.get(i).get("name"));
            String header = String.valueOf(columns.get(i).get("label"));

            if ("SNO".equalsIgnoreCase(columnName)) {
                widths[i] = 2f;
                continue;
            }
            if ("userCrn".equalsIgnoreCase(columnName)) {
                widths[i] = 3f;
                continue;
            }
            if ("accountNumber".equalsIgnoreCase(columnName)) {
                widths[i] = 4.7f;
                continue;
            }
            if ("createdDate".equalsIgnoreCase(columnName)) {
                widths[i] = 5f;
                continue;
            }
            if ("requestType".equalsIgnoreCase(columnName)) {
                widths[i] = 5.8f;
                continue;
            }
            int maxLength = header.length();
            for (Map<String, Object> row : dataList) {
                String value = resolveBeneValue(row, columnName);
                maxLength = Math.max(maxLength, value.length());
            }
            widths[i] = Math.max(3f, Math.min(maxLength / 2.0f, 8f));
        }

        return widths;
    }

}
