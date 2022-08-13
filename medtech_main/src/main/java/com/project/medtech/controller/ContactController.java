package com.project.medtech.controller;

import com.project.medtech.dto.TextDto;
import com.project.medtech.dto.UpdateContactDto;
import com.project.medtech.service.ContactService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
@Api("REST APIs related to `Contact` Entity")
public class ContactController {

    private final ContactService contactService;


    @ApiOperation("получение листа всех контактов регистратуры")
    @GetMapping("/get-all")
    public ResponseEntity<List<String>> getContacts() {
        return ResponseEntity.ok(contactService.getContacts());
    }

    @ApiOperation("создание нового контакта регистратуры")
    @PostMapping("/create")
    public ResponseEntity<String> createContact(@RequestBody TextDto textDto) {
        return ResponseEntity.ok(contactService.createContact(textDto));
    }

    @ApiOperation("изменение определенного контакта регистратуры")
    @PutMapping("/update")
    public ResponseEntity<String> updateContact(@RequestBody UpdateContactDto updateContactDto) {
        return ResponseEntity.ok(contactService.updateContact(updateContactDto));
    }

    @ApiOperation("удаление определенного контакта регистратуры")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteContact(@RequestBody TextDto textDto) {
        return ResponseEntity.ok(contactService.deleteContact(textDto));
    }

}
