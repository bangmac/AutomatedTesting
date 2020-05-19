package cg.wbd.grandemonstration.controller;

import cg.wbd.grandemonstration.model.Customer;
import cg.wbd.grandemonstration.model.Province;
import cg.wbd.grandemonstration.service.CustomerService;
import cg.wbd.grandemonstration.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Controller
@RequestMapping("customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProvinceService provinceService;

    @ModelAttribute("provinces")
    public Iterable<Province> allProvinces() {
        return provinceService.findAll();
    }

    @GetMapping
    public ModelAndView showList(Optional<String> s, Pageable pageInfo) {
        ModelAndView modelAndView = new ModelAndView("customers/list");
        Page<Customer> customers = s.isPresent() ? search(s, pageInfo) : getPage(pageInfo);
        modelAndView.addObject("keyword", s.orElse(null));
        modelAndView.addObject("customers", customers);
        return modelAndView;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Page<Customer>> apiList(Pageable pageInfo) {
        Page<Customer> customers = customerService.findAll(pageInfo);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ModelAndView showInformation(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("customers/info");
        Customer customer = customerService.findOne(id);
        modelAndView.addObject("customer", customer);
        return modelAndView;
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Customer> apiInformation(@PathVariable Long id) {
        Customer customer = customerService.findOne(id);
        HttpStatus status = customer == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(customer, status);
    }

    @PostMapping
    public ModelAndView updateCustomer(@Validated @ModelAttribute Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasGlobalErrors() || bindingResult.hasFieldErrors()) {
            return new ModelAndView("customers/info");
        }
        customerService.save(customer);
        return new ModelAndView("redirect:/customers");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> apiCreateCustomer(@Validated @RequestBody Customer customer,
                                                  BindingResult bindingResult,
                                                  UriComponentsBuilder ucBuilder) {
        if (bindingResult.hasGlobalErrors() || bindingResult.hasFieldErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        customerService.save(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/customers/{id}").buildAndExpand(customer.getId()).toUri());
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(headers, HttpStatus.CREATED);
        return responseEntity;
    }

    @PutMapping(value = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> apiUpdateCustomer(@PathVariable("id") long id,
                                                      @RequestBody Customer customer) {
        Customer originCustomer = customerService.findOne(id);

        if (originCustomer == null) {
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        originCustomer.setName(customer.getName());
        originCustomer.setEmail(customer.getEmail());
        originCustomer.setAddress(customer.getAddress());

        if (customer.getProvince() != null) {
            Province originProvince = provinceService.findOne(customer.getProvince().getId());
            if (originProvince == null) {
                return new ResponseEntity<Customer>(HttpStatus.BAD_REQUEST);
            }
            originCustomer.setProvince(originProvince);
        }

        customerService.save(originCustomer);
        return new ResponseEntity<Customer>(originCustomer, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @ResponseBody
    public ResponseEntity<Void> apiDeleteCustomer(@PathVariable Long id) {
        Customer target = customerService.findOne(id);

        if (target == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        customerService.delete(target);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Page<Customer> getPage(Pageable pageInfo) {
        return customerService.findAll(pageInfo);
    }

    private Page<Customer> search(Optional<String> s, Pageable pageInfo) {
        return customerService.search(s.get(), pageInfo);
    }
}
