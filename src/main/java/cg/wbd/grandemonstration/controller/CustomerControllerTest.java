package cg.wbd.grandemonstration.controller;

import cg.wbd.grandemonstration.model.Customer;
import cg.wbd.grandemonstration.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@SpringJUnitJupiterConfig(CustomerControllerTestConfig.class)
public class CustomerControllerTest {

    @Autowired
    private CustomerService customerService;

    private MockMvc mockMvc;

    @InjectMocks
    private CustomerController customerController;
    private CustomerService customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void customersListPageIsExists() throws Exception {
        mockMvc
                .perform(get("/customers"))
//                .andExpect(status().is(200));
                .andExpect(status().isOk());

    }

    @Test
    void customerUpdateSuccessControlling() throws Exception {
        Customer foo = new Customer(1L, "Foo Bar", "a@dummy.im", "Nowhere");
        when(customerService.save(isA(Customer.class))).thenReturn(foo);

        mockMvc
                .perform(post("/customers")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", foo.getId().toString())
                        .param("name", foo.getName())
                        .param("email", foo.getEmail())
                        .param("address", foo.getAddress()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/customers"));
    }

    @Test
    void testFindAll() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1L, "Foo Bar", "a@dummy.im", "Nowhere"));
        Pageable pageInfo = new PageRequest(0, 25);
        Page<Customer> customerPage = new PageImpl<Customer>(customers, pageInfo, 1);
        when(customerRepository.findAll(pageInfo)).thenReturn(customerPage);

        Page<Customer> actual = customerService.findAll(pageInfo);
        verify(customerRepository).findAll(pageInfo);
        assertEquals(customerPage, actual);
    }

    @Test
    void testFindOneFound() {
        Customer customer = new Customer(1L, "Foo Bar", "a@dummy.im", "Nowhere");
        when(customerRepository.findOne(1L)).thenReturn(customer);

        Customer actual = customerService.findOne(1L);
        verify(customerRepository).findOne(1L);
        assertEquals(customer, actual);
    }

    @Test
    void testFindOneNotFound() {
        when(customerRepository.findOne(1L)).thenReturn(null);

        Customer actual = customerService.findOne(1L);
        verify(customerRepository).findOne(1L);
        assertNull(actual);
    }

    @Test
    void saveCustomer() {
        Customer customer = new Customer(1L, "Foo Bar", "a@dummy.im", "Nowhere");
        customerService.save(customer);
        verify(customerRepository).save(customer);
    }
}