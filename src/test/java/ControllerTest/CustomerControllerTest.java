package ControllerTest;

import cg.wbd.grandemonstration.controller.CustomerController;
import cg.wbd.grandemonstration.model.Customer;
import cg.wbd.grandemonstration.service.CustomerService;
import cg.wbd.grandemonstration.service.ProvinceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@SpringJUnitJupiterConfig(CustomerControllerTestConfig.class)
public class CustomerControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CustomerService customerService;

    @InjectMocks // thuọc depen testCompile group: 'org.mockito', name: 'mockito-all', version: '1.10.19'
    private CustomerController customerController;

    @BeforeEach // trước mỗi testcase @Test sẽ reset lại từ đầu
    void setUp() { // cài đặt giả lập tạo đối tượng Mock
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void customersListPageIsExists() throws Exception {
        mockMvc
                .perform(get("/customers")) // thực thi hàm get và mong muốn trả lại static
                .andExpect(status().isOk());
    }

    @Test
    void customerBrowseControlling() throws Exception {
        mockMvc
                .perform(get("/customers"))
                .andExpect(status().is(200))
                .andExpect(view().name("customers/browse"));
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
}