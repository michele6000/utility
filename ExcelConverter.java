import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Generic converter from iterable list of object to Excel Sheet
// First Row of the file is Header, with custom style
// Support for date recognition

public class ExcelConverter {
    public static ByteArrayInputStream iterableToExcel(List<Object> iterable) {
        try {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Foglio 1");
            CreationHelper createHelper = workbook.getCreationHelper();

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setItalic(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            cellStyle.setFont(headerFont);

            List<String> HEADERs = Arrays.stream(iterable.get(0).getClass().getDeclaredFields()).map(Field::getName).toList();

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.size(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(HEADERs.get(col));
            }
            int rowIdx = 1;

            cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy hh:mm"));

            for (Object item : iterable) {
                Row row = sheet.createRow(rowIdx++);
                AtomicInteger cnt = new AtomicInteger();
                CellStyle finalCellStyle = cellStyle;
                Arrays.stream(iterable.get(0).getClass().getDeclaredFields()).forEach(field -> {
                    field.setAccessible(true);
                    try {
                        Cell cell = row.createCell(cnt.getAndIncrement());
                        if (field.get(item) != null)
                            cell.setCellValue(field.get(item).toString());
                        else
                            cell.setCellValue("");
                        if (field.getType().equals(Timestamp.class)) {
                            cell.setCellValue((Timestamp) field.get(item));
                            cell.setCellStyle(finalCellStyle);
                        }
                    } catch (Exception e) {
                        System.out.println("ERR:621218");
                        System.out.println(e.getMessage());
                    }
                });
            }

            AtomicInteger cnt = new AtomicInteger();
            Arrays.stream(iterable.get(0).getClass().getDeclaredFields()).forEach(i -> {
                sheet.autoSizeColumn(cnt.getAndIncrement());
            });

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}