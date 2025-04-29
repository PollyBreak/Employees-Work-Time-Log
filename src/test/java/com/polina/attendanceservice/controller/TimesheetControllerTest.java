package com.polina.attendanceservice.controller;


import com.polina.attendanceservice.controller.rest.TimesheetController;
import com.polina.attendanceservice.dto.MonthlyTimesheetRow;
import com.polina.attendanceservice.service.TimesheetService;
import jakarta.servlet.WriteListener;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.*;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimesheetController.class)
class TimesheetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimesheetService timesheetService;

    private List<MonthlyTimesheetRow> mockReport;

    @BeforeEach
    void setup() {
        Map<LocalDate, Duration> worked = new HashMap<>();
        worked.put(LocalDate.of(2024, 3, 1), Duration.ofHours(8));
        worked.put(LocalDate.of(2024, 3, 2), Duration.ofMinutes(150)); // 2h 30m

        mockReport = List.of(new MonthlyTimesheetRow(
                1L, "John", "Doe", "Dev", "B2", worked, Duration.ofMinutes(590)
        ));
    }

    @Test
    void getTimesheetAsJson_shouldReturnReport() throws Exception {
        when(timesheetService.generateMonthlyReport(5L, YearMonth.of(2024, 3)))
                .thenReturn(mockReport);

        mockMvc.perform(get("/api/company/5/timesheet/json")
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"))
                .andExpect(jsonPath("$[0].position").value("Dev"))
                .andExpect(jsonPath("$[0].room").value("B2"));

        verify(timesheetService).generateMonthlyReport(5L, YearMonth.of(2024, 3));
    }

    @Test
    void exportTimesheet_shouldWriteExcelFile() throws Exception {
        TimesheetController controller = new TimesheetController(timesheetService);

        when(timesheetService.generateMonthlyReport(5L, YearMonth.of(2024, 3)))
                .thenReturn(mockReport);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream servletOut = new ServletOutputStream() {
            @Override
            public void write(int b) {
                outStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }
        };

        when(response.getOutputStream()).thenReturn(servletOut);

        controller.exportTimesheet(5L, 2024, 3, response);

        verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        verify(response).setHeader(eq("Content-Disposition"), contains("Timesheet_2024-03.xlsx"));
        assertThat(outStream.size()).isGreaterThan(0); // ensure content was written

        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(outStream.toByteArray()))) {
            var sheet = workbook.getSheet("Timesheet");
            assertThat(sheet).isNotNull();
            assertThat(sheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("март");
        }
    }

}
