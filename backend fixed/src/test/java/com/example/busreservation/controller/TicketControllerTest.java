package com.example.busreservation.controller;

import com.example.busreservation.model.Booking;
import com.example.busreservation.security.SecurityConfig;
import com.example.busreservation.security.JwtAuthFilter;
import com.example.busreservation.service.TicketService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TicketController.class, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                SecurityConfig.class, JwtAuthFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

        @Autowired
        MockMvc mvc;

        @MockBean
        TicketService ticketService;

        @Test
        @WithMockUser(username = "owner@x.com", roles = { "USER" })
        void owner_can_download_pdf() throws Exception {
                Booking b = new Booking();
                b.setId(10L);

                Mockito.when(ticketService.getBookingOr404(10L)).thenReturn(b);
                Mockito.when(ticketService.isAdmin(Mockito.any())).thenReturn(false);
                Mockito.when(ticketService.isOwner(eq(b), Mockito.any())).thenReturn(true);
                Mockito.when(ticketService.downloadTicket(10L))
                                .thenReturn(ResponseEntity.ok("%PDF".getBytes()));

                mvc.perform(get("/api/v1/tickets/10/pdf"))
                                .andExpect(status().isOk())
                                .andExpect(content().bytes("%PDF".getBytes()));
        }

        @Test
        @WithMockUser(username = "stranger@x.com", roles = { "USER" })
        void stranger_gets_403() throws Exception {
                Booking b = new Booking();
                b.setId(11L);

                Mockito.when(ticketService.getBookingOr404(11L)).thenReturn(b);
                Mockito.when(ticketService.isAdmin(Mockito.any())).thenReturn(false);
                Mockito.when(ticketService.isOwner(eq(b), Mockito.any())).thenReturn(false);

                mvc.perform(get("/api/v1/tickets/11/pdf"))
                                .andExpect(status().isForbidden());
        }
}
