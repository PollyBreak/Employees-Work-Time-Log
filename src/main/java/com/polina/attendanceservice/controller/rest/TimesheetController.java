package com.polina.attendanceservice.controller.rest;

import com.polina.attendanceservice.dto.MonthlyTimesheetRow;
import com.polina.attendanceservice.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.*;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class TimesheetController {

    private final TimesheetService timesheetService;

    @GetMapping("/{id}/timesheet")
    public void exportTimesheet(
            @PathVariable Long id,
            @RequestParam int year,
            @RequestParam int month,
            HttpServletResponse response
    ) throws IOException {
        YearMonth ym = YearMonth.of(year, month);
        List<MonthlyTimesheetRow> report = timesheetService.generateMonthlyReport(id, ym);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Timesheet");

        int dayCount = ym.lengthOfMonth();

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(5).setCellValue(ym.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")));
        titleRow.createCell(6).setCellValue(ym.getYear());

        Row headerDate = sheet.createRow(1); // Числа
        Row headerDay = sheet.createRow(2);  // Дни недели

        headerDate.createCell(0).setCellValue("ФИО");
        headerDay.createCell(0).setCellValue("");

        headerDate.createCell(1).setCellValue("Должность");
        headerDay.createCell(1).setCellValue("");

        for (int d = 1; d <= dayCount; d++) {
            LocalDate date = ym.atDay(d);
            DayOfWeek dow = date.getDayOfWeek();

            String shortDay = switch (dow) {
                case MONDAY -> "Пн";
                case TUESDAY -> "Вт";
                case WEDNESDAY -> "Ср";
                case THURSDAY -> "Чт";
                case FRIDAY -> "Пт";
                case SATURDAY -> "Сб";
                case SUNDAY -> "Вс";
            };

            headerDate.createCell(1 + d).setCellValue(d);
            headerDay.createCell(1 + d).setCellValue(shortDay);
        }

        headerDate.createCell(2 + dayCount).setCellValue("Итого");
        headerDay.createCell(2 + dayCount).setCellValue("часы");


        int rowIndex = 3;
        for (MonthlyTimesheetRow row : report) {
            Row r = sheet.createRow(rowIndex++);
            r.createCell(0).setCellValue(row.getSurname() + " " + row.getName());
            r.createCell(1).setCellValue(row.getPosition() == null ? "" : row.getPosition());

            for (int d = 1; d <= dayCount; d++) {
                LocalDate date = ym.atDay(d);
                Duration dur = row.getWorkedHoursPerDay().get(date);
                String formatted = "";

                if (dur != null && dur.equals(Duration.ofHours(8))) {
                    formatted = "К"; // Командировка
                } else if (dur != null && !dur.isZero()) {
                    formatted = formatDuration(dur);
                }

                r.createCell(1 + d).setCellValue(formatted);
            }

            r.createCell(2 + dayCount).setCellValue(formatDuration(row.getTotalWorked()));
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = String.format("Timesheet_%s.xlsx", ym);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/{id}/timesheet/json")
    public ResponseEntity<List<MonthlyTimesheetRow>> getTimesheetAsJson(
            @PathVariable Long id,
            @RequestParam int year,
            @RequestParam int month
    ) {
        YearMonth ym = YearMonth.of(year, month);
        List<MonthlyTimesheetRow> report = timesheetService.generateMonthlyReport(id, ym);
        return ResponseEntity.ok(report);
    }

    private String formatDuration(Duration duration) {
        if (duration == null || duration.isZero()) return "";
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }
}
