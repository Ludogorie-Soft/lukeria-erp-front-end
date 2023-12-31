package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/client")
public class ClientController {
    private static final String CARTONTXT = "client";
    private static final String SESSION_TOKEN = "sessionToken";

    private static final String REDIRECTTXT = "redirect:/client/show";
    private final ClientClient clientClient;

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<ClientDTO> clients = clientClient.getAllClients(token);
        model.addAttribute("deleteMessageBoolean", true);

        model.addAttribute("clients", clients);
        return "Client/show";
    }

    @GetMapping("/show/{id}")
    String getClientById(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ClientDTO client = clientClient.getClientById(id, token);
        model.addAttribute(CARTONTXT, client);
        return "Client/showById";
    }


    @GetMapping("/create")
    String createClient(Model model) {
        ClientDTO client = new ClientDTO();
        model.addAttribute(CARTONTXT, client);
        return "Client/create";
    }

    @GetMapping("/editClient/{id}")
    String editClient(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ClientDTO existingClient = clientClient.getClientById(id, token);
        model.addAttribute(CARTONTXT, existingClient);
        return "Client/edit";
    }

    @PostMapping("/submit")
    public ModelAndView submitClient(@ModelAttribute("client") ClientDTO clientDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        if (Objects.equals(clientDTO.getBusinessName(), "")) {
            clientDTO.setBusinessName(clientDTO.getEnglishBusinessName());
            clientDTO.setAddress(clientDTO.getEnglishAddress());
            clientDTO.setMol(clientDTO.getEnglishMol());
        } else {
            clientDTO.setHasIdNumDDS(true);
        }
        clientClient.createClient(clientDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/{id}")
    ModelAndView editClient(@PathVariable(name = "id") Long id, ClientDTO clientDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientClient.updateClient(id, clientDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteClientById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientClient.deleteClientById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

}

